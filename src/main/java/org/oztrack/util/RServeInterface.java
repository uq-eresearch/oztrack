package org.oztrack.util;

import static org.oztrack.util.StartRserve.checkLocalRserve;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.error.RServeInterfaceException;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RServeInterface {
    protected final Log logger = LogFactory.getLog(getClass());

    private RConnection rConnection;
    private String rWorkingDir;
    private final SearchQuery searchQuery;
    private final String srs;
    private String rLog = "";

    // TODO: DAO should not appear in this layer.
    private PositionFixDao positionFixDao;

    public RServeInterface(SearchQuery searchQuery, PositionFixDao positionFixDao) {
        this.searchQuery = searchQuery;
        this.srs = (searchQuery.getSrs() != null) ? searchQuery.getSrs().toLowerCase(Locale.ENGLISH) : "epsg:3577";
        this.positionFixDao = positionFixDao;
    }

    public File createKml() throws RServeInterfaceException {
        startRConnection();

        // create a temporary file name
        Project project = this.searchQuery.getProject();
        String filePrefix = "project-" + project.getId() + "-" + searchQuery.getMapQueryType() + "-";
        Long uniqueId = new Random().nextLong();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmmssSSS");
        String fileName = this.rWorkingDir + filePrefix + sdf.format(new Date()) + uniqueId.toString() + ".kml";

        // project type determines what sort of dataframe to build
        switch (this.searchQuery.getProject().getProjectType()) {
            case GPS:
                createRPositionFixDataFrame();
                break;
            default:
                throw new RServeInterfaceException("Unhandled Project Type: " + project.getProjectType().toString());
        }

        // mapQueryType tells us what R function to call
        MapQueryType mapQueryType = this.searchQuery.getMapQueryType();
        switch (mapQueryType) {
            case POINTS:
                writePositionFixKmlFile(fileName);
                break;
            case MCP:
                writeMCPKmlFile(fileName, searchQuery);
                break;
            case KUD:
                writeKernelUDKmlFile(fileName, searchQuery);
                break;
            case AHULL:
                writeAlphahullKmlFile(fileName, searchQuery);
                break;
            case HEATMAP_POINT:
                writePointHeatmapKmlFile(fileName, searchQuery);
                break;
            case HEATMAP_LINE:
                writeLineHeatmapKmlFile(fileName, searchQuery);
                break;
            default:
                throw new RServeInterfaceException("Unhandled MapQueryType: " + searchQuery.getMapQueryType());
        }
        
        rConnection.close();
        
        return new File(fileName);
    }

    protected void startRConnection() throws RServeInterfaceException {
        try {
            if (checkLocalRserve()) {
                this.rConnection = new RConnection();
                this.rConnection.setSendBufferSize(10485760);
                this.rConnection.voidEval("library(adehabitatHR)");
                this.rConnection.voidEval("library(adehabitatMA)");
                this.rConnection.voidEval("library(alphahull)");
                this.rConnection.voidEval("library(maptools)");
                this.rConnection.voidEval("library(rgdal)");
                this.rConnection.voidEval("library(shapefiles)");
                rLog = rLog + "Libraries Loaded ";
                
                String[] scriptFiles = new String[] {
                    "to_SPLDF.r",
                    "heatmap.r"
                };
                for (String scriptFile : scriptFiles) {
                    String script = IOUtils.toString(getClass().getResourceAsStream("/r/" + scriptFile), "UTF-8");
                    this.rConnection.voidEval(script);
                }
                rLog = rLog + "Scripts Loaded ";

                // get the working directory: Java and R have different ideas about windows fileNames
                this.rWorkingDir = rConnection.eval("getwd()").asString() + File.separator;
                String osname = System.getProperty("os.name");
                if (osname != null && osname.length() >= 7 && osname.substring(0,7).equals("Windows")) {
                    this.rWorkingDir = this.rWorkingDir.replace("\\","/");
                }
            }
            else {
                throw new RServeInterfaceException("RServe not started.");
            }
        }
        catch (RserveException e) {
            throw new RServeInterfaceException(e.toString());
        }
        catch (REXPMismatchException e) {
            throw new RServeInterfaceException(e.toString());
        }
        catch (IOException e) {
            throw new RServeInterfaceException(e.toString());
        }
    }

    protected void createRPositionFixDataFrame() throws RServeInterfaceException {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(this.searchQuery);

        int [] animalIds = new int[positionFixList.size()];
        double [] latitudes= new double[positionFixList.size()];
        double [] longitudes= new double[positionFixList.size()];

        /* load up the arrays from the database result set*/
        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            animalIds[i] = Integer.parseInt(positionFix.getAnimal().getId().toString());
            longitudes[i] = positionFix.getLocationGeometry().getX();
            latitudes[i] = positionFix.getLocationGeometry().getY();
        }

        /* create the RList to become the dataFrame (add the name+array) */
        RList rPositionFixList = new RList();
        rPositionFixList.put("Id", new REXPInteger(animalIds));
        rPositionFixList.put("X", new REXPDouble(longitudes));
        rPositionFixList.put("Y", new REXPDouble(latitudes));

        try {
            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            this.rConnection.assign("positionFix", new REXPNull());
            this.rConnection.assign("positionFix", rPosFixDataFrame);
            rLog = rLog + " | PositionFix dataFrame assigned ";

            // We use WGS84 for coordinates and project to a user-defined SRS.
            // We assume the user-supplied SRS has units of metres in our area calculations.
            safeEval("positionFix$Name <- positionFix$Id;");
            safeEval("coordinates(positionFix) <- ~X+Y;");
            safeEval("positionFix.xy <- positionFix[,ncol(positionFix)];");
            safeEval("proj4string(positionFix.xy) <- CRS(\"+init=epsg:4326\");");
            safeEval("positionFix.proj <- spTransform(positionFix.xy,CRS(\"+init=" + srs + "\"));");
            rLog = rLog + "coordinates + 2 projections defined.";
        }
        catch (REngineException e) {
            throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        }
        catch (REXPMismatchException e) {
            throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        }
    }


    protected void writeMCPKmlFile(String fileName, SearchQuery searchQuery) throws RServeInterfaceException {
        String queryType = searchQuery.getMapQueryType().toString();
        Double percent = (searchQuery.getPercent() != null) ? searchQuery.getPercent() : 100d;
        safeEval(queryType + " <- mcp(positionFix.xy,percent=" + percent + ")");
        safeEval(queryType + "$area <- mcp(positionFix.proj,percent=" + percent + ",unin=c(\"m\"),unout=c(\"km2\"))$area");
        safeEval("writeOGR(" + queryType +", dsn=\"" + fileName + "\", layer= \"" + queryType + "\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))");
    }
    
    protected void writeKernelUDKmlFile(String fileName, SearchQuery searchQuery) throws RServeInterfaceException {
        String name = searchQuery.getMapQueryType().toString();
        Double percent = (searchQuery.getPercent() != null) ? searchQuery.getPercent() : 100d;
        String h = (searchQuery.getH() != null) ? searchQuery.getH() : "href";
        safeEval("KerHRp <- kernelUD(positionFix.proj, h = \"" + h + "\")");
        safeEval("KerHR <- kernelUD(positionFix.xy, h = \"" + h + "\")");
        safeEval(name + " <- getverticeshr(KerHR,percent=" + percent + ")");
        safeEval(name + "$area <- getverticeshr(KerHRp,percent=" + percent + ", unin=c(\"m\"), unout=c(\"km2\"))$area");
        safeEval("writeOGR(" + name +", dsn=\"" + fileName + "\", layer= \"" + name + "\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))");
    }
    
    protected void writeAlphahullKmlFile(String fileName, SearchQuery searchQuery) throws RServeInterfaceException {
        String name = searchQuery.getMapQueryType().toString();
        Double alpha = (searchQuery.getAlpha() != null) ? searchQuery.getAlpha() : 0.1;
        safeEval("ahull.proj.spldf <- id.alpha(dxy=positionFix.proj, ialpha=" + alpha + ", sCS=\"+init=" + srs + "\")");
        safeEval("ahull.xy.spldf <- spTransform(ahull.proj.spldf, CRS(\"+init=epsg:4326\"))");
        safeEval("writeOGR(ahull.xy.spldf, dsn=\"" + fileName + "\", layer= \"" + name + "\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))");
    }
    
    protected void writePointHeatmapKmlFile(String fileName, SearchQuery searchQuery) throws RServeInterfaceException {
        Double gridSize = (searchQuery.getGridSize() != null) ? searchQuery.getGridSize() : 100d;
        safeEval("PPA <- fpdens2kml(sdata=positionFix.xy,igrid=" + gridSize + ", ssrs=\"+init=" + srs + "\",scol=\"Greens\", labsent=FALSE)");
        safeEval("polykml(sw=PPA,filename=\"" + fileName + "\",kmlname=paste(unique(PPA$ID),\"_point_density\",sep=\"\"),namefield=unique(PPA$ID))");
    }
    
    protected void writeLineHeatmapKmlFile(String fileName, SearchQuery searchQuery) throws RServeInterfaceException {
        Double gridSize = (searchQuery.getGridSize() != null) ? searchQuery.getGridSize() : 100d;
        safeEval("LPA <- fldens2kml(sdata=positionFix.xy,igrid=" + gridSize + ", ssrs=\"+init=" + srs + "\",scol=\"Greens\", labsent=FALSE)");
        safeEval("polykml(sw=LPA,filename=\"" + fileName + "\",kmlname=paste(unique(LPA$ID),\"_line_density\",sep=\"\"),namefield=unique(LPA$ID))");
    }
    
    protected void writePositionFixKmlFile(String fileName) throws RServeInterfaceException {
        String rCommand;
        String outFileNameFix = fileName;

        try {
            rCommand = "coordinates(positionFix) <- c(\"Y\",\"X\");proj4string(positionFix)=CRS(\"+init=epsg:4326\")";
            rLog = rLog + "coordinates + projection defined for KML";
            logger.debug(rCommand);
            rConnection.eval(rCommand);

            @SuppressWarnings("unused")
            REXP foo = rConnection.eval("positionFix");

            rCommand = "writeOGR(positionFix, dsn=\"" + outFileNameFix + "\", layer= \"positionFix\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))";
            logger.debug(rCommand);
            rConnection.eval(rCommand);
            rLog = rLog + "KML written";
        }
        catch (RserveException e) {
            throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        }
    }
    
    // Wraps an R statement inside a try({...}, silent=TRUE) so we can catch any exception
    // that occurs during evaluation. This gives us a much better error message, such as
    // 
    //     Error in .kernelUDs(SpatialPoints(x, proj4string = CRS(as.character(pfs1))),  :
    //     h should be numeric or equal to either "href" or "LSCV"
    // 
    // instead of just this if we catch exceptions from RConnection.voidEval(...)
    // 
    //     org.rosuda.REngine.Rserve.RserveException: voidEval failed
    // 
    private void safeEval(String rCommand) throws RServeInterfaceException {
        logger.debug(String.format("Evaluating R: %s", rCommand));
        REXP rExp;
        try {
            rExp = rConnection.eval("try({" + rCommand + "}, silent=TRUE)");
        }
        catch (RserveException e) {
            throw new RServeInterfaceException("Error evaluating expression", e);
        }
        String errorMessage = null;
        try {
            if (rExp.inherits("try-error")) {
                errorMessage = rExp.asString();
            }
        }
        catch (REXPMismatchException e) {
            throw new RServeInterfaceException("Error getting error message", e);
        }
        if (errorMessage != null) {
            throw new RServeInterfaceException(errorMessage);
        }
    }
}
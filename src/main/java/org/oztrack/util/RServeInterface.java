package org.oztrack.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.error.RServeInterfaceException;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RFileOutputStream;
import org.rosuda.REngine.Rserve.RserveException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.oztrack.util.StartRserve.checkLocalRserve;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/08/11
 * Time: 2:06 PM
 */
public class RServeInterface {

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());

    /* inputs
    private List<PositionFix> positionFixList;
    private MapQueryType mapQueryType;
    private String outFileName;
    */

    private RConnection rConnection;
    private String rWorkingDir;
    private SearchQuery searchQuery;
    private String rLog = "";

    public RServeInterface() {
    }

    /*
    public RServeInterface(List<PositionFix> positionFixList, MapQueryType mapQueryType, String outFileName) {
       // this.positionFixList = positionFixList;
        this.mapQueryType = mapQueryType;
        this.outFileName = outFileName;
    }
    */

    public File createKml(SearchQuery searchQuery) throws RServeInterfaceException {

        this.searchQuery = searchQuery;
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
            case ALL_POINTS:
                writePositionFixKmlFile(fileName);
                break;
            case MCP100:
            case MCP95:
            case MCP50:
                writeMCPKmlFile(fileName, mapQueryType);
                break;
//            case CHARHULL100:
//            case CHARHULL95:
//            case CHARHULL50:
//                writeCharHullKmlFile(fileName, mapQueryType);
//                break;
            case KUD95:
            case KUD50:
                writeKernelUDKmlFile(fileName, mapQueryType);
                break;
            default:
                throw new RServeInterfaceException("Unhandled MapQueryType: " + searchQuery.getMapQueryType());
        }

        rConnection.close();

        return new File(fileName);


    }

    /*
    public void createPositionFixKml() throws RServeInterfaceException {

        startRConnection();
        createRPositionFixDataFrame();

        switch (this.mapQueryType) {
            case ALL_POINTS:
                //writePositionFixKmlFile();
                break;
            default:
                throw new RServeInterfaceException("Unhandled MapQueryType: " + this.mapQueryType);
        }
        rConnection.close();
    }
    */


    protected void startRConnection() throws RServeInterfaceException {

        try {
            if (checkLocalRserve()) {
                this.rConnection = new RConnection();
                this.rConnection.setSendBufferSize(10485760);
                this.rConnection.voidEval("library(adehabitatHR);library(adehabitatMA);library(maptools);library(rgdal);library(shapefiles)");
                rLog = rLog + "Libraries Loaded ";

                // get the working directory: Java and R have different ideas about windows fileNames
                this.rWorkingDir = rConnection.eval("getwd()").asString() + File.separator;
                String osname = System.getProperty("os.name");
                if (osname != null && osname.length() >= 7 && osname.substring(0,7).equals("Windows")) {
                    this.rWorkingDir = this.rWorkingDir.replace("\\","/");
                }

           } else {
                throw new RServeInterfaceException("RServe not started.");
           }

        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString());
        } catch (REXPMismatchException e) {
            throw new RServeInterfaceException(e.toString());
        }
    }

    protected void createRPositionFixDataFrame() throws RServeInterfaceException {

        List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery().queryProjectPositionFixes(this.searchQuery);

        int [] animalIds = new int[positionFixList.size()];
        double [] latitudes= new double[positionFixList.size()];
        double [] longitudes= new double[positionFixList.size()];
        String [] detectionTimes= new String[positionFixList.size()];

        /* load up the arrays from the database result set*/
        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            detectionTimes[i] = "foo"; //sdf.format(positionFix.getDetectionTime());
            animalIds[i] = Integer.parseInt(positionFix.getAnimal().getId().toString());
            latitudes[i] = Double.parseDouble(positionFix.getLatitude());
            longitudes[i] = Double.parseDouble(positionFix.getLongitude());
        }

        /* create the RList to become the dataFrame (add the name+array) */
        RList rPositionFixList = new RList();
        rPositionFixList.put("Id", new REXPInteger(animalIds));
        rPositionFixList.put("X", new REXPDouble(latitudes));
        rPositionFixList.put("Y", new REXPDouble(longitudes));
        //rPositionFixList.put("Foo", new REXPString(detectionTimes));

        /* assign the dataFrame */
        try {
            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            this.rConnection.assign("positionFix", new REXPNull());
            this.rConnection.assign("positionFix", rPosFixDataFrame);
            rLog = rLog + " | PositionFix dataFrame assigned ";

            // spatial it up
            String rCommand;
            REXP rResult;

            rCommand = "positionFix$Name <- positionFix$Id;"
                     + "coordinates(positionFix) <- ~Y+X;"
                     + "positionFix.xy <- positionFix[,ncol(positionFix)];"
                     + "proj4string(positionFix.xy) <- CRS(\"+init=epsg:4326\");"
                     + "positionFix.proj <- spTransform(positionFix.xy,CRS(\"+init=epsg:20255\"));";

            logger.debug(rCommand);
            rResult = rConnection.parseAndEval(rCommand);
            if (rResult.inherits("try-error")) {
                throw new RServeInterfaceException(" Log: " + rLog + " " + rResult.asString());
            }
            rLog = rLog + "coordinates + 2 projections defined.";

        } catch (REngineException e) {
             throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        } catch (REXPMismatchException e) {
             throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        }
    }


    protected void writeMCPKmlFile(String fileName, MapQueryType mapQueryType) throws RServeInterfaceException {

        String rCommand;
        String queryType = mapQueryType.toString(); // eg.MCP100
        String percent = queryType.replace("MCP","");

        try {

            rCommand = queryType + " <- mcp(positionFix.xy,percent=" + percent + ")";
            rConnection.voidEval(rCommand);
            rLog = rLog + queryType + " object set. File to write to : " + fileName;

            rCommand = queryType + "$area <- mcp(positionFix.proj,percent=" + percent + ")$area";
            rConnection.voidEval(rCommand);
            rLog = rLog + queryType + "$area object set";

            rCommand = "writeOGR(" + queryType +", dsn=\"" + fileName + "\", layer= \"" + queryType + "\", driver=\"KML\", dataset_options=c(\"NameField=Name\"))";
            rConnection.voidEval(rCommand);
            rLog = rLog + "KML written.";

        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString() + " Log: " + rLog);
        }
    }
    
    protected void writeKernelUDKmlFile(String fileName, MapQueryType mapQueryType) throws RServeInterfaceException {

        String rCommand;
        String queryType = mapQueryType.toString(); // eg.MCP100
        String percent = queryType.replace("KUD","");
        
        try {

            rCommand = "KerHR <- kernelUD(positionFix.xy, h = \"href\");"
                     + "KerHRp <- kernelUD(positionFix.proj, h = \"href\");";
            rConnection.voidEval(rCommand);
            rLog = rLog + "KerHR and KerHRp objects set. ";

            rCommand = queryType + " <- getverticeshr(KerHR,percent=" + percent + ");"
                     + queryType + "$area <- getverticeshr(KerHRp,percent=" + percent + ")$area";
            rConnection.voidEval(rCommand);
            rLog = rLog + queryType + " object set. ";

            rCommand = "writeOGR(" + queryType +", dsn=\"" + fileName + "\", layer= \"" + queryType + "\", driver=\"KML\", dataset_options=c(\"NameField=Name\"))";
            rConnection.voidEval(rCommand);
            rLog = rLog + "KML written. ";

        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString() + " Log: " + rLog);
        }
    }

    
    protected void writeCharHullKmlFile(String fileName, MapQueryType mapQueryType) throws RServeInterfaceException {

        String rCommand;
        String outFileNameFix = fileName.replace("\\","/"); /* for R in windows */
        
        try {

            rCommand = "coordinates(positionFix) <- c(\"Y\",\"X\");proj4string(positionFix)=CRS(\"+init=epsg:4326\")";
            rConnection.eval(rCommand);
            logger.debug(rCommand);
            rLog = rLog + "coordinates + projection defined for KML.";

            String queryType = mapQueryType.toString(); // eg.MCP100
            String percent = queryType.replace("CHARHULL","");

            rCommand = "CharHullHR <- CharHull(positionFix[,1])";
            rConnection.voidEval(rCommand);
            rLog = rLog + "CharHullHR object set.";

            rCommand = queryType + " <- getverticeshr(CharHullHR,percent=" + percent + ")";
            rConnection.voidEval(rCommand);
            rLog = rLog + queryType + " object set.";

            rCommand = "writeOGR(" + queryType +", dsn=\"" + outFileNameFix + "\", layer= \"" + queryType + "\", driver=\"KML\", dataset_options=c(\"NameField=Name\"))";
            rConnection.eval(rCommand);
            logger.debug(rCommand);
            rLog = rLog + "KML written.";

        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString() + " Log: " + rLog);
        }
    }


    protected void writePositionFixKmlFile(String fileName) throws RServeInterfaceException {

        String rCommand;
        String outFileNameFix = fileName;
        //String outFileNameFix = fileName.replace("\\","/"); /* for R in windows */

        try {

            rCommand = "coordinates(positionFix) <- c(\"Y\",\"X\");proj4string(positionFix)=CRS(\"+init=epsg:4326\")";
            rLog = rLog + "coordinates + projection defined for KML";
            logger.debug(rCommand);
            rConnection.eval(rCommand);

            REXP foo = rConnection.eval("positionFix");

            rCommand = "writeOGR(positionFix, dsn=\"" + outFileNameFix + "\", layer= \"positionFix\", driver=\"KML\", dataset_options=c(\"NameField=Name\"))";
            logger.debug(rCommand);
            rConnection.eval(rCommand);
            rLog = rLog + "KML written";

        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        }

    }


}

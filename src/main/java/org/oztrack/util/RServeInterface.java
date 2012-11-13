package org.oztrack.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisParameter;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
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

    public RServeInterface() {
    }

    public File createKml(Analysis analysis, List<PositionFix> positionFixList) throws RServeInterfaceException {
        startRConnection();
        String fileName = createTempFileName("project-" + analysis.getProject().getId() + "-" + analysis.getAnalysisType() + "-");
        Project project = analysis.getProject();

        String srs =
            StringUtils.isNotBlank(project.getSrsIdentifier())
            ? project.getSrsIdentifier().toLowerCase(Locale.ENGLISH)
            : "epsg:3577";
        createRPositionFixDataFrame(positionFixList, srs);

        switch (analysis.getAnalysisType()) {
            case MCP:
                writeMCPKmlFile(analysis, srs, fileName);
                break;
            case KUD:
                writeKernelUDKmlFile(analysis, srs, fileName);
                break;
            case AHULL:
                writeAlphahullKmlFile(analysis, srs, fileName);
                break;
            case HEATMAP_POINT:
                writePointHeatmapKmlFile(analysis, srs, fileName);
                break;
            case HEATMAP_LINE:
                writeLineHeatmapKmlFile(analysis, srs, fileName);
                break;
            default:
                throw new RServeInterfaceException("Unhandled AnalysisType: " + analysis.getAnalysisType());
        }

        rConnection.close();
        return new File(fileName);
    }

    private String createTempFileName(String fileNamePrefix) {
        Long uniqueId = new Random().nextLong();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmmssSSS");
        String fileName = this.rWorkingDir + fileNamePrefix + sdf.format(new Date()) + uniqueId.toString() + ".kml";
        return fileName;
    }

    private void startRConnection() throws RServeInterfaceException {
        if (StartRserve.checkLocalRserve()) {
            try {
                this.rConnection = new RConnection();
                this.rConnection.setSendBufferSize(10485760);
            }
            catch (RserveException e) {
                throw new RServeInterfaceException("Error starting Rserve.", e);
            }

            try {
                this.rWorkingDir = rConnection.eval("getwd()").asString() + File.separator;
            }
            catch (Exception e) {
                throw new RServeInterfaceException("Error getting Rserve working directory.", e);
            }
            String osname = System.getProperty("os.name");
            if (StringUtils.startsWith(osname, "Windows")) {
                this.rWorkingDir = this.rWorkingDir.replace("\\","/");
            }

            loadLibraries();
            loadScripts();
        }
        else {
            throw new RServeInterfaceException("Could not start Rserve.");
        }
    }

    private void loadLibraries() throws RServeInterfaceException {
        String[] libraries = new String[] {
            "adehabitatHR",
            "adehabitatMA",
            "shapefiles",
            "rgdal",
            "alphahull",
            "sp",
            "raster",
            "plyr",
            "spatstat",
            "maptools",
            "Grid2Polygons",
            "RColorBrewer",
            "googleVis",
            "spacetime",
            "plotKML"
        };
        for (String library : libraries) {
            try {
                this.rConnection.voidEval("library(" + library + ")");
            }
            catch (RserveException e) {
                throw new RServeInterfaceException("Error loading '" + library + "' library.", e);
            }
        }
    }

    private void loadScripts() throws RServeInterfaceException {
        String[] scriptFileNames = new String[] {
            "alphahull.r",
            "heatmap.r"
        };
        for (String scriptFileName : scriptFileNames) {
            String scriptString = null;
            try {
                scriptString = IOUtils.toString(getClass().getResourceAsStream("/r/" + scriptFileName), "UTF-8");
            }
            catch (IOException e) {
                throw new RServeInterfaceException("Error reading '" + scriptFileName + "' script.", e);
            }
            try {
                this.rConnection.voidEval(scriptString);
            }
            catch (RserveException e) {
                throw new RServeInterfaceException("Error running '" + scriptFileName + "' script.", e);
            }
        }
    }

    private void createRPositionFixDataFrame(List<PositionFix> positionFixList, String srs) throws RServeInterfaceException {
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

            // We use WGS84 for coordinates and project to a user-defined SRS.
            // We assume the user-supplied SRS has units of metres in our area calculations.
            safeEval("positionFix$Name <- positionFix$Id;");
            safeEval("coordinates(positionFix) <- ~X+Y;");
            safeEval("positionFix.xy <- positionFix[,ncol(positionFix)];");
            safeEval("proj4string(positionFix.xy) <- CRS(\"+init=epsg:4326\");");
            safeEval("positionFix.proj <- try({spTransform(positionFix.xy,CRS(\"+init=" + srs + "\"))}, silent=TRUE);");
            safeEval(
                "if (class(positionFix.proj) == 'try-error') {\n" +
                "  stop(\"Unable to project locations. Please check that coordinates and the project's Spatial Reference System are valid.\")\n" +
                "}"
            );
        }
        catch (REngineException e) {
            throw new RServeInterfaceException(e.toString());
        }
        catch (REXPMismatchException e) {
            throw new RServeInterfaceException(e.toString());
        }
    }


    private void writeMCPKmlFile(Analysis analysis, String srs, String fileName) throws RServeInterfaceException {
        AnalysisParameter percentParameter = analysis.getParamater("percent");
        Double percent = (percentParameter.getValue() != null) ? Double.valueOf(percentParameter.getValue()) : 100d;
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RServeInterfaceException("percent must be between 0 and 100.");
        }
        safeEval("mcp.obj <- try({mcp(positionFix.xy, percent=" + percent + ")}, silent=TRUE)");
        safeEval(
            "if (class(mcp.obj) == 'try-error') {\n" +
            "  stop('At least 5 relocations are required to fit a home range. Please ensure all animals have >5 locations.')\n" +
            "}"
        );
        safeEval("mcp.obj$area <- mcp(positionFix.proj, percent=" + percent + ", unin=c(\"m\"), unout=c(\"km2\"))$area");
        safeEval("writeOGR(mcp.obj, dsn=\"" + fileName + "\", layer= \"MCP\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))");
    }

    private void writeKernelUDKmlFile(Analysis analysis, String srs, String fileName) throws RServeInterfaceException {
        AnalysisParameter percentParameter = analysis.getParamater("percent");
        Double percent = (percentParameter.getValue() != null) ? Double.valueOf(percentParameter.getValue()) : 100d;
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RServeInterfaceException("percent must be between 0 and 100.");
        }
        AnalysisParameter hParameter = analysis.getParamater("h");
        String h = (hParameter.getValue() != null) ? hParameter.getValue() : "href";
        if (!(h.equals("href") || h.equals("LSCV") || NumberUtils.isNumber(h))) {
            throw new RServeInterfaceException("h-value must be \"href\", \"LSCV\", or a numeric value.");
        }
        String hExpr = NumberUtils.isNumber(h) ? h : "\"" + h + "\"";
        AnalysisParameter gridSizeParameter = analysis.getParamater("gridSize");
        Double gridSize = (gridSizeParameter.getValue() != null) ? Double.valueOf(gridSizeParameter.getValue()) : 50d;
        AnalysisParameter extentParameter = analysis.getParamater("extent");
        Double extent = (extentParameter.getValue() != null) ? Double.valueOf(extentParameter.getValue()) : 1d;
        safeEval("h <- " + hExpr);
        safeEval("KerHRp <- try({kernelUD(xy=positionFix.proj, h=h, grid=" + gridSize + ", extent=" + extent + ")}, silent=TRUE)");
        safeEval(
            "if (class(KerHRp) == 'try-error') {\n" +
            "  if (h == 'href') {\n" +
            "    stop('Kernel unable to generate under these parameters. Try increasing the extent.')\n" +
            "  }\n" +
            "  if (h == 'LSCV') {\n" +
            "    stop('Kernel unable to generate under these parameters. Try increasing the extent and the grid size.')\n" +
            "  }\n" +
            "  if (class(h) == 'numeric') {\n" +
            "    stop('Kernel unable to generate under these parameters. Try increasing the h smoothing parameter value.')\n" +
            "  }\n" +
            "  stop('Kernel unable to generate due to error: ' + conditionMessage(KerHRp))\n" +
            "}"
        );
        safeEval("allh <- sapply(1:length(KerHRp), function(x) {KerHRp[[x]]@h$h})");
        safeEval("myKerP <- try({getverticeshr(KerHRp, percent=" + percent + ", unin=c(\"m\"), unout=c(\"km2\"))}, silent=TRUE)");
        safeEval(
            "if (class(myKerP) == 'try-error') {\n" +
            "  stop('Kernel polygon unable to generate under these parameters. Try increasing the grid size or change the percentile.')\n" +
            "}"
        );
        safeEval("myKer <- spTransform(myKerP, CRS(\"+proj=longlat +datum=WGS84\"))");
        safeEval("myKer$area <- myKerP$area");
        safeEval("myKer$hval <- allh");
        safeEval("writeOGR(myKer, dsn=\"" + fileName + "\", layer= \"KUD\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))");
    }

    private void writeAlphahullKmlFile(Analysis analysis, String srs, String fileName) throws RServeInterfaceException {
        AnalysisParameter alphaParameter = analysis.getParamater("alpha");
        Double alpha = (alphaParameter.getValue() != null) ? Double.valueOf(alphaParameter.getValue()) : 0.1d;
        if (!(alpha > 0d)) {
            throw new RServeInterfaceException("alpha must be greater than 0.");
        }
        safeEval("myAhull <- myalphahullP(positionFix.proj, sinputssrs=\"+init=" + srs + "\", ialpha=" + alpha + ")");
        safeEval("writeOGR(myAhull, dsn=\"" + fileName + "\", layer=\"AHULL\", driver=\"KML\", dataset_options=c(\"NameField=Id\"))");
    }

    private void writePointHeatmapKmlFile(Analysis analysis, String srs, String fileName) throws RServeInterfaceException {
        AnalysisParameter gridSizeParameter = analysis.getParamater("gridSize");
        Double gridSize = (gridSizeParameter.getValue() != null) ? Double.valueOf(gridSizeParameter.getValue()) : 100d;
        if (!(gridSize > 0d)) {
            throw new RServeInterfaceException("grid size must be greater than 0.");
        }
        safeEval("PPA <- try({fpdens2kml(sdata=positionFix.xy, igrid=" + gridSize + ", ssrs=\"+init=" + srs + "\", scol=\"Greens\", labsent=FALSE)}, silent=TRUE)");
        safeEval(
            "if (class(PPA) == 'try-error') {\n" +
            "  stop('Grid size too small. Try increasing grid number.')\n" +
            "}"
        );
        safeEval("polykml(sw=PPA, filename=\"" + fileName + "\", kmlname=paste(unique(PPA$ID), \"_point_density\",sep=\"\"),namefield=unique(PPA$ID))");
    }

    private void writeLineHeatmapKmlFile(Analysis analysis, String srs, String fileName) throws RServeInterfaceException {
        AnalysisParameter gridSizeParameter = analysis.getParamater("gridSize");
        Double gridSize = (gridSizeParameter.getValue() != null) ? Double.valueOf(gridSizeParameter.getValue()) : 100d;
        if (!(gridSize > 0d)) {
            throw new RServeInterfaceException("grid size must be greater than 0.");
        }
        safeEval("LPA <- try({fldens2kml(sdata=positionFix.xy, igrid=" + gridSize + ", ssrs=\"+init=" + srs + "\",scol=\"YlOrRd\", labsent=FALSE)}, silent=TRUE)");
        safeEval(
            "if (class(LPA) == 'try-error') {\n" +
            "  stop('Grid size too small. Try increasing grid number.')\n" +
            "}"
        );
        safeEval("polykml(sw=LPA, filename=\"" + fileName + "\", kmlname=paste(unique(LPA$ID), \"_line_density\", sep=\"\"), namefield=unique(LPA$ID))");
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
        try {
            rConnection.eval("e <- tryCatch({" + rCommand + "}, error = function(e) {e})");
        }
        catch (RserveException e) {
            throw new RServeInterfaceException("Error evaluating expression", e);
        }
        String errorMessage = null;
        try {
            if (rConnection.eval("inherits(e, 'error')").asInteger() == 1) {
                errorMessage = rConnection.eval("conditionMessage(e)").asString();
            }
        }
        catch (Exception e) {
            throw new RServeInterfaceException("Error getting error message", e);
        }
        if (errorMessage != null) {
            throw new RServeInterfaceException(errorMessage);
        }
    }
}
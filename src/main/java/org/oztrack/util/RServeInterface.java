package org.oztrack.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.error.RServeInterfaceException;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
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

    public void createKml(Analysis analysis, List<PositionFix> positionFixList) throws RServeInterfaceException {
        startRConnection();
        Project project = analysis.getProject();

        String srs =
            StringUtils.isNotBlank(project.getSrsIdentifier())
            ? project.getSrsIdentifier().toLowerCase(Locale.ENGLISH)
            : "epsg:3577";
        createAnimalNameList(analysis);
        createRPositionFixDataFrame(positionFixList, srs);

        switch (analysis.getAnalysisType()) {
            case MCP:
                writeMCPKmlFile(analysis, srs);
                break;
            case KUD:
                writeKernelUDKmlFile(analysis, srs);
                break;
            case KBB:
                writeKernelBBKmlFile(analysis, srs);
                break;
            case AHULL:
                writeAlphahullKmlFile(analysis, srs);
                break;
            case LOCOH:
                writeLocohKmlFile(analysis, srs);
                break;
            case HEATMAP_POINT:
                writePointHeatmapKmlFile(analysis, srs);
                break;
            case HEATMAP_LINE:
                writeLineHeatmapKmlFile(analysis, srs);
                break;
            default:
                throw new RServeInterfaceException("Unhandled AnalysisType: " + analysis.getAnalysisType());
        }

        rConnection.close();
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
            loadLibrary(library);
        }
    }

    private void loadLibrary(String library) throws RServeInterfaceException {
        try {
            this.rConnection.voidEval("library(" + library + ")");
        }
        catch (RserveException e) {
            throw new RServeInterfaceException("Error loading '" + library + "' library.", e);
        }
    }

    private void loadScripts() throws RServeInterfaceException {
        String[] scriptFileNames = new String[] {
            "kmlPolygons.r",
            "mcp.r",
            "kernelud.r",
            "kernelbb.r",
            "alphahull.r",
            "locoh.r",
            "heatmap.r"
        };
        for (String scriptFileName : scriptFileNames) {
            loadScript(scriptFileName);
        }
    }

    private void loadScript(String scriptFileName) throws RServeInterfaceException {
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

    private void createAnimalNameList(Analysis analysis) throws RServeInterfaceException {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<REXP> contents = new ArrayList<REXP>();
        for (Animal animal : analysis.getAnimals()) {
            names.add(animal.getId().toString());
            contents.add(new REXPString(animal.getAnimalName()));
        }
        REXP rexp = new REXPList(new RList(contents, names));
        try {
            this.rConnection.assign("animalName", rexp);
        }
        catch (REngineException e) {
            throw new RServeInterfaceException(e.toString());
        }
    }

    private void createRPositionFixDataFrame(List<PositionFix> positionFixList, String srs) throws RServeInterfaceException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] animalIds = new String[positionFixList.size()];
        String[] dates = new String[positionFixList.size()];
        double[] latitudes = new double[positionFixList.size()];
        double[] longitudes = new double[positionFixList.size()];

        // Load up the arrays from the database result set
        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            animalIds[i] = positionFix.getAnimal().getId().toString();
            dates[i] = dateFormat.format(positionFix.getDetectionTime());
            longitudes[i] = positionFix.getLocationGeometry().getX();
            latitudes[i] = positionFix.getLocationGeometry().getY();
        }

        // Create the RList to become the dataframe
        RList rPositionFixList = new RList();
        rPositionFixList.put("ID", new REXPString(animalIds));
        rPositionFixList.put("Date", new REXPString(dates));
        rPositionFixList.put("X", new REXPDouble(longitudes));
        rPositionFixList.put("Y", new REXPDouble(latitudes));

        try {
            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            this.rConnection.assign("positionFix", rPosFixDataFrame);

            // Convert date strings into POSIXct instances
            safeEval("positionFix$Date <- as.POSIXct(strptime(positionFix$Date, '%Y-%m-%d %H:%M:%S'));");

            // Create SpatialPointsDataFrame in WGS84
            safeEval("positionFix.xy <- positionFix[, c('ID', 'X', 'Y')];");
            safeEval("coordinates(positionFix.xy) <- ~X+Y;");
            safeEval("proj4string(positionFix.xy) <- CRS('+init=epsg:4326');");

            // Create SpatialPointsDataFrame projected to SRS
            // Note: we assume the user-supplied SRS has units of metres in our area calculations.
            safeEval("positionFix.proj <- try({spTransform(positionFix.xy,CRS('+init=" + srs + "'))}, silent=TRUE);");
            safeEval(
                "if (class(positionFix.proj) == 'try-error') {\n" +
                "  stop('Unable to project locations. Please check that coordinates and the project\\'s Spatial Reference System are valid.')\n" +
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


    private void writeMCPKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Double percent = (Double) analysis.getParameterValue("percent", true);
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RServeInterfaceException("percent must be between 0 and 100.");
        }
        safeEval("percent <- " + percent);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_mcp(percent=percent, kmlFile=kmlFile)");
    }

    private void writeKernelUDKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Double percent = (Double) analysis.getParameterValue("percent", true);
        String hEstimator = (String) analysis.getParameterValue("hEstimator", false);
        Double hValue = (Double) analysis.getParameterValue("hValue", false);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        Double extent = (Double) analysis.getParameterValue("extent", true);
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RServeInterfaceException("percent must be between 0 and 100.");
        }
        if ((hEstimator == null) && (hValue == null)) {
            throw new RServeInterfaceException("h estimator or h value must be entered.");
        }
        safeEval("h <- " + ((hValue != null) ? hValue.toString() : "'" + hEstimator + "'"));
        safeEval("gridSize <- " + gridSize);
        safeEval("extent <- " + extent);
        safeEval("percent <- " + percent);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_kernelud(h=h, gridSize=gridSize, extent=extent, percent=percent, kmlFile=kmlFile)");
    }

    private void writeKernelBBKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Double percent = (Double) analysis.getParameterValue("percent", true);
        Double sig1 = (Double) analysis.getParameterValue("sig1", false);
        Double sig2 = (Double) analysis.getParameterValue("sig2", false);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        Double extent = (Double) analysis.getParameterValue("extent", true);
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RServeInterfaceException("percent must be between 0 and 100.");
        }
        if ((sig1 == null) || (sig2 == null)) {
            throw new RServeInterfaceException("sig1 and sig2 must both be entered.");
        }
        safeEval("sig1 <- " + sig1);
        safeEval("sig2 <- " + sig2);
        safeEval("gridSize <- " + gridSize);
        safeEval("extent <- " + extent);
        safeEval("percent <- " + percent);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_kernelbb(sig1=sig1, sig2=sig2, gridSize=gridSize, extent=extent, percent=percent, kmlFile=kmlFile)");
    }

    private void writeAlphahullKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Double alpha = (Double) analysis.getParameterValue("alpha", true);
        if (!(alpha > 0d)) {
            throw new RServeInterfaceException("alpha must be greater than 0.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("alpha <- " + alpha);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_alphahull(srs=srs, alpha=alpha, kmlFile=kmlFile)");
    }

    private void writeLocohKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Double percent = (Double) analysis.getParameterValue("percent", true);
        Double k = (Double) analysis.getParameterValue("k", false);
        Double r = (Double) analysis.getParameterValue("r", false);
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RServeInterfaceException("percent must be between 0 and 100.");
        }
        safeEval("k <- " + ((k != null) ? k : "NULL"));
        safeEval("r <- " + ((r != null) ? r : "NULL"));
        safeEval("percent <- " + percent);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_locoh(k=k, r=r, percent=percent, kmlFile=kmlFile)");
    }

    private void writePointHeatmapKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Boolean showAbsence = (Boolean) analysis.getParameterValue("showAbsence", true);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        String colours = (String) analysis.getParameterValue("colours", true);
        if (!(gridSize > 0d)) {
            throw new RServeInterfaceException("grid size must be greater than 0.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("gridSize <- " + gridSize);
        safeEval("colours <- '" + colours + "'");
        safeEval("labsent <- " + (showAbsence ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_heatmap_point(srs=srs, gridSize=gridSize, colours=colours, labsent=labsent, kmlFile=kmlFile)");
    }

    private void writeLineHeatmapKmlFile(Analysis analysis, String srs) throws RServeInterfaceException {
        Boolean showAbsence = (Boolean) analysis.getParameterValue("showAbsence", true);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        String colours = (String) analysis.getParameterValue("colours", true);
        if (!(gridSize > 0d)) {
            throw new RServeInterfaceException("grid size must be greater than 0.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("gridSize <- " + gridSize);
        safeEval("colours <- '" + colours + "'");
        safeEval("labsent <- " + (showAbsence ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("oztrack_heatmap_line(srs=srs, gridSize=gridSize, colours=colours, labsent=labsent, kmlFile=kmlFile)");
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
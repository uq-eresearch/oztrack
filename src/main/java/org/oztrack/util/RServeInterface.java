package org.oztrack.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
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

    private ObjectPool<RConnection> rConnectionPool;
    private RConnection rConnection;

    public RServeInterface(ObjectPool<RConnection> rConnectionPool) {
        this.rConnectionPool = rConnectionPool;
    }

    public void createKml(Analysis analysis, List<PositionFix> positionFixList) throws RServeInterfaceException {
        try {
            this.rConnection = rConnectionPool.borrowObject();
        }
        catch (Exception e) {
            throw new RServeInterfaceException("Error getting R connection.", e);
        }
        try {
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
        }
        finally {
            try {
                rConnectionPool.returnObject(this.rConnection);
            }
            catch (Exception e) {
                logger.error("Error returning R connection", e);
            }
        }
    }

    public Map<Long, Set<Date>> runSpeedFilter(Project project, List<PositionFix> positionFixList, Double maxSpeed) throws RServeInterfaceException {
        try {
            this.rConnection = rConnectionPool.borrowObject();
        }
        catch (Exception e) {
            throw new RServeInterfaceException("Error getting R connection.", e);
        }
        try {
            String srs =
                StringUtils.isNotBlank(project.getSrsIdentifier())
                ? project.getSrsIdentifier().toLowerCase(Locale.ENGLISH)
                : "epsg:3577";
            createRPositionFixDataFrame(positionFixList, srs);
            safeEval("srs <- '+init=" + srs + "'");
            safeEval("max.speed <- " + maxSpeed);
            REXP rexp = safeEval("fspeedfilter(sinputfile=positionFix, sinputssrs=srs, max.speed=max.speed)");
            RList data = rexp.getAttribute("data").asList();
            String[] idStrings = data.at("ID").asStrings();
            double[] dateDoubles = ((REXPDouble) data.at("Date")).asDoubles();
            Map<Long, Set<Date>> animalDates = new HashMap<Long, Set<Date>>();
            for (int i = 0; i < idStrings.length; i++) {
                Long id = Long.valueOf(idStrings[i]);
                Date date = new Date(((long) dateDoubles[i]) * 1000L); // s to ms
                Set<Date> dates = animalDates.get(id);
                if (dates == null) {
                    dates = new HashSet<Date>();
                    animalDates.put(id, dates);
                }
                dates.add(date);
            }
            return animalDates;
        }
        catch (REXPMismatchException e) {
            throw new RServeInterfaceException("Error running speed filter.", e);
        }
        finally {
            try {
                rConnectionPool.returnObject(this.rConnection);
            }
            catch (Exception e) {
                logger.error("Error returning R connection", e);
            }
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
        safeEval("srs <- '" + srs + "'");
        safeEval("percent <- " + percent);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("is180 <- FALSE");
        safeEval("oztrack_mcp(srs=srs, percent=percent, kmlFile=kmlFile, is180=is180)");
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
        safeEval("srs <- '" + srs + "'");
        safeEval("h <- " + ((hValue != null) ? hValue.toString() : "'" + hEstimator + "'"));
        safeEval("gridSize <- " + gridSize);
        safeEval("extent <- " + extent);
        safeEval("percent <- " + percent);
        safeEval("kmlFile <- '" + analysis.getAbsoluteResultFilePath() + "'");
        safeEval("is180 <- FALSE");
        safeEval("oztrack_kernelud(srs=srs, h=h, gridSize=gridSize, extent=extent, percent=percent, kmlFile=kmlFile, is180=is180)");
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
        safeEval("is180 <- FALSE");
        safeEval("oztrack_alphahull(srs=srs, alpha=alpha, kmlFile=kmlFile, is180=is180)");
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
    private REXP safeEval(String rCommand) throws RServeInterfaceException {
        logger.debug(String.format("Evaluating R: %s", rCommand));
        REXP rexp = null;
        try {
            rexp = this.rConnection.eval("e <- tryCatch({" + rCommand + "}, error = function(e) {e})");
        }
        catch (RserveException e) {
            throw new RServeInterfaceException("Error evaluating expression", e);
        }
        String errorMessage = null;
        try {
            if (this.rConnection.eval("inherits(e, 'error')").asInteger() == 1) {
                errorMessage = this.rConnection.eval("conditionMessage(e)").asString();
            }
        }
        catch (Exception e) {
            throw new RServeInterfaceException("Error getting error message", e);
        }
        if (errorMessage != null) {
            throw new RServeInterfaceException(errorMessage);
        }
        return rexp;
    }
}
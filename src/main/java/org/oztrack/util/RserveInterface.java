package org.oztrack.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.error.RserveInterfaceException;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RserveException;

public class RserveInterface {
    private final Logger logger = Logger.getLogger(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private RserveConnectionPool rConnectionPool;
    private RConnection rConnection;

    public RserveInterface(RserveConnectionPool rserveConnectionPool) {
        this.rConnectionPool = rserveConnectionPool;
    }

    public void runAnalysis(Analysis analysis, List<PositionFix> positionFixList) throws RserveInterfaceException {
        try {
            this.rConnection = rConnectionPool.borrowObject();
        }
        catch (Exception e) {
            throw new RserveInterfaceException("Error getting R connection.", e);
        }
        try {
            Project project = analysis.getProject();

            String srs =
                StringUtils.isNotBlank(project.getSrsIdentifier())
                ? project.getSrsIdentifier().toLowerCase(Locale.ENGLISH)
                : "epsg:3577";

            switch (analysis.getAnalysisType()) {
                case MCP:
                    runMcpAnalysis(analysis, positionFixList, srs);
                    break;
                case KUD:
                    runKernelUdAnalysis(analysis, positionFixList, srs);
                    break;
                case KBB:
                    runKernelBbAnalysis(analysis, positionFixList, srs);
                    break;
                case AHULL:
                    runAlphahullAnalysis(analysis, positionFixList, srs);
                    break;
                case LOCOH:
                    runLocohAnalysis(analysis, positionFixList, srs);
                    break;
                case HEATMAP_POINT:
                    runPointHeatmapAnalysis(analysis, positionFixList, srs);
                    break;
                case HEATMAP_LINE:
                    runLineHeatmapAnalysis(analysis, positionFixList, srs);
                    break;
                case KALMAN:
                    runKalmanAnalysis(analysis, positionFixList);
                    break;
                case KALMAN_SST:
                    runKalmanSstAnalysis(analysis, positionFixList);
                    break;
                default:
                    throw new RserveInterfaceException("Unhandled AnalysisType: " + analysis.getAnalysisType());
            }
        }
        catch (RserveInterfaceException e) {
            logger.error("Error running R analysis.", e);
            throw e;
        }
        finally {
            try {
                rConnectionPool.invalidateObject(this.rConnection);
            }
            catch (Exception e2) {
                logger.error("Error invalidating R connection.", e2);
            }
        }
    }

    public Map<Long, Set<Date>> runSpeedFilter(Project project, List<PositionFix> positionFixList, Double maxSpeed) throws RserveInterfaceException {
        try {
            this.rConnection = rConnectionPool.borrowObject();
        }
        catch (Exception e) {
            throw new RserveInterfaceException("Error getting R connection.", e);
        }
        try {
            String srs =
                StringUtils.isNotBlank(project.getSrsIdentifier())
                ? project.getSrsIdentifier().toLowerCase(Locale.ENGLISH)
                : "epsg:3577";
            assignRPositionFixDataFrame(positionFixList, false);
            safeEval("srs <- '+init=" + srs + "'");
            safeEval("max.speed <- " + maxSpeed);
            safeEval("is180 <- " + (project.getCrosses180() ? "TRUE" : "FALSE"));
            REXP rexp = safeEval("fspeedfilter(sinputfile=positionFix, sinputssrs=srs, max.speed=max.speed, is180=is180)");
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
            throw new RserveInterfaceException("Error running speed filter.", e);
        }
        finally {
            try {
                rConnectionPool.invalidateObject(this.rConnection);
            }
            catch (Exception e2) {
                logger.error("Error invalidating R connection.", e2);
            }
        }
    }

    private void assignRAnimalNameList(Analysis analysis) throws RserveInterfaceException {
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
            throw new RserveInterfaceException(e.toString());
        }
    }

    private void assignRPositionFixDataFrame(
        List<PositionFix> positionFixList,
        boolean includeSst
    ) throws RserveInterfaceException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] animalIds = new String[positionFixList.size()];
        String[] dates = new String[positionFixList.size()];
        double[] latitudes = new double[positionFixList.size()];
        double[] longitudes = new double[positionFixList.size()];
        double[] ssts = includeSst ? new double[positionFixList.size()] : null;

        // Load up the arrays from the database result set
        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            animalIds[i] = positionFix.getAnimal().getId().toString();
            dates[i] = dateFormat.format(positionFix.getDetectionTime());
            longitudes[i] = positionFix.getLocationGeometry().getX();
            latitudes[i] = positionFix.getLocationGeometry().getY();
            if (includeSst) {
                ssts[i] = (positionFix.getSst() != null) ? positionFix.getSst() : REXPDouble.NA;
            }
        }

        // Create the RList to become the dataframe
        RList rPositionFixList = new RList();
        rPositionFixList.put("ID", new REXPString(animalIds));
        rPositionFixList.put("Date", new REXPString(dates));
        rPositionFixList.put("X", new REXPDouble(longitudes));
        rPositionFixList.put("Y", new REXPDouble(latitudes));
        if (includeSst) {
            rPositionFixList.put("sst", new REXPDouble(ssts));
        }

        try {
            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            this.rConnection.assign("positionFix", rPosFixDataFrame);

            // Convert date strings into POSIXct instances
            safeEval("positionFix$Date <- as.POSIXct(strptime(positionFix$Date, '%Y-%m-%d %H:%M:%S'));");
        }
        catch (REngineException e) {
            throw new RserveInterfaceException(e.toString());
        }
        catch (REXPMismatchException e) {
            throw new RserveInterfaceException(e.toString());
        }
    }

    private void assignRPositionFixXYDataFrame() throws RserveInterfaceException {
        // Create SpatialPointsDataFrame in WGS84
        safeEval("positionFix.xy <- positionFix[, c('ID', 'X', 'Y')];");
        safeEval("coordinates(positionFix.xy) <- ~X+Y;");
        safeEval("proj4string(positionFix.xy) <- CRS('+init=epsg:4326');");
    }

    private void assignRPositionFixProjDataFrame(String srs) throws RserveInterfaceException {
        // Create SpatialPointsDataFrame projected to SRS
        // Note: we assume the user-supplied SRS has units of metres in our area calculations.
        safeEval("positionFix.proj <- try({spTransform(positionFix.xy,CRS('+init=" + srs + "'))}, silent=TRUE);");
        safeEval(
            "if (class(positionFix.proj) == 'try-error') {\n" +
            "  stop('Unable to project locations. Please check that coordinates and the project\\'s Spatial Reference System are valid.')\n" +
            "}"
        );
    }

    private void runMcpAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRAnimalNameList(analysis);
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        assignRPositionFixProjDataFrame(srs);
        Double percent = (Double) analysis.getParameterValue("percent", true);
        Boolean is180 = analysis.getProject().getCrosses180();
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RserveInterfaceException("percent must be between 0 and 100.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("percent <- " + percent);
        safeEval("is180 <- " + (is180 ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_mcp(srs=srs, percent=percent, is180=is180)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runKernelUdAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRAnimalNameList(analysis);
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        assignRPositionFixProjDataFrame(srs);
        Double percent = (Double) analysis.getParameterValue("percent", true);
        String hEstimator = (String) analysis.getParameterValue("hEstimator", false);
        Boolean is180 = analysis.getProject().getCrosses180();
        Double hValue = (Double) analysis.getParameterValue("hValue", false);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        Double extent = (Double) analysis.getParameterValue("extent", true);
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RserveInterfaceException("percent must be between 0 and 100.");
        }
        if ((hEstimator == null) && (hValue == null)) {
            throw new RserveInterfaceException("h estimator or h value must be entered.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("h <- " + ((hValue != null) ? hValue.toString() : "'" + hEstimator + "'"));
        safeEval("gridSize <- " + gridSize);
        safeEval("extent <- " + extent);
        safeEval("percent <- " + percent);
        safeEval("is180 <- " + (is180 ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_kernelud(srs=srs, h=h, gridSize=gridSize, extent=extent, percent=percent, is180=is180)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runKernelBbAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRAnimalNameList(analysis);
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        assignRPositionFixProjDataFrame(srs);
        Double percent = (Double) analysis.getParameterValue("percent", true);
        Double sig1 = (Double) analysis.getParameterValue("sig1", false);
        Double sig2 = (Double) analysis.getParameterValue("sig2", false);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        Double extent = (Double) analysis.getParameterValue("extent", true);
        Boolean is180 = analysis.getProject().getCrosses180();
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RserveInterfaceException("percent must be between 0 and 100.");
        }
        if ((sig1 == null) || (sig2 == null)) {
            throw new RserveInterfaceException("sig1 and sig2 must both be entered.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("sig1 <- " + sig1);
        safeEval("sig2 <- " + sig2);
        safeEval("gridSize <- " + gridSize);
        safeEval("extent <- " + extent);
        safeEval("percent <- " + percent);
        safeEval("is180 <- " + (is180 ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_kernelbb(srs=srs, sig1=sig1, sig2=sig2, gridSize=gridSize, extent=extent, percent=percent, is180=is180)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runAlphahullAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRAnimalNameList(analysis);
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        assignRPositionFixProjDataFrame(srs);
        Double alpha = (Double) analysis.getParameterValue("alpha", true);
        Boolean is180 = analysis.getProject().getCrosses180();
        if (!(alpha > 0d)) {
            throw new RserveInterfaceException("alpha must be greater than 0.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("alpha <- " + alpha);
        safeEval("is180 <- " + (is180 ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_alphahull(srs=srs, alpha=alpha, is180=is180)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runLocohAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRAnimalNameList(analysis);
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        assignRPositionFixProjDataFrame(srs);
        Double percent = (Double) analysis.getParameterValue("percent", true);
        Double k = (Double) analysis.getParameterValue("k", false);
        Double r = (Double) analysis.getParameterValue("r", false);
        Boolean is180 = analysis.getProject().getCrosses180();
        if (!(percent >= 0d && percent <= 100d)) {
            throw new RserveInterfaceException("percent must be between 0 and 100.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("k <- " + ((k != null) ? k : "NULL"));
        safeEval("r <- " + ((r != null) ? r : "NULL"));
        safeEval("percent <- " + percent);
        safeEval("is180 <- " + (is180 ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_locoh(srs=srs, k=k, r=r, percent=percent, is180=is180)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runPointHeatmapAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        Boolean showAbsence = (Boolean) analysis.getParameterValue("showAbsence", true);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        String colours = (String) analysis.getParameterValue("colours", true);
        if (!(gridSize > 0d)) {
            throw new RserveInterfaceException("grid size must be greater than 0.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("gridSize <- " + gridSize);
        safeEval("colours <- '" + colours + "'");
        safeEval("labsent <- " + (showAbsence ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_heatmap_point(srs=srs, gridSize=gridSize, colours=colours, labsent=labsent)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runLineHeatmapAnalysis(Analysis analysis, List<PositionFix> positionFixList, String srs) throws RserveInterfaceException {
        assignRPositionFixDataFrame(positionFixList, false);
        assignRPositionFixXYDataFrame();
        Boolean showAbsence = (Boolean) analysis.getParameterValue("showAbsence", true);
        Double gridSize = (Double) analysis.getParameterValue("gridSize", true);
        String colours = (String) analysis.getParameterValue("colours", true);
        if (!(gridSize > 0d)) {
            throw new RserveInterfaceException("grid size must be greater than 0.");
        }
        safeEval("srs <- '" + srs + "'");
        safeEval("gridSize <- " + gridSize);
        safeEval("colours <- '" + colours + "'");
        safeEval("labsent <- " + (showAbsence ? "TRUE" : "FALSE"));
        safeEval("kmlFile <- oztrack_heatmap_line(srs=srs, gridSize=gridSize, colours=colours, labsent=labsent)");
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runKalmanAnalysis(Analysis analysis, List<PositionFix> positionFixList) throws RserveInterfaceException {
        if (analysis.getAnimals().size() > 1) {
            throw new RserveInterfaceException("The Kalman Filter can only be run on one animal at a time.");
        }
        assignRPositionFixDataFrame(positionFixList, false);
        safeEval(
            "kmlFile <- oztrack_kalman(\n" +
            "  sinputfile=" + "positionFix" + ",\n" +
            "  is.AM=" + toRValue(analysis.getProject().getCrosses180()) + ",\n" +
            "  startdate=" + toRValue(analysis.getParameterValue("startDate", false)) + ",\n" +
            "  startX=" + toRValue(analysis.getParameterValue("startX", false)) + ",\n" +
            "  startY=" + toRValue(analysis.getParameterValue("startY", false)) + ",\n"+
            "  enddate=" + toRValue(analysis.getParameterValue("endDate", false)) + ",\n" +
            "  endX=" + toRValue(analysis.getParameterValue("endX", false)) + ",\n" +
            "  endY=" + toRValue(analysis.getParameterValue("endY", false)) + ",\n" +
            "  u.active=" + toRValue(analysis.getParameterValue("uActive", true)) + ",\n" +
            "  v.active=" + toRValue(analysis.getParameterValue("vActive", true)) + ",\n" +
            "  D.active=" + toRValue(analysis.getParameterValue("DActive", true)) + ",\n" +
            "  bx.active=" + toRValue(analysis.getParameterValue("bxActive", true)) + ",\n" +
            "  by.active=" + toRValue(analysis.getParameterValue("byActive", true)) + ",\n" +
            "  sx.active=" + toRValue(analysis.getParameterValue("sxActive", true)) + ",\n" +
            "  sy.active=" + toRValue(analysis.getParameterValue("syActive", true)) + ",\n" +
            "  a0.active=" + toRValue(analysis.getParameterValue("a0Active", true)) + ",\n" +
            "  b0.active=" + toRValue(analysis.getParameterValue("b0Active", true)) + ",\n" +
            "  vscale.active=" + toRValue(analysis.getParameterValue("vscaleActive", true)) + ",\n" +
            "  u.init=" + toRValue(analysis.getParameterValue("uInit", true)) + ",\n" +
            "  v.init=" + toRValue(analysis.getParameterValue("vInit", true)) + ",\n" +
            "  D.init=" + toRValue(analysis.getParameterValue("DInit", true)) + ",\n" +
            "  bx.init=" + toRValue(analysis.getParameterValue("bxInit", true)) + ",\n" +
            "  by.init=" + toRValue(analysis.getParameterValue("byInit", true)) + ",\n" +
            "  sx.init=" + toRValue(analysis.getParameterValue("sxInit", true)) + ",\n" +
            "  sy.init=" + toRValue(analysis.getParameterValue("syInit", true)) + ",\n" +
            "  a0.init=" + toRValue(analysis.getParameterValue("a0Init", true)) + ",\n" +
            "  b0.init=" + toRValue(analysis.getParameterValue("b0Init", true)) + ",\n" +
            "  vscale.init=" + toRValue(analysis.getParameterValue("vscaleInit", true)) + ",\n" +
            "  var.struct=" + toRValue(analysis.getParameterValue("varStruct", true)) + "\n" +
            ")"
        );
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private void runKalmanSstAnalysis(Analysis analysis, List<PositionFix> positionFixList) throws RserveInterfaceException {
        if (analysis.getAnimals().size() > 1) {
            throw new RserveInterfaceException("The Kalman Filter can only be run on one animal at a time.");
        }
        assignRPositionFixDataFrame(positionFixList, true);
        safeEval(
            "kmlFile <- oztrack_kfsst(\n" +
            "  sinputfile=" + "positionFix" + ",\n" +
            "  is.AM=" + toRValue(analysis.getProject().getCrosses180()) + ",\n" +
            "  startdate=" + toRValue(analysis.getParameterValue("startDate", false)) + ",\n" +
            "  startX=" + toRValue(analysis.getParameterValue("startX", false)) + ",\n" +
            "  startY=" + toRValue(analysis.getParameterValue("startY", false)) + ",\n"+
            "  enddate=" + toRValue(analysis.getParameterValue("endDate", false)) + ",\n" +
            "  endX=" + toRValue(analysis.getParameterValue("endX", false)) + ",\n" +
            "  endY=" + toRValue(analysis.getParameterValue("endY", false)) + ",\n" +
            "  u.active=" + toRValue(analysis.getParameterValue("uActive", true)) + ",\n" +
            "  v.active=" + toRValue(analysis.getParameterValue("vActive", true)) + ",\n" +
            "  D.active=" + toRValue(analysis.getParameterValue("DActive", true)) + ",\n" +
            "  bx.active=" + toRValue(analysis.getParameterValue("bxActive", true)) + ",\n" +
            "  by.active=" + toRValue(analysis.getParameterValue("byActive", true)) + ",\n" +
            "  sx.active=" + toRValue(analysis.getParameterValue("sxActive", true)) + ",\n" +
            "  sy.active=" + toRValue(analysis.getParameterValue("syActive", true)) + ",\n" +
            "  a0.active=" + toRValue(analysis.getParameterValue("a0Active", true)) + ",\n" +
            "  b0.active=" + toRValue(analysis.getParameterValue("b0Active", true)) + ",\n" +
            "  bsst.active=" + toRValue(analysis.getParameterValue("bsstActive", true)) + ",\n" +
            "  ssst.active=" + toRValue(analysis.getParameterValue("ssstActive", true)) + ",\n" +
            "  r.active=" + toRValue(analysis.getParameterValue("rActive", true)) + ",\n" +
            "  u.init=" + toRValue(analysis.getParameterValue("uInit", true)) + ",\n" +
            "  v.init=" + toRValue(analysis.getParameterValue("vInit", true)) + ",\n" +
            "  D.init=" + toRValue(analysis.getParameterValue("DInit", true)) + ",\n" +
            "  bx.init=" + toRValue(analysis.getParameterValue("bxInit", true)) + ",\n" +
            "  by.init=" + toRValue(analysis.getParameterValue("byInit", true)) + ",\n" +
            "  sx.init=" + toRValue(analysis.getParameterValue("sxInit", true)) + ",\n" +
            "  sy.init=" + toRValue(analysis.getParameterValue("syInit", true)) + ",\n" +
            "  a0.init=" + toRValue(analysis.getParameterValue("a0Init", true)) + ",\n" +
            "  b0.init=" + toRValue(analysis.getParameterValue("b0Init", true)) + ",\n" +
            "  bsst.init=" + toRValue(analysis.getParameterValue("bsstInit", true)) + ",\n" +
            "  ssst.init=" + toRValue(analysis.getParameterValue("ssstInit", true)) + ",\n" +
            "  r.init=" + toRValue(analysis.getParameterValue("rInit", true)) + "\n" +
            ")"
        );
        moveFile("kmlFile", analysis.getAbsoluteResultFilePath());
    }

    private String toRValue(Object object) {
        if (object == null) {
            return "NULL";
        }
        else if (object instanceof Double) {
            return ((Double) object).toString();
        }
        else if (object instanceof Boolean) {
            return ((Boolean) object) ? "TRUE" : "FALSE";
        }
        else if (object instanceof Date) {
            return "'" + isoDateFormat.format((Date) object) + "'";
        }
        else {
            return "'" + object.toString() + "'";
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
    private REXP safeEval(String rCommand) throws RserveInterfaceException {
        logger.debug(String.format("Evaluating R: %s", rCommand));
        REXP rexp = null;
        try {
            rexp = this.rConnection.eval("e <- tryCatch({" + rCommand + "}, error = function(e) {e})");
        }
        catch (RserveException e) {
            throw new RserveInterfaceException("Error evaluating expression", e);
        }
        String errorMessage = null;
        try {
            if (this.rConnection.eval("inherits(e, 'error')").asInteger() == 1) {
                errorMessage = this.rConnection.eval("conditionMessage(e)").asString();
            }
        }
        catch (Exception e) {
            throw new RserveInterfaceException("Error getting error message", e);
        }
        if (errorMessage != null) {
            throw new RserveInterfaceException(errorMessage);
        }
        return rexp;
    }

    private void moveFile(String remoteFileNameExpr, String localFileName) throws RserveInterfaceException {
        String remoteFileName = null;
        try {
            remoteFileName = safeEval(remoteFileNameExpr).asString();
        }
        catch (REXPMismatchException e) {
            throw new RserveInterfaceException("Error reading remote file name", e);
        }
        try {
            RFileInputStream remoteFileStream = rConnection.openFile(remoteFileName);
            IOUtils.copy(remoteFileStream, new FileOutputStream(localFileName));
        }
        catch (IOException e) {
            throw new RserveInterfaceException("Error reading remote file", e);
        }
        try {
            rConnection.removeFile(remoteFileName);
        }
        catch (RserveException e) {
            throw new RserveInterfaceException("Error removing remote file", e);
        }
    }
}
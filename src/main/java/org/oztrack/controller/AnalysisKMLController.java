package org.oztrack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisParameter;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.AnalysisParameterType;
import org.oztrack.data.model.types.AnalysisType;
import org.oztrack.error.RServeInterfaceException;
import org.oztrack.util.RServeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnalysisKMLController {
    protected final Log logger = LogFactory.getLog(getClass());

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("analysis")
    public void initAnalysisBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("analysis")
    public Analysis getAnalysis(
        Authentication authentication,
        HttpServletRequest request,
        @RequestParam(value="queryType", required=true) AnalysisType analysisType,
        @RequestParam(value="projectId", required=true) Long projectId,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString,
        @RequestParam(value="animalIds", required=false) List<Long> animalIds
    ) {
        Analysis analysis = new Analysis();
        analysis.setProject((projectId != null) ? projectDao.getProjectById(projectId) : null);
        try {
            analysis.setFromDate(StringUtils.isNotBlank(fromDateString) ? dateFormat.parse(fromDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid fromDate", e);
        }
        try {
            analysis.setToDate(StringUtils.isNotBlank(toDateString) ? dateFormat.parse(toDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid toDate", e);
        }
        analysis.setAnalysisType(analysisType);
        Set<Animal> animals = new HashSet<Animal>();
        for (Long animalId : animalIds) {
            animals.add(animalDao.getAnimalById(animalId));
        }
        analysis.setAnimals(animals);
        Set<AnalysisParameter> analysisParameters = new HashSet<AnalysisParameter>();
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        for (AnalysisParameterType analysisParameterType : analysisType.getParameterTypes()) {
            String[] requestParameterValues = requestParameterMap.get(analysisParameterType.getIdentifier());
            String requestParameterValue = (requestParameterValues != null) ? requestParameterValues[0] : null;
            AnalysisParameter analysisParameter = new AnalysisParameter();
            analysisParameter.setAnalysis(analysis);
            analysisParameter.setName(analysisParameterType.getIdentifier());
            analysisParameter.setValue(requestParameterValue);
            analysisParameters.add(analysisParameter);
        }
        analysis.setParameters(analysisParameters);
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        java.util.Date createDate = new java.util.Date();
        analysis.setCreateDate(createDate);
        analysis.setCreateUser(currentUser);
        analysis.setUpdateDate(createDate);
        analysis.setUpdateUser(currentUser);
        return analysis;
    }

    @RequestMapping(value="/analysisKML", method=RequestMethod.GET)
    @PreAuthorize("#analysis.project.global or hasPermission(#analysis.project, 'read')")
    public void handleAnalysisKML(
        Authentication authentication,
        HttpServletResponse response,
        @ModelAttribute(value="analysis") Analysis analysis
    ) {
        writeAnalysisKML(response, analysis);
        if (permissionEvaluator.hasPermission(authentication, analysis.getProject(), "write")) {
            analysisDao.save(analysis);
        }
    }

    private void writeAnalysisKML(HttpServletResponse response, Analysis analysis) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(analysis.toSearchQuery());
        RServeInterface rServeInterface = new RServeInterface();
        File kmlFile = null;
        try {
            kmlFile = rServeInterface.createKml(analysis, positionFixList);
        }
        catch (RServeInterfaceException e) {
            logger.error("RServeInterface exception", e);
            writeKMLQueryErrorResponse(response, e.getMessage());
            response.setStatus(500);
            return;
        }
        String filename = analysis.getAnalysisType().name().toLowerCase(Locale.ENGLISH) + ".kml";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/vnd.google-earth.kml+xml");
        response.setCharacterEncoding("UTF-8");
        FileInputStream kmlInputStream = null;
        try {
            kmlInputStream = new FileInputStream(kmlFile);
            IOUtils.copy(kmlInputStream, response.getOutputStream());
            kmlFile.delete();
        }
        catch (IOException e) {
            writeKMLQueryErrorResponse(response, "Error writing KML response.");
            response.setStatus(500);
            return;
        }
        finally {
            IOUtils.closeQuietly(kmlInputStream);
        }
    }

    private static void writeKMLQueryErrorResponse(HttpServletResponse response, String error) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.append("<?xml version=\"1.0\"?>\n");
        out.append("<map-query-kml-response xmlns=\"http://oztrack.org/xmlns#\">\n");
        out.append("    <error>" + StringUtils.trim(error) + "</error>\n");
        out.append("</map-query-kml-response>\n");
    }
}
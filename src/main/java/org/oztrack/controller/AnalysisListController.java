package org.oztrack.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisParameter;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.AnalysisParameterType;
import org.oztrack.data.model.types.AnalysisStatus;
import org.oztrack.data.model.types.AnalysisType;
import org.oztrack.util.AnalysisRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnalysisListController {
    private final Logger logger = Logger.getLogger(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private AnalysisRunner analysisRunner;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("analysis")
    public void initAnalysisBinder(WebDataBinder binder) {
        binder.setAllowedFields();
        // Disallowing fields shouldn't be needed given say none are allowed above.
        // However, we get BeanPropertyBindingResult errors without this line:
        binder.setDisallowedFields("fromDate", "toDate");
    }

    @ModelAttribute("analysis")
    public Analysis getAnalysis(
        Authentication authentication,
        HttpServletRequest request,
        @PathVariable(value="projectId") Long projectId,
        @RequestParam(value="analysisType", required=true) AnalysisType analysisType,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString,
        @RequestParam(value="animalIds", required=false) List<Long> animalIds
    ) {
        Analysis analysis = new Analysis();
        analysis.setStatus(AnalysisStatus.NEW);
        analysis.setMessage(null);
        analysis.setProject((projectId != null) ? projectDao.getProjectById(projectId) : null);
        try {
            analysis.setFromDate(StringUtils.isNotBlank(fromDateString) ? isoDateFormat.parse(fromDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid fromDate", e);
        }
        try {
            analysis.setToDate(StringUtils.isNotBlank(toDateString) ? isoDateFormat.parse(toDateString) : null);
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
            if (requestParameterValue != null) {
                AnalysisParameter analysisParameter = new AnalysisParameter();
                analysisParameter.setAnalysis(analysis);
                analysisParameter.setName(analysisParameterType.getIdentifier());
                analysisParameter.setValue(requestParameterValue);
                analysisParameters.add(analysisParameter);
            }
        }
        analysis.setParameters(analysisParameters);
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        analysis.setCreateDate(new java.util.Date());
        analysis.setCreateUser(currentUser);
        HttpSession session = request.getSession(false);
        analysis.setCreateSession((session != null) ? session.getId() : null);
        return analysis;
    }

    @RequestMapping(value="/projects/{projectId}/analyses", method=RequestMethod.POST)
    @PreAuthorize("permitAll")
    public void processCreate(
        HttpServletRequest request,
        HttpServletResponse response,
        @ModelAttribute(value="analysis") Analysis analysis
    ) {
        analysisDao.save(analysis);
        analysisRunner.run(analysis.getId());
        String analysisUrl = String.format("%s/projects/%d/analyses/%d", request.getContextPath(), analysis.getProject().getId(), analysis.getId());
        response.setStatus(201);
        response.setHeader("Location", analysisUrl);
    }
}
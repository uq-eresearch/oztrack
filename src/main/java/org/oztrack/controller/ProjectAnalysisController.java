package org.oztrack.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.ProjectVisitDao;
import org.oztrack.data.access.SrsDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectVisit;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.AnalysisType;
import org.oztrack.data.model.types.MapLayerType;
import org.oztrack.data.model.types.ProjectVisitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectAnalysisController {
    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectVisitDao projectVisitDao;

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @Autowired
    private SrsDao srsDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{id}/analysis", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String getView(
        Authentication authentication,
        HttpServletRequest request,
        Model model,
        @ModelAttribute(value="project") Project project
    ) {
        projectVisitDao.save(new ProjectVisit(project, ProjectVisitType.ANALYSIS, new Date()));
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
        model.addAttribute("mapLayerTypeList", MapLayerType.values());
        ArrayList<AnalysisType> analysisTypeList = new ArrayList<AnalysisType>();
        for (AnalysisType analysisType : AnalysisType.values()) {
            analysisTypeList.add(analysisType);
        }
        model.addAttribute("analysisTypeList", analysisTypeList);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("projectBoundingBox", projectDao.getBoundingBox(project, false));
        model.addAttribute("animalBoundingBoxes", projectDao.getAnimalBoundingBoxes(project, false));
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, false));
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        HttpSession currentSession = request.getSession(false);
        String currentSessionId = (currentSession != null) ? currentSession.getId() : null;
        model.addAttribute("savedAnalyses", analysisDao.getSavedAnalyses(project));
        model.addAttribute("previousAnalyses", analysisDao.getPreviousAnalyses(project, currentUser, currentSessionId));
        return "project-analysis.html";
    }
}
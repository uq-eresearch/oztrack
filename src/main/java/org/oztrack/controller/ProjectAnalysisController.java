package org.oztrack.controller;

import java.util.List;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.SrsDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    AnimalDao animalDao;

    @Autowired
    SrsDao srsDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{id}/analysis", method=RequestMethod.GET)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String getView(Model model, @ModelAttribute(value="project") Project project) {
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
        MapQueryType [] mapQueryTypeList = new MapQueryType[] {
            MapQueryType.LINES,
            MapQueryType.POINTS,
            MapQueryType.START_END,
            MapQueryType.MCP,
            MapQueryType.KUD,
            MapQueryType.AHULL,
            MapQueryType.HEATMAP_POINT,
            MapQueryType.HEATMAP_LINE
        };
        model.addAttribute("mapQueryTypeList", mapQueryTypeList);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, false));
        return "project-analysis";
    }
}

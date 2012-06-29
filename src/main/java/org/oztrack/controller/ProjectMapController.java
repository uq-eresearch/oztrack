package org.oztrack.controller;

import java.util.List;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectMapController {
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    AnimalDao animalDao;

    @ModelAttribute("project")
    public Project getProject(@RequestParam(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @RequestMapping(value="/projectmap", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String getView(Model model, @ModelAttribute(value="project") Project project) {
    	MapQueryType [] mapQueryTypeList = MapQueryType.values();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        model.addAttribute("mapQueryTypeList", mapQueryTypeList);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        
        return "projectmap";
    }
}




package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.oztrack.app.Constants;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectMapController {
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    AnimalDao animalDao;
    
    @RequestMapping(value="/projectmap", method=RequestMethod.GET)
    public String getView(HttpSession session, Model model, @RequestParam("id") Long projectId) {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        if (currentUserId == null) {
            return "redirect:login";
        }

        Project project = projectDao.getProjectById(projectId);
    	MapQueryType [] mapQueryTypeList = MapQueryType.values();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        model.addAttribute("project", project);
        model.addAttribute("mapQueryTypeList", mapQueryTypeList);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        
        return "projectmap";
    }
}




package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectMapController {
    @RequestMapping(value="/projectmap", method=RequestMethod.GET)
    public String getView(HttpSession session, Model model, @RequestParam("project_id") Long projectId) {
        User sessionUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (sessionUser == null) {
        	return "redirect:login";
        }

        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
        Project project = projectDao.getProjectById(projectId);
        projectDao.refresh(project);
        
    	MapQueryType [] mapQueryTypeList = MapQueryType.values();
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        model.addAttribute("project", project);
        model.addAttribute("mapQueryTypeList", mapQueryTypeList);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        
        return "projectmap";
    }
}




package org.oztrack.controller;

import java.util.HashMap;
import java.util.List;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsageController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private DataFileDao dataFileDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @RequestMapping(value="/settings/usage", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getView(Model model) {
        List<Project> projects = projectDao.getAll();
        model.addAttribute("users", userDao.getAll());
        model.addAttribute("projects", projects);
        model.addAttribute("projectDao", projectDao);
        model.addAttribute("numAnimals", animalDao.getNumAnimals());
        model.addAttribute("numDataFiles", dataFileDao.getNumDataFiles());
        model.addAttribute("numPositionFixes", positionFixDao.getNumPositionFixes());
        model.addAttribute("speciesList", animalDao.getSpeciesList());
        HashMap<Project, Integer> detectionCount = new HashMap<Project, Integer>();
        for (Project project : projects) {
            detectionCount.put(project, projectDao.getDetectionCount(project, false));
        }
        model.addAttribute("detectionCount", detectionCount);
        return "usage";
    }
}

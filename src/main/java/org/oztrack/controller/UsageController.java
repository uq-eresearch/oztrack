package org.oztrack.controller;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
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
        model.addAttribute("users", userDao.getAll());
        model.addAttribute("projects", projectDao.getAll());
        model.addAttribute("numAnimals", animalDao.getNumAnimals());
        model.addAttribute("numDataFiles", dataFileDao.getNumDataFiles());
        model.addAttribute("numPositionFixes", positionFixDao.getNumPositionFixes());
        return "usage";
    }
}

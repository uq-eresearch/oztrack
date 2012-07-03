package org.oztrack.controller;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.oztrack.validator.ProjectFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProjectListController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private UserDao userDao;
    
    @ModelAttribute("project")
    public Project getProject() {
        Project project = new Project();
        project.setRightsStatement("The data is the property of the University of Queensland. Permission is required to use this material.");
        return project;
    }
    
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String getListView(Model model) {
        return "projects";
    }
    
    @RequestMapping(value="/projects/new", method=RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String getNewView(@ModelAttribute(value="project") Project project) {
        return "projectadd";
    }
    
    @RequestMapping(value="/projects", method=RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public String processCreate(
        Authentication authentication,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult
    ) throws Exception {
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            return "projectadd";
        }
        projectDao.create(project, currentUser);
        return "redirect:/projects/" + project.getId();
    }
}
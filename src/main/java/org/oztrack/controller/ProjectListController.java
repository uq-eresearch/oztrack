package org.oztrack.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.validator.ProjectFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectListController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private UserDao userDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "title",
            "description",
            "projectType",
            "spatialCoverageDescr",
            "speciesCommonName",
            "speciesScientificName",
            "publicationTitle",
            "publicationUrl",
            "isGlobal",
            "rightsStatement"
        );
    }

    @InitBinder("publicProjects")
    public void initPublicProjectsBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder("privateProjects")
    public void initPrivateProjectsBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }
    
    @ModelAttribute("project")
    public Project getProject() {
        Project project = new Project();
        project.setIsGlobal(true);
        project.setRightsStatement("The data is the property of the University of Queensland. Permission is required to use this material.");
        return project;
    }

    @ModelAttribute("publicProjects")
    public List<Project> getPublicProjects() {
        return projectDao.getProjectsByPublished(true);
    }
    
    @ModelAttribute("privateProjects")
    public List<Project> getPrivateProjects() {
        return projectDao.getProjectsByPublished(false);
    }
    
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getListView(Model model) {
        return "projects";
    }
    
    @RequestMapping(value="/projects/new", method=RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String getNewView(@ModelAttribute(value="project") Project project) {
        return "project-form";
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
            return "project-form";
        }
        projectDao.create(project, currentUser);
        return "redirect:/projects/" + project.getId();
    }
}

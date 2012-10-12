package org.oztrack.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.DataLicenceDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.SrsDao;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectListController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SrsDao srsDao;

    @Autowired
    private DataLicenceDao dataLicenceDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "title",
            "description",
            "projectType",
            "spatialCoverageDescr",
            "speciesCommonName",
            "speciesScientificName",
            "srsIdentifier",
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
        project.setSrsIdentifier("EPSG:3577");
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
    public String getNewView(Model model, @ModelAttribute(value="project") Project project) {
        model.addAttribute("srsList", srsDao.getAllOrderedByBoundsAreaDesc());
        model.addAttribute("dataLicences", dataLicenceDao.getAll());
        model.addAttribute("currentYear", (new GregorianCalendar()).get(Calendar.YEAR));
        return "project-form";
    }

    @RequestMapping(value="/projects", method=RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public String processCreate(
        Authentication authentication,
        Model model,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult,
        @RequestParam(value="dataLicenceIdentifier", required=false) String dataLicenceIdentifier
    ) throws Exception {
        if (project.isGlobal() && (dataLicenceIdentifier != null)) {
            project.setDataLicence(dataLicenceDao.getByIdentifier(dataLicenceIdentifier));
        }
        else {
            project.setDataLicence(null);
        }
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("srsList", srsDao.getAllOrderedByBoundsAreaDesc());
            model.addAttribute("dataLicences", dataLicenceDao.getAll());
            model.addAttribute("currentYear", (new GregorianCalendar()).get(Calendar.YEAR));
            return "project-form";
        }
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        projectDao.create(project, currentUser);
        return "redirect:/projects/" + project.getId();
    }
}

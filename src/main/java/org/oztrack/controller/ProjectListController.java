package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.DataLicenceDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.SrsDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.ProjectAccess;
import org.oztrack.util.EmbargoUtils;
import org.oztrack.validator.ProjectFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
    private OzTrackConfiguration configuration;

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
            "spatialCoverageDescr",
            "speciesCommonName",
            "speciesScientificName",
            "srsIdentifier",
            "publicationTitle",
            "publicationUrl",
            "access",
            "embargoDate",
            "rightsStatement"
        );
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @InitBinder("openAccessProjects")
    public void initOpenAccessProjectsBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder("embargoAccessProjects")
    public void initEmbargoAccessProjectsBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder("closedAccessProjects")
    public void initClosedAccessProjectsBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject() {
        Project project = new Project();
        project.setAccess(ProjectAccess.OPEN);
        project.setSrsIdentifier("EPSG:3577");
        return project;
    }

    @ModelAttribute("openAccessProjects")
    public List<Project> getOpenAccessProjects() {
        return projectDao.getProjectsByAccess(ProjectAccess.OPEN);
    }

    @ModelAttribute("embargoAccessProjects")
    public List<Project> getEmbargoAccessProjects() {
        return projectDao.getProjectsByAccess(ProjectAccess.EMBARGO);
    }

    @ModelAttribute("closedAccessProjects")
    public List<Project> getClosedAccessProjects() {
        return projectDao.getProjectsByAccess(ProjectAccess.CLOSED);
    }

    @RequestMapping(value="/projects", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getListView(Model model) {
        return "projects";
    }

    @RequestMapping(value="/projects/new", method=RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String getNewView(Model model, @ModelAttribute(value="project") Project project) {
        addFormAttributes(model);
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
        if (
            OzTrackApplication.getApplicationContext().isDataLicencingEnabled() &&
            (project.getAccess() != ProjectAccess.CLOSED) &&
            (dataLicenceIdentifier != null)
        ) {
            project.setDataLicence(dataLicenceDao.getByIdentifier(dataLicenceIdentifier));
        }
        else {
            project.setDataLicence(null);
        }
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "project-form";
        }
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        projectDao.create(project, currentUser);
        return "redirect:/projects/" + project.getId();
    }

    private void addFormAttributes(Model model) {
        if (configuration.isDataLicencingEnabled()) {
            model.addAttribute("dataLicences", dataLicenceDao.getAll());
        }
        model.addAttribute("srsList", srsDao.getAllOrderedByBoundsAreaDesc());
        model.addAttribute("currentYear", (new GregorianCalendar()).get(Calendar.YEAR));
        model.addAttribute("currentDate", new Date());
        model.addAttribute("dataLicencingEnabled", configuration.isDataLicencingEnabled());
        model.addAttribute("closedAccessDisableDate", configuration.getClosedAccessDisableDate());
        addEmbargoDateFormAttributes(model);
    }

    private void addEmbargoDateFormAttributes(Model model) {
        final Date truncatedCurrentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        final Date truncatedCreateDate = truncatedCurrentDate;

        EmbargoUtils.EmbargoInfo embargoInfo = EmbargoUtils.getEmbargoInfo(truncatedCreateDate);

        model.addAttribute("minEmbargoDate", truncatedCurrentDate);
        model.addAttribute("maxEmbargoDate", embargoInfo.getMaxEmbargoDateNorm());
        model.addAttribute("maxEmbargoYears", EmbargoUtils.maxEmbargoYearsNorm);

        LinkedHashMap<String, Date> presetEmbargoDates = new LinkedHashMap<String, Date>();
        for (int years = 1; years <= EmbargoUtils.maxEmbargoYearsNorm; years++) {
            String key = years + " " + ((years == 1) ? "year" : "years");
            Date value = DateUtils.addYears(truncatedCreateDate, years);
            presetEmbargoDates.put(key, value);
        }
        model.addAttribute("presetEmbargoDates", presetEmbargoDates);
        model.addAttribute("otherEmbargoDate", null);
    }
}

package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.DataLicenceDao;
import org.oztrack.data.access.PersonDao;
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
    private final GregorianCalendar currentCalendar = new GregorianCalendar();

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PersonDao personDao;

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
            "crosses180",
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
        project.setEmbargoDate(DateUtils.addYears(currentCalendar.getTime(), 1));
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
        @RequestParam(value="dataLicenceIdentifier", required=false) String dataLicenceIdentifier,
        HttpServletRequest request
    ) throws Exception {
        // Using @RequestParam for these fails when only one value provided:
        // Spring decides a single value containing commas should be expanded to a list
        // (e.g. ["a, b, c"] becomes ["a", "b", "c"] instead of being interpreted as ["a, b, c"]).
        // Note that two or more values is handled correctly (e.g. ["a, b", "c"]).
        String[] publicationReferenceParam = request.getParameterValues("publicationReference");
        String[] publicationUrlParam = request.getParameterValues("publicationUrl");
        String[] conbtributorIdParam = request.getParameterValues("contributor");

        if (!project.getAccess().equals(ProjectAccess.EMBARGO)) {
            project.setEmbargoDate(null);
            project.setEmbargoNotificationDate(null);
        }
        if ((project.getAccess() != ProjectAccess.CLOSED) && (dataLicenceIdentifier != null)) {
            project.setDataLicence(dataLicenceDao.getByIdentifier(dataLicenceIdentifier));
        }
        else {
            project.setDataLicence(null);
        }
        ProjectController.setProjectPublications(project, bindingResult, publicationReferenceParam, publicationUrlParam);
        ProjectController.setProjectContributions(project, bindingResult, conbtributorIdParam, personDao);
        new ProjectFormValidator(projectDao, null).validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            project.setEmbargoDate(DateUtils.addYears(currentCalendar.getTime(), 1));
            addFormAttributes(model);
            return "project-form";
        }
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        projectDao.create(project, currentUser);
        return "redirect:/projects/" + project.getId();
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("people", personDao.getAllOrderedByName());
        model.addAttribute("dataLicences", dataLicenceDao.getAll());
        model.addAttribute("srsList", srsDao.getAllOrderedByBoundsAreaDesc());
        model.addAttribute("currentYear", currentCalendar.get(Calendar.YEAR));
        model.addAttribute("currentDate", currentCalendar.getTime());
        boolean beforeClosedAccessDisableDate =
            (configuration.getClosedAccessDisableDate() == null) ||
            (currentCalendar.getTime().before(configuration.getClosedAccessDisableDate()));
        model.addAttribute("beforeClosedAccessDisableDate", beforeClosedAccessDisableDate);
        addEmbargoDateFormAttributes(model, currentCalendar.getTime());
    }

    private void addEmbargoDateFormAttributes(Model model, Date currentDate) {
        final Date truncatedCurrentDate = DateUtils.truncate(currentDate, Calendar.DATE);
        final Date truncatedCreateDate = truncatedCurrentDate;

        EmbargoUtils.EmbargoInfo embargoInfo = EmbargoUtils.getEmbargoInfo(truncatedCreateDate, null);

        boolean beforeNonIncrementalEmbargoDisableDate =
            (configuration.getNonIncrementalEmbargoDisableDate() == null) ||
            (currentDate.before(configuration.getNonIncrementalEmbargoDisableDate()));
        model.addAttribute("beforeNonIncrementalEmbargoDisableDate", beforeNonIncrementalEmbargoDisableDate);

        model.addAttribute("minEmbargoDate", truncatedCurrentDate);
        model.addAttribute("maxEmbargoDate", embargoInfo.getMaxEmbargoDate());
        model.addAttribute("maxEmbargoYears", embargoInfo.getMaxEmbargoYears());
        model.addAttribute("maxIncrementalEmbargoDate", embargoInfo.getMaxIncrementalEmbargoDate());

        LinkedHashMap<String, Date> presetEmbargoDates = new LinkedHashMap<String, Date>();
        if (beforeNonIncrementalEmbargoDisableDate) {
            for (int years = 1; years <= embargoInfo.getMaxEmbargoYears(); years++) {
                String key = years + " " + ((years == 1) ? "year" : "years");
                Date value = DateUtils.addYears(truncatedCreateDate, years);
                presetEmbargoDates.put(key, value);
            }
        }
        else {
            presetEmbargoDates.put("Annual renewal", embargoInfo.getMaxIncrementalEmbargoDate());
        }
        model.addAttribute("presetEmbargoDates", presetEmbargoDates);
        model.addAttribute("otherEmbargoDate", null);
    }
}

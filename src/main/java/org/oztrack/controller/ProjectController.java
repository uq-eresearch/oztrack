package org.oztrack.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.DataLicenceDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.SrsDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.Publication;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.ProjectAccess;
import org.oztrack.data.model.types.Role;
import org.oztrack.error.DataSpaceInterfaceException;
import org.oztrack.util.DataSpaceInterface;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectController {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DataFileDao dataFileDao;

    @Autowired
    private AnimalDao animalDao;

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
            "access",
            "rightsStatement"
        );
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{id}", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getDetailView(Model model, @ModelAttribute(value="project") Project project) {
        Role[] roles = Role.values();
        HashMap<Role, List<ProjectUser>> projectUsersByRole = new HashMap<Role, List<ProjectUser>>();
        for (Role role : roles) {
            projectUsersByRole.put(role, projectDao.getProjectUsersWithRole(project, role));
        }
        model.addAttribute("roles", roles);
        model.addAttribute("projectUsersByRole", projectUsersByRole);
        model.addAttribute("projectBoundingBox", projectDao.getBoundingBox(project));
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, false));
        model.addAttribute("projectDetectionCount", projectDao.getDetectionCount(project, false));
        return getView(model, project, "project");
    }

    @RequestMapping(value="/projects/{id}/animals", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String getAnimalsView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "project-animals");
    }

    @RequestMapping(value="/projects/{id}/publish", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'manage')")
    public String getPublishView(
        Model model,
        @ModelAttribute(value="project") Project project,
        HttpServletResponse response
    ) {
        if (!configuration.isDataSpaceEnabled()) {
            response.setStatus(404);
            return null;
        }
        model.addAttribute("projectBoundingBox", projectDao.getBoundingBox(project));
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, false));
        return getView(model, project, "project-publish");
    }

    @RequestMapping(value="/projects/{id}/publish", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#project, 'manage')")
    public String handleRequest(
        Model model,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="action", required=false) String action,
        HttpServletResponse response
    ) throws Exception {
        if (!configuration.isDataSpaceEnabled()) {
            response.setStatus(404);
            return null;
        }
        String errorMessage = "";
        try {
            DataSpaceInterface dsi = new DataSpaceInterface(projectDao, userDao);
            if (action.equals("publish")) {
                dsi.updateDataSpace(project);
            }
            else if (action.equals("delete")) {
                dsi.deleteFromDataSpace(project);
            }
            project = projectDao.getProjectById(project.getId());

        }
        catch (DataSpaceInterfaceException e) {
            errorMessage = e.getMessage();
        }

        model.addAttribute("project", project);
        model.addAttribute("errorMessage", errorMessage);
        return "java_DataSpaceInterface";
    }

    @RequestMapping(value="/projects/{id}/edit", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getEditView(Model model, @ModelAttribute(value="project") Project project) {
        addFormAttributes(model, project);
        return "project-form";
    }

    @RequestMapping(value="/projects/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String processUpdate(
        Authentication authentication,
        Model model,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult,
        @RequestParam(value="embargoDate", required=false) String embargoDateString,
        @RequestParam(value="dataLicenceIdentifier", required=false) String dataLicenceIdentifier,
        HttpServletRequest request
    ) throws Exception {
        // Using @RequestParam for these fails when only one value provided:
        // Spring decides a single value containing commas should be expanded to a list
        // (e.g. ["a, b, c"] becomes ["a", "b", "c"] instead of being interpreted as ["a, b, c"]).
        // Note that two or more values is handled correctly (e.g. ["a, b", "c"]).
        List<String> publicationTitles = Arrays.asList(request.getParameterValues("publicationTitle"));
        List<String> publicationUrls = Arrays.asList(request.getParameterValues("publicationUrl"));

        if (StringUtils.isNotBlank(embargoDateString)) {
            Date embargoDate = isoDateFormat.parse(embargoDateString);
            if (!embargoDate.equals(project.getEmbargoDate())) {
                project.setEmbargoDate(embargoDate);
                project.setEmbargoNotificationDate(null);
            }
        }
        if (
            StringUtils.isNotBlank(dataLicenceIdentifier) &&
            (project.getAccess() != ProjectAccess.CLOSED)
        ) {
            project.setDataLicence(dataLicenceDao.getByIdentifier(dataLicenceIdentifier));
        }
        else {
            project.setDataLicence(null);
        }
        project.getPublications().clear();
        int numPublicationTitles = (publicationTitles != null) ? publicationTitles.size() : 0;
        int numPublicationUrls = (publicationUrls != null) ? publicationUrls.size() : 0;
        int numPublications = Math.max(numPublicationTitles, numPublicationUrls);
        for (int i = 0; i < numPublications; i++) {
            String title = (i < numPublicationTitles) ? publicationTitles.get(i) : null;
            String url = (i < numPublicationUrls) ? publicationUrls.get(i) : null;
            if (StringUtils.isBlank(title)) {
                bindingResult.rejectValue("publications", "publications", "All publications must have a Title.");
            }
            if (StringUtils.isNotBlank(url)) {
                try {
                    // Prepend "http://" to URLs that look like they need it.
                    URI uri = new URI(url);
                    String prefix = "";
                    if (uri.getScheme() == null) {
                        prefix += "http:";
                    }
                    if (uri.getAuthority() == null) {
                        prefix += "//";
                    }
                    url = prefix + url;
                    new URI(url).toURL();
                }
                catch (Exception e) {
                    bindingResult.rejectValue("publications", "publications", "Invalid URL provided: \"" + url + "\".");
                }
            }
            Publication publication = new Publication();
            publication.setProject(project);
            publication.setOrdinal(i);
            publication.setTitle(title);
            publication.setUrl(url);
            project.getPublications().add(publication);
        }
        new ProjectFormValidator().validate(project, bindingResult);
        Project projectWithSameTitle = projectDao.getProjectByTitle(project.getTitle());
        if ((projectWithSameTitle != null) && (projectWithSameTitle.getId() != project.getId())) {
            bindingResult.rejectValue("title", "error.empty.field", "Project with same title already exists.");
        }
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, project);
            return "project-form";
        }
        projectDao.update(project);
        return "redirect:/projects/" + project.getId();
    }

    private void addFormAttributes(Model model, Project project) {
        model.addAttribute("dataLicences", dataLicenceDao.getAll());
        model.addAttribute("srsList", srsDao.getAllOrderedByBoundsAreaDesc());
        model.addAttribute("currentYear", (new GregorianCalendar()).get(Calendar.YEAR));
        model.addAttribute("currentDate", new Date());
        model.addAttribute("closedAccessDisableDate", configuration.getClosedAccessDisableDate());
        addEmbargoDateFormAttributes(model, project);
    }

    private void addEmbargoDateFormAttributes(Model model, Project project) {
        final Date truncatedCurrentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        final Date truncatedCreateDate = DateUtils.truncate(project.getCreateDate(), Calendar.DATE);

        EmbargoUtils.EmbargoInfo embargoInfo = EmbargoUtils.getEmbargoInfo(project.getCreateDate());

        model.addAttribute("minEmbargoDate", truncatedCurrentDate);
        model.addAttribute("maxEmbargoDate", embargoInfo.getMaxEmbargoDate());
        model.addAttribute("maxEmbargoYears", embargoInfo.getMaxEmbargoYears());

        LinkedHashMap<String, Date> presetEmbargoDates = new LinkedHashMap<String, Date>();
        for (int years = 1; years <= embargoInfo.getMaxEmbargoYears(); years++) {
            String key =
                ((years > EmbargoUtils.maxEmbargoYearsNorm) ? "Extension: " : "") +
                years + " " + ((years == 1) ? "year" : "years");
            Date value = DateUtils.addYears(truncatedCreateDate, years);
            presetEmbargoDates.put(key, value);
        }
        model.addAttribute("presetEmbargoDates", presetEmbargoDates);

        // Only set otherEmbargoDate field if it doesn't match any of the presets
        Date otherEmbargoDate = null;
        if (project.getEmbargoDate() != null) {
            otherEmbargoDate = project.getEmbargoDate();
            DateUtils.truncate(project.getEmbargoDate(), Calendar.DATE);
            for (Date presetEmbargoDate : presetEmbargoDates.values()) {
                if (otherEmbargoDate.getTime() == presetEmbargoDate.getTime()) {
                    otherEmbargoDate = null;
                    break;
                }
            }
        }
        model.addAttribute("otherEmbargoDate", otherEmbargoDate);
    }

    @RequestMapping(value="/projects/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#project, 'manage')")
    public void processDelete(@ModelAttribute(value="project") Project project, HttpServletResponse response) {
        if ((project.getDataSpaceURI() != null) && !project.getDataSpaceURI().isEmpty()) {
            response.setStatus(403);
            return;
        }
        projectDao.delete(project);
        response.setStatus(204);
    }

    private String getView(Model model, Project project, String viewName) {
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(project);
        String dataSpaceUrl = configuration.getDataSpaceUrl();
        model.addAttribute("project", project);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("dataFileList", dataFileList);
        model.addAttribute("dataSpaceUrl", dataSpaceUrl);
        return viewName;
    }

    @RequestMapping(value="/projects/{id}/users", method=RequestMethod.POST, produces="application/xml")
    @PreAuthorize("hasPermission(#project, 'manage')")
    public void processAddUser(
        Model model,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="user-id") Long userId,
        @RequestParam(value="role") String role,
        HttpServletResponse response
    ) throws IOException {
        if (userId == null) {
            writeAddUserResponse(response.getWriter(), "No user selected");
            response.setStatus(400);
            return;
        }
        User user = userDao.getById(userId);
        if (user == null) {
            writeAddUserResponse(response.getWriter(), "Invalid user ID supplied");
            response.setStatus(400);
            return;
        }
        for (ProjectUser projectUser : project.getProjectUsers()) {
            if (projectUser.getUser().equals(user)) {
                writeAddUserResponse(response.getWriter(), "Already assigned to project");
                response.setStatus(400);
                return;
            }
        }
        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setRole(Role.fromIdentifier(role));
        project.getProjectUsers().add(projectUser);
        projectDao.save(project);
        writeAddUserResponse(response.getWriter(), null);
        response.setStatus(204);
    }

    private static void writeAddUserResponse(PrintWriter out, String error) {
        out.append("<?xml version=\"1.0\"?>\n");
        out.append("<add-project-user-response xmlns=\"http://oztrack.org/xmlns#\">\n");
        if (error != null) {
            out.append("    <error>" + error + "</error>\n");
        }
        out.append("</add-project-user-response>\n");
    }

    @RequestMapping(value="/projects/{id}/users/{userId}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#project, 'manage')")
    public void processUserDelete(
        @ModelAttribute(value="project") Project project,
        @PathVariable(value="userId") Long userId,
        HttpServletResponse response
    ) throws IOException {
        if (userId == null) {
            writeDeleteUserResponse(response.getWriter(), "No user selected");
            response.setStatus(400);
            return;
        }
        User user = userDao.getById(userId);
        if (user == null) {
            writeDeleteUserResponse(response.getWriter(), "Invalid user ID supplied");
            response.setStatus(400);
            return;
        }
        ProjectUser foundProjectUser = null;
        boolean foundOtherManager = false;
        for (ProjectUser projectUser : project.getProjectUsers()) {
            if (projectUser.getUser().equals(user)) {
                foundProjectUser = projectUser;
            }
            else if (projectUser.getRole() == Role.MANAGER) {
                foundOtherManager = true;
            }
        }
        if (foundProjectUser == null) {
            writeDeleteUserResponse(response.getWriter(), "User not assigned to project");
            response.setStatus(400);
            return;
        }
        if (!foundOtherManager) {
            writeDeleteUserResponse(response.getWriter(), "There must be at least one manager remaining on a project");
            response.setStatus(400);
            return;
        }
        project.getProjectUsers().remove(foundProjectUser);
        projectDao.save(project);
        writeDeleteUserResponse(response.getWriter(), null);
        response.setStatus(204);
    }

    private static void writeDeleteUserResponse(PrintWriter out, String error) {
        out.append("<?xml version=\"1.0\"?>\n");
        out.append("<delete-project-user-response xmlns=\"http://oztrack.org/xmlns#\">\n");
        if (error != null) {
            out.append("    <error>" + error + "</error>\n");
        }
        out.append("</delete-project-user-response>\n");
    }
}

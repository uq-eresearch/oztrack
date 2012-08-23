package org.oztrack.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.SrsDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.oztrack.error.DataSpaceInterfaceException;
import org.oztrack.util.DataSpaceInterface;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectController {
    protected final Log logger = LogFactory.getLog(getClass());

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
        return getView(model, project, "project");
    }

    @RequestMapping(value="/projects/{id}/animals", method=RequestMethod.GET)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String getAnimalsView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "project-animals");
    }

    @RequestMapping(value="/projects/{id}/publish", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'manage')")
    public String getPublishView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "project-publish");
    }

    @RequestMapping(value="/projects/{id}/publish", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#project, 'manage')")
    public String handleRequest(
        Model model,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="action", required=false) String action
    ) throws Exception {
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
        model.addAttribute("srsList", srsDao.getAllOrderedByBoundsAreaDesc());
        return "project-form";
    }

    @RequestMapping(value="/projects/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String processUpdate(
        Authentication authentication,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult
    ) throws Exception {
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            return "project-form";
        }
        projectDao.update(project);
        return "redirect:/projects/" + project.getId();
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
        String dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL();
        model.addAttribute("project", project);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("dataFileList", dataFileList);
        model.addAttribute("dataSpaceURL", dataSpaceURL);
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

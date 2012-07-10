package org.oztrack.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
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
    
    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @RequestMapping(value="/projects/{id}", method=RequestMethod.GET)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String getDetailView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "project");
    }
    
    @RequestMapping(value="/projects/{id}/animals", method=RequestMethod.GET)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String getAnimalsView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "project-animals");
    }
    
    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getCleanseView(Model model, @ModelAttribute(value="project") Project project) {
        return "project-cleanse";
    }
    
    @RequestMapping(value="/projects/{id}/publish", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getPublishView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "project-publish");
    }

    @RequestMapping(value="/projects/{id}/publish", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String handleRequest(
        Model model,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="username", required=false) String username,
        @RequestParam(value="action", required=false) String action
    ) throws Exception {
        Map <String, Object> projectActionMap = new HashMap<String, Object>();
        projectActionMap.put("project", project);
        projectActionMap.put("action", action);
        
        // TODO: DAO should not be passed to view layer.
        model.addAttribute("projectDao", projectDao);
        model.addAttribute("userDao", userDao);
        model.addAttribute("projectActionMap", projectActionMap);
        return "java_DataSpaceInterface";
    }

    @RequestMapping(value="/projects/{id}/edit", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getEditView(@ModelAttribute(value="project") Project project) {
        return "project-form";
    }
    
    @RequestMapping(value="/projects/{id}", method=RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public String processUpdate(
        Authentication authentication,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult
    ) throws Exception {
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            return "project-form";
        }
        projectDao.saveProjectImageFile(project);
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
}

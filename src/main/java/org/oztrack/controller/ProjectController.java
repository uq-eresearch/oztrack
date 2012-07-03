package org.oztrack.controller;

import java.io.File;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProjectController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    ProjectDao projectDao;
    
    @Autowired
    DataFileDao dataFileDao;
    
    @Autowired
    AnimalDao animalDao;
    
    @Autowired
    UserDao userDao;
    
    @ModelAttribute("project")
    public Project getProject(@RequestParam(value="id", required=false) Long projectId) {
        Project project = null;
        if (projectId == null) {
            project = new Project();
            project.setRightsStatement("The data is the property of the University of Queensland. Permission is required to use this material.");
        }
        else {
            project = projectDao.getProjectById(projectId);
        }
        return project;
    }
    
    @RequestMapping(value="/projectdetail", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String getDetailView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "projectdetail");
    }
    
    @RequestMapping(value="/projectdescr", method=RequestMethod.GET)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String getDescriptionView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "projectdescr");
    }
    
    @RequestMapping(value="/publish", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getPublishView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "publish");
    }
    
    @RequestMapping(value="/projectanimals", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")    
    public String getAnimalsView(Model model, @ModelAttribute(value="project") Project project) {
        return getView(model, project, "projectanimals");
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
    
    @RequestMapping(value="/projectadd", method=RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String getFormView(@ModelAttribute(value="project") Project project) {
        return "projectadd";
    }
    
    @RequestMapping(value="/projectadd", method=RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public String processSubmit(
        Authentication authentication,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult,
        @RequestParam(value="update", required=false) String update
    ) throws Exception {
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            return "projectadd";
        }
        if (update != null) {
            saveProjectImageFile(project);
            projectDao.update(project);
        }
        else {
            this.addNewProject(project, currentUser);
        }
        return "redirect:projects";
    }
    
    private void addNewProject(Project project, User currentUser) throws Exception {
        logger.info(" User: " + currentUser.getFullName() + " Creating project: " + project.getTitle() + " " + new java.util.Date().toString());

        // create/update details
        project.setCreateDate(new java.util.Date());
        project.setCreateUser(currentUser);
        project.setDataSpaceAgent(currentUser);
        
        // set the current user to be an admin for this project
        ProjectUser adminProjectUser = new ProjectUser();
        adminProjectUser.setProject(project);
        adminProjectUser.setUser(currentUser);
        adminProjectUser.setRole(Role.ADMIN);
        
        // add this project to the user's list of projects
        List <ProjectUser> userProjectUsers = currentUser.getProjectUsers();
        userProjectUsers.add(adminProjectUser);
        currentUser.setProjectUsers(userProjectUsers);

        // add this user to the project's list of users
        List <ProjectUser> projectProjectUsers = project.getProjectUsers();
        projectProjectUsers.add(adminProjectUser);
        project.setProjectUsers(projectProjectUsers);
        
        // save it all - project first
        projectDao.save(project);
        
        User user = userDao.getUserById(currentUser.getId());
        userDao.update(user);

        project.setDataDirectoryPath("project-" + project.getId().toString());
        saveProjectImageFile(project);
        projectDao.update(project);
    }
    
    private void saveProjectImageFile(Project project) throws Exception {
        MultipartFile file = project.getImageFile();
        if ((file == null) || project.getImageFile().getSize() == 0) {
            return;
        }
        project.setImageFilePath(file.getOriginalFilename());
        File saveFile = new File(project.getAbsoluteImageFilePath());
        saveFile.mkdirs();
        file.transferTo(saveFile);
    }
}

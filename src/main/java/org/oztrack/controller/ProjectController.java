package org.oztrack.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
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
    
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    public String getListView(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (currentUser == null) {
        	return "redirect:login";
        }
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getByUsername(currentUser.getUsername());
        userDao.refresh(user);
        model.addAttribute("user", user);
        return "projects";
    }
    
    @RequestMapping(value="/projectdetail", method=RequestMethod.GET)
    public String getDetailView(HttpSession session, Model model, @RequestParam("project_id") Long projectId) {
        return getProjectView(session, model, projectId, "projectdetail");
    }
    
    @RequestMapping(value="/projectdescr", method=RequestMethod.GET)
    public String getDescriptionView(HttpSession session, Model model, @RequestParam("project_id") Long projectId) {
        return getProjectView(session, model, projectId, "projectdescr");
    }
    
    @RequestMapping(value="/publish", method=RequestMethod.GET)
    public String getPublishView(HttpSession session, Model model, @RequestParam("project_id") Long projectId) {
        return getProjectView(session, model, projectId, "publish");
    }
    
    @RequestMapping(value="/projectanimals", method=RequestMethod.GET)
    public String getAnimalsView(HttpSession session, Model model, @RequestParam("project_id") Long projectId) {
        return getProjectView(session, model, projectId, "projectanimals");
    }
    
    private String getProjectView(HttpSession session, Model model, Long projectId, String viewName) {
        User sessionUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (sessionUser == null) {
            return "redirect:login";
        }
            
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
        Project project = projectDao.getProjectById(projectId);
        
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(project);
        String dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL();
        
        model.addAttribute("project", project);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("dataFileList", dataFileList);
        model.addAttribute("dataSpaceURL", dataSpaceURL);
        
        return viewName;
    }
    
    @ModelAttribute("project")
    public Project getProject(@RequestParam(value="id", required=false) Long projectId) {
        Project project = null;
        if (projectId == null) {
            project = new Project();
            project.setRightsStatement("The data is the property of the University of Queensland. Permission is required to use this material.");
        }
        else {
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            project = projectDao.getProjectById(projectId);
        }
        return project;
    }
    
    @RequestMapping(value="/projectadd", method=RequestMethod.GET)
    public String getFormView(HttpSession session, @ModelAttribute(value="project") Project project) {
        User sessionUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (sessionUser == null) {
            return "redirect:login";
        }
        return "projectadd";
    }
    
    @RequestMapping(value="/projectadd", method=RequestMethod.POST)
    public String processSubmit(
        HttpSession session,
        @ModelAttribute(value="project") Project project,
        BindingResult bindingResult,
        @RequestParam(value="update", required=false) String update
    ) throws Exception {
        User sessionUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (sessionUser == null) {
            return "redirect:login";
        }
        new ProjectFormValidator().validate(project, bindingResult);
        if (bindingResult.hasErrors()) {
            return "projectadd";
        }
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
        if (update != null) {
            if (project.getImageFile().getSize() != 0) {
                project.setImageFileLocation(saveProjectImage(project));
            }
            projectDao.update(project);
        }
        else {
            this.addNewProject(projectDao, project, sessionUser);
        }
        return "redirect:projects";
    }
    
    protected void addNewProject(ProjectDao projectDao, Project project, User currentUser) throws Exception {
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
        
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getUserById(currentUser.getId());
        userDao.update(user);

        project.setDataDirectoryPath(getProjectDirectoryPath(project));
        project.setImageFileLocation(saveProjectImage(project));
        projectDao.update(project);

    }
    
    private String saveProjectImage(Project project) throws Exception {
        MultipartFile file = project.getImageFile();
        String imgFilePath = getProjectDirectoryPath(project) + File.separator + "img" + File.separator + file.getOriginalFilename();
        File saveFile = new File(imgFilePath);
        saveFile.mkdirs();
        file.transferTo(saveFile);
        return imgFilePath;
    }
    
    private String getProjectDirectoryPath(Project project) {
        String dataDir = OzTrackApplication.getApplicationContext().getDataDir();
        if ((dataDir == null) || (dataDir.isEmpty())) {
            dataDir = System.getProperty("user.home");
        }
        return dataDir + File.separator + "oztrack" + File.separator + "project-" + project.getId().toString();
    }
}
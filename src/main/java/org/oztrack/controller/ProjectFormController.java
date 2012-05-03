package org.oztrack.controller;


import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


/**
 * @author uqpnewm5
 */


public class ProjectFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors, Map controlModel) throws Exception {

        User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        
        if (currentUser == null) {
        	return new ModelAndView("redirect:login");
        } else {
        	return super.showForm(request, response, errors, controlModel);    //To change body of overridden methods use File | Settings | File Templates.
    	}
    }
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
    	
        String projectId = request.getParameter("id");
        Project project = null;

            if (projectId != null) {
                ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
                project = projectDao.getProjectById(Long.parseLong(projectId));
            }

    	if (project != null) {
        	return project;
    	
    	} else {
    		
    		Project emptyProject = new Project();
    		emptyProject.setRightsStatement("The data is the property of the University of Queensland. Permission is required to use this material.");
    		return emptyProject;
    	}
    }
    
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        ModelAndView modelAndView;

        if (currentUser == null) {
            String noSessionError = "You need to be logged in to create a project.";
            modelAndView = new ModelAndView("redirect:login");
            modelAndView.addObject("errorMessage", noSessionError);

        } else {

            Project project = (Project) command;
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            // is this an update or an add
            
            //Boolean updating = (request.getParameter("update").equals("true") ? true : false);
            
            if (request.getParameter("update") != null) {
            	if (project.getImageFile().getSize() != 0) {
            		project.setImageFileLocation(saveProjectImage(project));
            	}
            	projectDao.update(project);
            } else {
            	this.addNewProject(projectDao, project, currentUser);
            }

            modelAndView = new ModelAndView(getSuccessView());

        }
        return modelAndView;
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
    
    protected String saveProjectImage(Project project) throws Exception {
    	
	    // image file to file system
	    MultipartFile file = project.getImageFile();
	    String imgFilePath = getProjectDirectoryPath(project) + File.separator + "img" + File.separator
	                     + file.getOriginalFilename();
	    File saveFile = new File(imgFilePath);
	    saveFile.mkdirs();
	    file.transferTo(saveFile);
	    return imgFilePath;
    	
    }
    
    protected String getProjectDirectoryPath(Project project) {
    	
        // set data directory : need the id to sort the path
        String dataDir = OzTrackApplication.getApplicationContext().getDataDir();

        if ((dataDir == null) || (dataDir.isEmpty())) {
            dataDir = System.getProperty("user.home");
        }
        
        return dataDir + File.separator + "oztrack"
                + File.separator + "project-" + project.getId().toString();
        
    }
    
}	
	


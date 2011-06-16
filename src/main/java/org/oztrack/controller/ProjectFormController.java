package org.oztrack.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.types.ProjectType;
import org.oztrack.data.model.types.Role;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author uqpnewm5
 */


public class ProjectFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        ModelAndView modelAndView;

        if (currentUser == null) {
            String noSessionError = "You need to be logged in to create a project.";
            modelAndView = new ModelAndView("login");
            modelAndView.addObject("noSessionError", noSessionError);

        } else {

            Project project = (Project) command;
            logger.info("created project: " + project.getTitle() + " " + new java.util.Date().toString());

            // create/update details
            project.setCreateDate(new java.util.Date());
            project.setCreateUser(currentUser);

            // set the current user to be an admin for this project
            ProjectUser projectUser = new ProjectUser();
            projectUser.setProject(project);
            projectUser.setUser(currentUser);
            projectUser.setRole(Role.ADMIN);

            // add this project to the user's list of projects
            List <ProjectUser> userProjectUsers = currentUser.getProjectUsers();
            userProjectUsers.add(projectUser);
            currentUser.setProjectUsers(userProjectUsers);

            // add this user to the project's list of users
            List <ProjectUser> projectProjectUsers = project.getProjectUsers();
            projectProjectUsers.add(projectUser);
            project.setProjectUsers(projectProjectUsers);

            // save it all - project first
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            projectDao.save(project);

            // sort out the image file - need the id to sort out the file path
            // get the image and throw it on the filesystem
            MultipartFile file = project.getImageFile();

            String dataDir = OzTrackApplication.getApplicationContext().getDataDir();

            if ((dataDir == null) || (dataDir.isEmpty())) {
                logger.debug("dataDir property not set");
                dataDir = System.getProperty("user.home");
            } else {
                logger.debug("dataDir: " + dataDir);
            }

            // save the file to the data dir
            String filePath = dataDir + File.separator + "oztrack" + File.separator
                             + project.getId().toString() + File.separator + "img" + File.separator
                             + file.getOriginalFilename();

            File saveFile = new File(filePath);
            saveFile.mkdirs();
            file.transferTo(saveFile);

            project.setImageFileLocation(filePath);
            projectDao.update(project);


            modelAndView = new ModelAndView(getSuccessView());

        }

            return modelAndView;
    }

}	
	


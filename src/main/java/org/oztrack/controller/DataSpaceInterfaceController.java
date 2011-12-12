package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;



public class DataSpaceInterfaceController implements Controller {

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());
    
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
        // parameters from ajax post
        String projectId = request.getParameter("project");
        String username = request.getParameter("username");
        //User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User currentUser = userDao.getByUsername(username);
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();

        if (currentUser == null) {
        	
        	return new ModelAndView("login");
        	
        } else {
        	
        	if (projectId == null) {
        		
        		return new ModelAndView("projects");
        		
        	} else {
        		
        		Project project = projectDao.getProjectById(Long.parseLong(projectId));
        		logger.info("request for dataspace syndication by user: " + currentUser.getUsername() + " for project: " + project.getTitle());
        		return new ModelAndView("java_DataSpaceInterface", "project", project);
        		
        	}
        }
	}
}

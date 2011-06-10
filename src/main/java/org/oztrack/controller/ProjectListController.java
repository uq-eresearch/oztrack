package org.oztrack.controller;

import org.oztrack.app.AuthenticationManager;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ProjectListController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
//        AuthenticationManager authenticationManager = OzTrackApplication.getApplicationContext().getAuthenticationManager();
//        HttpSession session = httpServletRequest.getSession();
//        User currentUser = authenticationManager.getUserFromSession(session);
        User currentUser = (User) httpServletRequest.getSession().getAttribute(Constants.CURRENT_USER);

        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getByUsername(currentUser.getUsername());

    	// returns ALL projects that this user has access to
    	//List<Project> userProjectList = projectDao.getProjectListByUserId(currentUser.getId());
        List <ProjectUser> userProjectList = user.getProjectUsers();

        ModelAndView modelAndView = new ModelAndView("projects");
        modelAndView.addObject("userProjectList", userProjectList);
        modelAndView.addObject(Constants.CURRENT_USER, currentUser);
        return modelAndView;
    }
}
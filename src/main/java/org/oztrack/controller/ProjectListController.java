package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ProjectListController implements Controller {
    protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User currentUser = (User) httpServletRequest.getSession().getAttribute(Constants.CURRENT_USER);
        ModelAndView modelAndView = null;
        
        if (currentUser == null) {
        	modelAndView = new ModelAndView("redirect:login");
        }
        else {
            UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
            User user = userDao.getByUsername(currentUser.getUsername());
            userDao.refresh(user);
            modelAndView = new ModelAndView("projects");
            modelAndView.addObject("user", user);
        }
        return modelAndView;
    }
}
package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 27/05/11
 * Time: 1:40 PM
 */
public class AllOztrackProjectsController implements Controller {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

         ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
         List<Project> projectList = projectDao.getAll();

         ModelAndView modelAndView = new ModelAndView("allOztrackProjects");
         modelAndView.addObject("projectList", projectList);
         return modelAndView;
    }

}

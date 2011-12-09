package org.oztrack.controller;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectDescrController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

            ModelAndView modelAndView;
            Long projectId = null;
            
        	if (httpServletRequest.getParameter("id") == null) {
            	
        		modelAndView = new ModelAndView("home");
        		
            } else { 
            	
            	projectId = Long.parseLong(httpServletRequest.getParameter("id"));
		        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
		        Project project = projectDao.getProjectById(projectId);
		        projectDao.refresh(project);
		        modelAndView = new ModelAndView("projectdescr");
		        modelAndView.addObject("project", project);
            }
        
        return modelAndView;
	}

}

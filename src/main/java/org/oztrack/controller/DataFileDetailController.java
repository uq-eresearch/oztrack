package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class DataFileDetailController implements Controller {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User sessionUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        
        Long projectId = null;
        if (request.getParameter("project_id") != null) {
            projectId = Long.parseLong(request.getParameter("project_id"));
        }
        Project project = null;
        if (projectId != null) {
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            project = projectDao.getProjectById(projectId);
            projectDao.refresh(project);
        }
        
        ModelAndView modelAndView = new ModelAndView("datafiles");
        if (sessionUser == null) {
        	modelAndView = new ModelAndView("redirect:login");
        }
        else {
            DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        	if (request.getRequestURI().contains("datafiles")) {
        		modelAndView = new ModelAndView("datafiles");
		        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(project);
		        modelAndView.addObject("project", project);
		        modelAndView.addObject("dataFileList", dataFileList);
        	}
        	else if (request.getRequestURI().contains("datafiledetail")) {
        		modelAndView = new ModelAndView("datafiledetail");
	            DataFile dataFile = dataFileDao.getDataFileById(Long.parseLong(request.getParameter("datafile_id")));
	            dataFileDao.refresh(dataFile);
	            modelAndView.addObject("project", project);
	            modelAndView.addObject("dataFile", dataFile);
	        }
        }	
        
        return modelAndView;
    }
}

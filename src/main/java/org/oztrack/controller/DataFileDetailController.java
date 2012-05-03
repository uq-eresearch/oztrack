package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 28/04/11
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataFileDetailController implements Controller {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        ModelAndView modelAndView = new ModelAndView("datafiles");
        String errorMessage = null;
        
        Project sessionProject = (Project) httpServletRequest.getSession().getAttribute("project");
        User sessionUser = (User) httpServletRequest.getSession().getAttribute(Constants.CURRENT_USER);
        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        
        if (sessionUser == null) {
        	
        	modelAndView = new ModelAndView("redirect:login");
        
        } else {

        	if (httpServletRequest.getRequestURI().contains("datafiles")) {
	            
        		modelAndView = new ModelAndView("datafiles");
		        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(sessionProject);
		        modelAndView.addObject("dataFileList", dataFileList);
	        
        	} else if (httpServletRequest.getRequestURI().contains("datafiledetail")) {
	            
        		modelAndView = new ModelAndView("datafiledetail");
	            DataFile dataFile = dataFileDao.getDataFileById(Long.parseLong(httpServletRequest.getParameter("datafile_id")));
	            dataFileDao.refresh(dataFile);
	            modelAndView.addObject("dataFile", dataFile);
	            
	            if (dataFile == null) {
	            	errorMessage = "Couldn't find anything on that file sorry";
	            }
	        }
        }	
        
        return modelAndView;
    }
}

package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ProjectDetailController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //String modelAndViewName = "projectdetail"; 
        ModelAndView modelAndView;
        String errorMessage = null;
        
        User sessionUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        
        if (sessionUser == null) {
        	modelAndView = new ModelAndView("redirect:login");
        }
        else {
            Long projectId = null;
        	if (request.getParameter("project_id") != null) {
            	projectId = Long.parseLong(request.getParameter("project_id"));
            }

	        if (projectId != null) {
	        	
	        	// check that the user has access to this project
	        
		        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
		        Project project = projectDao.getProjectById(projectId);
		        projectDao.refresh(project);
		        
		        String modelAndViewName = "projectdetail";
		        
		        if (request.getRequestURI().contains("projectmap")) {
		            modelAndViewName = "projectmap";
		        } else if (request.getRequestURI().contains("projectanimals")) {
		            modelAndViewName = "projectanimals";
		        } else if (request.getRequestURI().contains("publish")) {
		            modelAndViewName = "publish";
		        }
		        
		        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
		        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
		        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
		        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(project);
		        String dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL();
		        
		        modelAndView = new ModelAndView(modelAndViewName);
		        modelAndView.addObject("errorStr", errorMessage);
		        modelAndView.addObject("project", project);
		        modelAndView.addObject("projectAnimalsList", projectAnimalsList);
		        modelAndView.addObject("dataFileList", dataFileList);
		        modelAndView.addObject("dataSpaceURL",dataSpaceURL);
	        
	        }
	        else {
	        	modelAndView = new ModelAndView("redirect:projects");
	        }
	        
        }
        
        return modelAndView;
	}

}

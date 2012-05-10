package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ProjectMapController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
    	ModelAndView modelAndView; 
    	String errorMessage = "";
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
                ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
                Project project = projectDao.getProjectById(projectId);
                projectDao.refresh(project);
                
        		modelAndView = new ModelAndView("projectmap");
            	MapQueryType [] mapQueryTypeList = MapQueryType.values();
                AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
                List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

                modelAndView.addObject("errorMessage", errorMessage);
                modelAndView.addObject("project", project);
                modelAndView.addObject("mapQueryTypeList", mapQueryTypeList);
                modelAndView.addObject("projectAnimalsList", projectAnimalsList);
            }
            else {
                modelAndView = new ModelAndView("redirect:projects");
            }
        }
        return modelAndView;
    }
}




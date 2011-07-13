package org.oztrack.controller;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.DataFile;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ProjectDetailController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        logger.debug("Parm project_id = " + httpServletRequest.getParameter("project_id"));
        String errorStr = null;

        Long project_id;
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();

        if (httpServletRequest.getParameter("project_id") == null) {
            Project tempProject =  (Project) httpServletRequest.getSession().getAttribute("project");
            project_id = tempProject.getId();
        } else {
            project_id = Long.parseLong(httpServletRequest.getParameter("project_id"));
        }

        Project project = projectDao.getProjectById(project_id);
        httpServletRequest.getSession().setAttribute("project", project);

        if (project ==  null) {
                errorStr = "Couldn't find any project sorry.";
        }

        String modelAndViewName = "projectdetail"; // = httpServletRequest.getRequestURI().replaceAll("/oztrack*/","").split(";")[0];

        if (httpServletRequest.getRequestURI().contains("datafiles")) {
            modelAndViewName = "datafiles";
        } else if (httpServletRequest.getRequestURI().contains("projectmap")) {
            modelAndViewName = "projectmap";
        }

        //EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        //Project projectTest = entityManager.find(Project.class, project.getId());
        //entityManager.refresh(projectTest);
        //projectDao.refresh(project);



        ModelAndView modelAndView = new ModelAndView(modelAndViewName);
        modelAndView.addObject("errorStr", errorStr);
        modelAndView.addObject("project", project);
        return modelAndView;
    }
}

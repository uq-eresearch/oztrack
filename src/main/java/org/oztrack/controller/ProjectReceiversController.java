package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ReceiverDeployment;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 23/05/11
 * Time: 1:57 PM
 */
public class ProjectReceiversController implements Controller {
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        
        List<ReceiverDeployment> receiverList = null;
        String errors = "";
        
        if (project == null) {
            errors = "No project, sorry.";
        }
        else {
            ReceiverDeploymentDao receiverDeploymentDao = OzTrackApplication.getApplicationContext().getDaoManager().getReceiverDeploymentDao();
            receiverList = receiverDeploymentDao.getReceiversByProjectId(project.getId());
        }
    
        ModelAndView modelAndView = new ModelAndView("projectreceivers");
        modelAndView.addObject("receiverList", receiverList);
        modelAndView.addObject("errors", errors);
        modelAndView.addObject("project", project);
        return modelAndView;
        
    }
    
}

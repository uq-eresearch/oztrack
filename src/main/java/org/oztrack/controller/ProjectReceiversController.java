package org.oztrack.controller;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ReceiverDeployment;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 23/05/11
 * Time: 1:57 PM
 */
public class ProjectReceiversController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

    Project project =  (Project) httpServletRequest.getSession().getAttribute("project");
    String errors = "";
    ReceiverDeploymentDao receiverDeploymentDao = OzTrackApplication.getApplicationContext().getDaoManager().getReceiverDeploymentDao();
    List<ReceiverDeployment> receiverList = receiverDeploymentDao.getReceiversByProjectId(project.getId());

    if (project == null) {
        errors = "No project, sorry.";
    }

    ModelAndView modelAndView = new ModelAndView("projectReceivers");
    modelAndView.addObject("receiverList", receiverList);
    modelAndView.addObject("errors", errors);
    modelAndView.addObject("project", project);
    return modelAndView;

    }

}

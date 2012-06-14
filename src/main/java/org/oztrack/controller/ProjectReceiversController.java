package org.oztrack.controller;

import java.util.List;

import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ReceiverDeployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectReceiversController {
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private ReceiverDeploymentDao receiverDeploymentDao;
    
    @ModelAttribute("project")
    public Project getProject(@RequestParam(value="project_id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @ModelAttribute("receiverList")
    public List<ReceiverDeployment> getReceiverList(@ModelAttribute(value="project") Project project) {
        return receiverDeploymentDao.getReceiversByProjectId(project.getId());
    }
    
    @RequestMapping(value="/projectreceivers", method=RequestMethod.GET)
    public String handleRequest() throws Exception {
        return "projectreceivers";
    }
}
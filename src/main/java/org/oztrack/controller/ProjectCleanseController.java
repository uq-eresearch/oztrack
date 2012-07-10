package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectCleanseController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ProjectDao projectDao;
    
    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getCleanseView(@ModelAttribute(value="project") Project project) {
        return "project-cleanse";
    }
    
    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String processCleanse(@ModelAttribute(value="project") Project project, HttpServletRequest request) {
        String[] polygonsWkt = request.getParameterValues("polygon");
        if (polygonsWkt == null) {
            polygonsWkt = new String[] {};
        }
        for (String polygonWkt : polygonsWkt) {
            logger.info("Polygon: " + polygonWkt);
        }
        return "project-cleanse";
    }
}

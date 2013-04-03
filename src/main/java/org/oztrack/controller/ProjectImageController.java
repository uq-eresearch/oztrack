package org.oztrack.controller;

import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.ProjectImageDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectImageController {
    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectImageDao projectImageDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder("projectImage")
    public void initProjectImageBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="projectId") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @ModelAttribute("projectImage")
    public ProjectImage getProjectImage(@PathVariable(value="projectImageId") Long projectImageId) {
        return projectImageDao.getProjectImageById(projectImageId);
    }

    @RequestMapping(value="/projects/{projectId}/images/{projectImageId}/file", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#projectImage.project, 'read')")
    public void handleFileGet(
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="projectImage") ProjectImage projectImage,
        HttpServletResponse response
    ) throws Exception {
        response.setHeader("Content-Disposition", "inline; filename=\"" + projectImage.getOriginalFileName() + "\"");
        response.setContentType(projectImage.getFileMimeType());
        IOUtils.copy(new FileInputStream(projectImage.getAbsoluteFilePath()), response.getOutputStream());
    }

    @RequestMapping(value="/projects/{projectId}/images/{projectImageId}/thumbnail", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#projectImage.project, 'read')")
    public void handleThumbnailGet(
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="projectImage") ProjectImage projectImage,
        HttpServletResponse response
    ) throws Exception {
        response.setContentType(projectImage.getThumbnailMimeType());
        response.setHeader("Content-Disposition", "inline; filename=\"" + projectImage.getOriginalFileName() + "\"");
        IOUtils.copy(new FileInputStream(projectImage.getAbsoluteThumbnailPath()), response.getOutputStream());
    }

    @RequestMapping(value="/projects/{projectId}/images/{projectImageId}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#projectImage.project, 'write')")
    public void handleDelete(
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="projectImage") ProjectImage projectImage,
        HttpServletResponse response
    ) throws Exception {
        projectImageDao.delete(projectImage);
        response.setStatus(204);
    }
}
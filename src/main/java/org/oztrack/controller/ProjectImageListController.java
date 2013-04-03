package org.oztrack.controller;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.ProjectImageDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectImage;
import org.oztrack.data.model.User;
import org.oztrack.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProjectImageListController {
    private final int thumbnailWidth = 130;
    private final int thumbnailHeight = 100;

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectImageDao projectImageDao;

    @Autowired
    private UserDao userDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="projectId") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{projectId}/images", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String handlePost(
        Authentication authentication,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="file") MultipartFile file,
        HttpServletResponse response
    ) throws Exception {
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        ProjectImage projectImage = new ProjectImage();
        projectImage.setProject(project);
        projectImageDao.create(projectImage, currentUser);
        new File(projectImage.getAbsoluteDataDirectoryPath()).mkdirs();
        IOUtils.copy(file.getInputStream(), new FileOutputStream(projectImage.getAbsoluteFilePath()));
        projectImage.setFileMimeType(file.getContentType());
        ImageUtils.resize(file.getInputStream(), new FileOutputStream(projectImage.getAbsoluteThumbnailPath()), thumbnailWidth, thumbnailHeight);
        projectImage.setThumbnailMimeType("image/jpeg");
        projectImage.setOriginalFileName(file.getOriginalFilename());
        projectImageDao.update(projectImage);
        return "redirect:/projects/" + project.getId();
    }
}
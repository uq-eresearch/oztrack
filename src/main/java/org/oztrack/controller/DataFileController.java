package org.oztrack.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.oztrack.app.Constants;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.validator.DataFileFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DataFileController {
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private DataFileDao dataFileDao;
    
    @Autowired
    private UserDao userDao;

    @ModelAttribute("project")
    public Project getProject(@RequestParam(value="project_id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @ModelAttribute("dataFile")
    public DataFile getDataFile(@RequestParam(value="datafile_id", required=false) Long dataFileId) {
        if (dataFileId == null) {
            DataFile dataFile = new DataFile();
            dataFile.setLocalTimeConversionHours(10L);
            return dataFile;
        }
        else {
            return dataFileDao.getDataFileById(dataFileId);
        }
    }
    
    @RequestMapping(value="/datafiles", method=RequestMethod.GET)
    public String handleDataFilesRequest(
        HttpSession session,
        Model model,
        @ModelAttribute(value="project") Project project
    ) throws Exception {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        if (currentUserId == null) {
            return "redirect:login";
        }
        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(project);
        model.addAttribute("dataFileList", dataFileList);
        return "datafiles";
    }
    
    @RequestMapping(value="/datafiledetail", method=RequestMethod.GET)
    public String handleDataFileDetailsRequest(
        HttpSession session,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="dataFile") DataFile dataFile
    ) throws Exception {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        if (currentUserId == null) {
            return "redirect:login";
        }
        return "datafiledetail";
    }
    
    @RequestMapping(value="/datafileadd", method=RequestMethod.GET)
    public String getFormView(
        HttpSession session,
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="dataFile") DataFile dataFile
    ) {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        if (currentUserId == null) {
            return "redirect:login";
        }
        ArrayList <String> fileHeaders = new ArrayList <String>();
        switch (project.getProjectType()) {
            case GPS:
                for (PositionFixFileHeader h : PositionFixFileHeader.values()) {
                    fileHeaders.add(h.toString());
                }
                break;
            default:
                break;
        }
        model.addAttribute("fileHeaders", fileHeaders);
        return "datafileadd";
    }
    
    @RequestMapping(value="/datafileadd", method=RequestMethod.POST)
    public String processSubmit(
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="dataFile") DataFile dataFile,
        BindingResult bindingResult,
        @RequestParam(value="update", required=false) String update
    ) throws Exception {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        if (currentUserId == null) {
            return "redirect:login";
        }
        User currentUser = userDao.getUserById(currentUserId);
        new DataFileFormValidator().validate(dataFile, bindingResult);
        if (bindingResult.hasErrors()) {
            return "datafileadd";
        }
        
        MultipartFile file = dataFile.getFile();
        dataFile.setSingleAnimalInFile(false);
        dataFile.setUserGivenFileName(file.getOriginalFilename());
        dataFile.setContentType(file.getContentType());
        dataFile.setCreateDate(new java.util.Date());
        dataFile.setUpdateDate(new java.util.Date());
        dataFile.setCreateUser(currentUser);
        dataFile.setUpdateUser(currentUser);
        dataFile.setProject(project);
        dataFileDao.save(dataFile);

        String filePath = project.getDataDirectoryPath() + File.separator + "datafile-" + dataFile.getId().toString() + ".csv";

        try {
            File saveFile = new File(filePath);
            saveFile.mkdirs();
            file.transferTo(saveFile);
        }
        catch (IOException e) {
            // usually we should only arrive here if everything crashed during the file upload and we can't write over a failed file
            model.addAttribute("errorMessage", "There was a problem with uploading that file. Please try and create a new one.");
            dataFile.setStatus(DataFileStatus.INACTIVE);
            dataFile.setStatusMessage("There was a problem with this file upload and it has been discarded. Please try again.");
            return "datafileadd";
        }
        
        // ready to go now; poller will pick the job up
        dataFile.setOzTrackFileName(filePath);
        dataFile.setStatus(DataFileStatus.NEW);
        dataFileDao.update(dataFile);
        redirectAttributes.addAttribute("projectId", project.getId());
        return "redirect:datafiles?project_id={projectId}";
    }
}

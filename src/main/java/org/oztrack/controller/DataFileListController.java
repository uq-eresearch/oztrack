package org.oztrack.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DataFileListController {
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private DataFileDao dataFileDao;
    
    @Autowired
    private UserDao userDao;

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="project_id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @ModelAttribute("dataFile")
    public DataFile getDataFile() {
        DataFile dataFile = new DataFile();
        dataFile.setLocalTimeConversionHours(10L);
        return dataFile;
    }
    
    @RequestMapping(value="/projects/{project_id}/datafiles", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String handleDataFilesRequest(
        Model model,
        @ModelAttribute(value="project") Project project
    ) throws Exception {
        List<DataFile> dataFileList = dataFileDao.getDataFilesByProject(project);
        model.addAttribute("dataFileList", dataFileList);
        return "datafiles";
    }
    
    @RequestMapping(value="/projects/{project_id}/datafiles/new", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getNewView(
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="dataFile") DataFile dataFile
    ) {
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
    
    @RequestMapping(value="/projects/{project_id}/datafiles", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String processCreate(
        Authentication authentication,
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="dataFile") DataFile dataFile,
        BindingResult bindingResult
    ) throws Exception {
        User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
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

        dataFile.setDataFilePath("datafile-" + dataFile.getId().toString() + ".csv");
        
        try {
            File saveFile = new File(dataFile.getAbsoluteDataFilePath());
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
        dataFile.setStatus(DataFileStatus.NEW);
        dataFileDao.update(dataFile);
        return "redirect:/datafiles/" + dataFile.getId();
    }
}

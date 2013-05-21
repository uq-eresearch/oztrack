package org.oztrack.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.ArgosClass;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.util.ExcelToCsvConverter;
import org.oztrack.validator.DataFileFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder("dataFile")
    public void initDataFileBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "fileDescription",
            "localTimeConversionRequired",
            "localTimeConversionHours",
            "file"
        );
    }

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
    @PreAuthorize("hasPermission(#project, 'write')")
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
        for (PositionFixFileHeader h : PositionFixFileHeader.values()) {
            fileHeaders.add(h.toString());
        }
        model.addAttribute("fileHeaders", fileHeaders);
        ArrayList<String> argosClassCodes = new ArrayList<String>();
        for (ArgosClass a : ArgosClass.values()) {
            argosClassCodes.add(a.getCode());
        }
        model.addAttribute("argosClassCodes", argosClassCodes.toArray(new String[] {}));
        return "datafile-form";
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
            return "datafile-form";
        }

        MultipartFile uploadedFile = dataFile.getFile();
        dataFile.setSingleAnimalInFile(false);
        dataFile.setUserGivenFileName(uploadedFile.getOriginalFilename());
        dataFile.setContentType(uploadedFile.getContentType());
        dataFile.setCreateDate(new java.util.Date());
        dataFile.setUpdateDate(new java.util.Date());
        dataFile.setCreateUser(currentUser);
        dataFile.setUpdateUser(currentUser);
        dataFile.setProject(project);
        dataFileDao.save(dataFile);

        dataFile.setDataFilePath("datafile-" + dataFile.getId().toString() + ".csv");

        String errorMessage = null;
        try {
            File savedFile = new File(dataFile.getAbsoluteDataFilePath());
            savedFile.getParentFile().mkdirs();
            if (
                uploadedFile.getContentType().equals("text/csv") ||
                uploadedFile.getContentType().equals("text/plain") ||
                uploadedFile.getOriginalFilename().toLowerCase(Locale.ENGLISH).endsWith(".csv") ||
                uploadedFile.getOriginalFilename().toLowerCase(Locale.ENGLISH).endsWith(".txt")
            ) {
                uploadedFile.transferTo(savedFile);
            }
            else if (
                uploadedFile.getContentType().equals("application/vnd.ms-excel") ||
                uploadedFile.getOriginalFilename().toLowerCase(Locale.ENGLISH).endsWith(".xls") ||
                uploadedFile.getOriginalFilename().toLowerCase(Locale.ENGLISH).endsWith(".xlsx")
            ) {
                ExcelToCsvConverter.convertExcelToCsv(uploadedFile.getInputStream(), new FileOutputStream(savedFile));
            }
            else {
                errorMessage = "Files must be in CSV or Excel format.";
            }
        }
        catch (Exception e) {
            errorMessage = "Error uploading file. Please check file and try again.";
        }

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            dataFile.setStatus(DataFileStatus.INACTIVE);
            dataFile.setStatusMessage("There was a problem with this file upload and it has been discarded.");
            return "datafile-form";
        }

        // ready to go now; poller will pick the job up
        dataFile.setStatus(DataFileStatus.NEW);
        dataFileDao.update(dataFile);
        return "redirect:/datafiles/" + dataFile.getId();
    }
}

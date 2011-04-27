package org.oztrack.controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 12/04/11
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.ss.usermodel.*;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.RawAcousticDetection;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.DataFileType;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataFileFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        DataFile dataFile = (DataFile) command;

        // read the file
        MultipartFile file = dataFile.getFile();


        if (file == null) {
            // hmm, that's strange, the user did not upload anything
        } else {
            dataFile.setUserGivenFileName(file.getOriginalFilename());
            dataFile.setContentType(file.getContentType());
            dataFile.setUploadDate(new java.util.Date());
            dataFile.setUploadUser(OzTrackApplication.getApplicationContext().getAuthenticationManager().getCurrentUser().getFullName());
            dataFile.setStatus(DataFileStatus.NEW);
            dataFile.setDataFileType(DataFileType.ACOUSTIC);

            Project project = (Project) request.getSession().getAttribute("project");

            String filePath = System.getenv("OZTRACK_DATA")
                             + dataFile.getDataFileType() + "_"
                             + dataFile.getId().toString() ;

            dataFile.setOzTrackFileName(filePath);
            logger.info("save file : " + filePath);
            File saveFile = new File(filePath);
            file.transferTo(saveFile);
            // poller will pick this up

            // link the datafile to the project in session
            dataFile.setProject(project);
            List<DataFile> dataFiles = project.getDataFiles();
            dataFiles.add(dataFile);
            project.setDataFiles(dataFiles);

            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            projectDao.save(project);

        }




        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        //modelAndView.addObject("numberDetections",numberDetections);
        //modelAndView.addObject("dataFile", dataFile);
        //modelAndView.addObject("rawAcousticDetectionsList", rawAcousticDetectionsList);

        return modelAndView;
    }

}



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
import org.oztrack.data.access.DataFileDao;
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
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        DataFile dataFile = new DataFile();
        dataFile.setLocalTimeConversionHours((long)10);
        return dataFile;
    }


    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        DataFile dataFile = (DataFile) command;

        // read the file
        MultipartFile file = dataFile.getFile();


        if (file == null) {
            // hmm, that's strange, the user did not upload anything
        } else {
            // set dataFile details
            dataFile.setUserGivenFileName(file.getOriginalFilename());
            dataFile.setContentType(file.getContentType());
            dataFile.setUploadDate(new java.util.Date());
            dataFile.setUploadUser(OzTrackApplication.getApplicationContext().getAuthenticationManager().getCurrentUser().getFullName());
            dataFile.setDataFileType(DataFileType.ACOUSTIC);

            // persist at project level
            Project project = (Project) request.getSession().getAttribute("project");
            dataFile.setProject(project);
            List<DataFile> dataFiles = project.getDataFiles();
            dataFiles.add(dataFile);
            project.setDataFiles(dataFiles);

            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            projectDao.save(project);

            // saving file to filesystem: check for data directory in app properties
            String dataDir = OzTrackApplication.getApplicationContext().getDataDir();

            if ((dataDir == null) || (dataDir.isEmpty())) {
                logger.debug("dataDir property not set");
                dataDir = System.getProperty("user.home");
            } else {
                logger.debug("dataDir: " + dataDir);
            }

            // save the file to the data dir
            String filePath = dataDir + File.separator + "oztrack" + File.separator
                             + project.getId().toString() + File.separator
                             + dataFile.getDataFileType().toString().toLowerCase() + File.separator
                             + "oztrack-" + dataFile.getDataFileType().toString().toLowerCase()
                             + "-" + project.getId().toString()
                             + "-" + dataFile.getId().toString() + ".csv";

            File saveFile = new File(filePath);
            saveFile.mkdirs();
            file.transferTo(saveFile);

            logger.info("saved file : " + saveFile.getAbsolutePath());

            // ready to go now; poller will pick the job up
            dataFile.setOzTrackFileName(filePath);
            dataFile.setStatus(DataFileStatus.NEW);
            DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
            dataFileDao.update(dataFile);

        }

        ModelAndView modelAndView = new ModelAndView(getSuccessView());

        return modelAndView;
    }

}



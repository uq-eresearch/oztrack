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
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.RawAcousticDetection;
import org.oztrack.data.model.User;
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

        String dataFileId = request.getParameter("datafile_id");
        DataFile dataFile;

        /* retrying from FAILED data upload (datafiledetail.jsp*/
        if (dataFileId != null) {
            DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
            dataFile = dataFileDao.getDataFileById(Long.parseLong(dataFileId));

        /* empty form */
        } else {
            dataFile = new DataFile();
            dataFile.setLocalTimeConversionHours((long)10);
        }
        return dataFile;

    }


    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors, Map controlModel) throws Exception {

        Project project = (Project) request.getSession().getAttribute("project");

        ModelAndView modelAndView = super.showForm(request, response, errors, controlModel);    //To change body of overridden methods use File | Settings | File Templates.
        modelAndView.addObject("project",project);
        return modelAndView;

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        DataFile dataFile = (DataFile) command;
        MultipartFile file = dataFile.getFile();
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();

        /* from datafiles.jsp : Add a Data File button
        *  or datafiledetail.jsp: Retry link if FAILED */
        String project_id=request.getParameter("project_id");
        logger.debug("adding file to project_id = " + project_id);
        Project project = projectDao.getProjectById(Long.parseLong(project_id));


        if (file == null) {
            // hmm, that's strange, the user did not upload anything
        } else {
            // set dataFile details
            dataFile.setUserGivenFileName(file.getOriginalFilename());
            dataFile.setContentType(file.getContentType());
            dataFile.setCreateDate(new java.util.Date());
            dataFile.setUpdateDate(new java.util.Date());

            //dataFile.setUploadUser(OzTrackApplication.getApplicationContext().getAuthenticationManager().getCurrentUser().getFullName());
            User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
            //dataFile.setUploadUser(currentUser.getFullName() );
            dataFile.setCreateUser(currentUser);
            dataFile.setUpdateUser(currentUser);

            // persist at project level
            //Project project = (Project) request.getSession().getAttribute("project");
            dataFile.setProject(project);
            List<DataFile> dataFiles = project.getDataFiles();
            dataFiles.add(dataFile);
            project.setDataFiles(dataFiles);

            projectDao.save(project);

            // save the file to the data dir
            String filePath = project.getDataDirectoryPath() + File.separator
                             + "datafile-" + dataFile.getId().toString() + ".csv";

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



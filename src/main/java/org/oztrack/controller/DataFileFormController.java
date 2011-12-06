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
import org.oztrack.data.model.types.AcousticFileHeader;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.DataFileType;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.data.model.types.ProjectType;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        ModelAndView modelAndView;
    	User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);
        
        if (currentUser == null) {
        	
        	modelAndView = new ModelAndView("redirect:login");
        	return modelAndView;

        } else {

	    	Project project = (Project) request.getSession().getAttribute("project");
	        
	    	if (project != null) {
	    		
		    	ArrayList <String> fileHeaders = new ArrayList <String>();
		        
		        switch (project.getProjectType()) {
		        	case GPS:
		            	for (PositionFixFileHeader h : PositionFixFileHeader.values()) {
		            		fileHeaders.add(h.toString());
		            	}
		            	break;
		        	case PASSIVE_ACOUSTIC:
		            	for (AcousticFileHeader h : AcousticFileHeader.values()) {
		            		fileHeaders.add(h.toString());
		            	}
		            	break;
		            default:
		            	break;
		        }
	        
		        modelAndView = super.showForm(request, response, errors, controlModel);    //To change body of overridden methods use File | Settings | File Templates.
		        modelAndView.addObject("fileHeaders", fileHeaders);
		        modelAndView.addObject("project",project);
		        return modelAndView;
		        
	    	} else {
	    		modelAndView = new ModelAndView("projects");
	    		return modelAndView;
	    	}
	    	
        
        }
        

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        /* we are here from datafiles.jsp : Add a Data File button
        *  or datafiledetail.jsp: Retry link if FAILED */

    	DataFile dataFile = (DataFile) command;
        MultipartFile file = dataFile.getFile();
        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        String filePath = null;
        ModelAndView modelAndView;
        
        Project project =  (Project) request.getSession().getAttribute("project");
        User currentUser = (User) request.getSession().getAttribute(Constants.CURRENT_USER);

        if ((project != null) && (currentUser != null)) {
        	
	        dataFile.setSingleAnimalInFile(false);
	        dataFile.setUserGivenFileName(file.getOriginalFilename());
	        dataFile.setContentType(file.getContentType());
	        dataFile.setCreateDate(new java.util.Date());
	        dataFile.setUpdateDate(new java.util.Date());
	        dataFile.setCreateUser(currentUser);
	        dataFile.setUpdateUser(currentUser);
	        dataFile.setProject(project);
	        dataFileDao.save(dataFile);
	
	        // save the file to the data directory
	        filePath = project.getDataDirectoryPath() + File.separator
	                         + "datafile-" + dataFile.getId().toString() + ".csv";

	        try {
		        
	        	File saveFile = new File(filePath);
		        saveFile.mkdirs();
		        file.transferTo(saveFile);
		        
	        } catch (IOException e) {
	        	
	        	// usually we should only arrive here if everything crashed during the file upload and we can't write over a failed file
	        	modelAndView = new ModelAndView("redirect:datafileadd");
	        	modelAndView.addObject("errorMessage", "There was a problem with uploading that file. Please try and create a new one.");
	        	dataFile.setStatus(DataFileStatus.INACTIVE);
	        	dataFile.setStatusMessage("There was a problem with this file upload and it has been discarded. Please try again.");
	        }
	        
	        // ready to go now; poller will pick the job up
	        dataFile.setOzTrackFileName(filePath);
	        dataFile.setStatus(DataFileStatus.NEW);
	        dataFileDao.update(dataFile);
	        modelAndView = new ModelAndView(getSuccessView());

        } else {

        	modelAndView = new ModelAndView("redirect:login");
        }
        
        
        
        return modelAndView;
    }

}



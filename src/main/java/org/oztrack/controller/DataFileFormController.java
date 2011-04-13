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
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.oztrack.data.model.DataFile;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class DataFileFormController extends SimpleFormController {

	 /** Logger for this class and subclasses */
     protected final Log logger = LogFactory.getLog(getClass());

	  @Override
       protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

		DataFile dataFile = (DataFile) command;

        MultipartFile file = dataFile.getFile();
        if (file == null) {
           // hmm, that's strange, the user did not upload anything
        } else {
            dataFile.setUserGivenFileName(file.getOriginalFilename());
        }

        // link the datafile to the project in session
        Project project = (Project) request.getSession().getAttribute("project");
        dataFile.setProject(project);
        List<DataFile> dataFiles = project.getDataFiles();
        dataFiles.add(dataFile);
        project.setDataFiles(dataFiles);

        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
        projectDao.save(project);

		ModelAndView modelAndView = new ModelAndView(getSuccessView());
	    modelAndView.addObject("project", project);

	    return modelAndView;
	    }

}



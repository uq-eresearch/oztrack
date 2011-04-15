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
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
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
//            dataFile.setUploadUser(OzTrackApplication.getApplicationContext().getAuthenticationManager().getCurrentUser().getFullName());
        }

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));

        // get headers
        HashMap <String, Integer> headingsMap = new HashMap<String, Integer>();
        String strLine = br.readLine();
        String [] colHeadings =  strLine.split(",");

        for (int i=0; i < colHeadings.length; i++) {

            String heading = colHeadings[i].replaceAll("/","").replaceAll(" ","").toUpperCase();

            if (heading.equals(Constants.DATETIME) ) headingsMap.put(Constants.DATETIME, i);
            if (heading.equals(Constants.ANIMALID) ) headingsMap.put(Constants.ANIMALID, i) ;
            if (heading.equals(Constants.SENSOR1)  ) headingsMap.put(Constants.SENSOR1, i) ;
            if (heading.equals(Constants.UNITS1)   ) headingsMap.put(Constants.UNITS1, i) ;
            if (heading.equals(Constants.RECEIVERID) ) headingsMap.put(Constants.RECEIVERID, i);
        }

        while ((strLine = br.readLine()) != null) {

            colHeadings = strLine.split(",");
            RawAcousticDetection rawAcousticDetection = new RawAcousticDetection();
            rawAcousticDetection.setDatetime(colHeadings[headingsMap.get(Constants.DATETIME)]);
            rawAcousticDetection.setAnimalid(colHeadings[headingsMap.get(Constants.ANIMALID)]);
            rawAcousticDetection.setSensor1(colHeadings[headingsMap.get(Constants.SENSOR1)]);
            rawAcousticDetection.setUnits1(colHeadings[headingsMap.get(Constants.UNITS1)]);
            rawAcousticDetection.setReceiverid(colHeadings[headingsMap.get(Constants.RECEIVERID)]);
            entityManager.persist(rawAcousticDetection);
        }
        transaction.commit();

        RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();
        List<RawAcousticDetection> rawAcousticDetectionsList = rawAcousticDetectionDao.getAll();
        int numberDetections = rawAcousticDetectionDao.getNumberDetections();

        // link the datafile to the project in session
        Project project = (Project) request.getSession().getAttribute("project");
        dataFile.setProject(project);
        List<DataFile> dataFiles = project.getDataFiles();
        dataFiles.add(dataFile);
        project.setDataFiles(dataFiles);

        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
        projectDao.save(project);

        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        modelAndView.addObject("numberDetections",numberDetections);
        modelAndView.addObject("dataFile", dataFile);
        modelAndView.addObject("rawAcousticDetectionsList", rawAcousticDetectionsList);

        return modelAndView;
    }

}



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
import java.util.List;


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
        }


        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String strLine;

        while ((strLine = br.readLine()) != null) {

            String [] cols = strLine.split(",");
            RawAcousticDetection rawAcousticDetection = new RawAcousticDetection();
            rawAcousticDetection.setDatetime(cols[0]);
            rawAcousticDetection.setAnimalid(cols[1]);
            rawAcousticDetection.setSensor1(cols[2]);
            rawAcousticDetection.setUnits1(cols[3]);
            rawAcousticDetection.setReceiverid(cols[4]);
            entityManager.persist(rawAcousticDetection);
        }
        transaction.commit();


        /* hold off xls for now. problems with reading dates.
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        for (Row row : sheet) {
            RawAcousticDetection rawAcousticDetection = new RawAcousticDetection();
            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 0:
                        rawAcousticDetection.setDatetime(Double.toString(cell.getNumericCellValue()));
                        break;
                    case 1:
                        rawAcousticDetection.setAnimalid(cell.toString());
                        break;
                    case 2:
                        rawAcousticDetection.setSensor1(cell.toString());
                        break;
                    case 3:
                        rawAcousticDetection.setUnits1(cell.toString());
                        break;
                    case 4:
                        rawAcousticDetection.setReceiverid(cell.toString());
                        break;
                    default:
                        break;
                }
            }
            entityManager.persist(rawAcousticDetection);
        }
        transaction.commit();
        */


        RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();
        List<RawAcousticDetection> rawAcousticDetectionsList = rawAcousticDetectionDao.getAll();

        // link the datafile to the project in session
        Project project = (Project) request.getSession().getAttribute("project");
        dataFile.setProject(project);
        List<DataFile> dataFiles = project.getDataFiles();
        dataFiles.add(dataFile);
        project.setDataFiles(dataFiles);

        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
        projectDao.save(project);

        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        modelAndView.addObject("dataFile", dataFile);
        modelAndView.addObject("rawAcousticDetectionsList", rawAcousticDetectionsList);

        return modelAndView;
    }

}



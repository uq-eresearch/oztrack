package org.oztrack.controller;

import au.edu.uq.itee.maenad.dataaccess.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.direct.JdbcQuery;
import org.oztrack.data.model.*;
import org.oztrack.data.model.types.ProjectType;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 11:24 AM
  */
public class SearchFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        Project project =  (Project) request.getSession().getAttribute("project");
        SearchQuery searchQuery = (SearchQuery) command;
        searchQuery.setProject(project);
        request.getSession().setAttribute("searchQuery", searchQuery);

 //       List<AcousticDetection> acousticDetections = queryAcousticDetections(searchQuery);

        ModelAndView modelAndView = showForm(request, response, errors);
 //       modelAndView.addObject("acousticDetectionsList", acousticDetections);

        return modelAndView;

    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {

        // handle date format from datepicker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(sdf, true);
        binder.registerCustomEditor(Date.class,editor);


        // bind animalList object
        binder.registerCustomEditor(List.class,"animalList", new CustomCollectionEditor(List.class) {

            @Override
            protected Object convertElement(Object element) {
                String animalId = (String) element;
                logger.debug("Initbinder: animalId: " + animalId);
                //AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
                Animal animal = new Animal();
                animal.setId(Long.valueOf(animalId));
                return animal;
            }
        });

        super.initBinder(request, binder);    //To change body of overridden methods use File | Settings | File Templates.
    }

/*
   //screws up the data
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {

        // add a searchQuery command object
        Project project = (Project) request.getSession().getAttribute("project");
        SearchQuery searchQuery = (SearchQuery) request.getSession().getAttribute("searchQuery");

        if (searchQuery == null) {
            searchQuery = new SearchQuery();
            searchQuery.setProject(project);
            request.getSession().setAttribute("searchQuery",searchQuery);
        }

        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        List<Animal> animalList = animalDao.getAnimalsByProjectId(project.getId());
        searchQuery.setAnimalList(animalList);

        return searchQuery;//super.formBackingObject(request);    //To change body of overridden methods use File | Settings | File Templates.
    }

    */


    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {

        // find the project, add a searchQuery command object
        Project project =  (Project) request.getSession().getAttribute("project");
        SearchQuery searchQuery = (SearchQuery) request.getSession().getAttribute("searchQuery");
        // get a list of animals for the form to use
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        if (searchQuery == null) {
            searchQuery = new SearchQuery();
            searchQuery.setProject(project);
            request.getSession().setAttribute("searchQuery",searchQuery);

        }
        // for pagination
        int offset=0;
        int nbrObjectsPerPage=30;
        int totalCount=0;
        int nbrObjectsThisPage=0;

        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }

        /*
        // in the searchQuery, put the animals being searched on
        // remove the same from the animalList
        if (searchQuery.getAnimalList() == null) {
            List<Animal> animalsInSearchList = animalDao.getAnimalsByProjectId(project.getId());
            searchQuery.setAnimalList(animalsInSearchList);
        }

        for (Animal animal: searchQuery.getAnimalList()) {
            animalsNotInSearchList.remove(animal);

        } */

        ModelAndView modelAndView = new ModelAndView("searchform");
        modelAndView.addObject("searchQuery",searchQuery); // empty searchQuery
        modelAndView.addObject("projectAnimalsList", projectAnimalsList);

        /*
        AcousticDetectionDao acousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getAcousticDetectionDao();
        Page<AcousticDetection> acousticDetectionsPage = acousticDetectionDao.getPage(offset,nbrObjectsPerPage);
        List<AcousticDetection> acousticDetectionsList = acousticDetectionsPage.getObjects();
        nbrObjectsThisPage = acousticDetectionsList.size();

        totalCount=acousticDetectionDao.getTotalCount();


        modelAndView.addObject("acousticDetectionsList", acousticDetectionsList);
        modelAndView.addObject("offset", offset);
        modelAndView.addObject("nbrObjectsPerPage", nbrObjectsPerPage);
        modelAndView.addObject("nbrObjectsThisPage", nbrObjectsThisPage);
        modelAndView.addObject("totalCount", totalCount);
        */

        switch (project.getProjectType()) {
             case PASSIVE_ACOUSTIC:
                 //modelAndView.addObject("acousticDetectionsList", queryAcousticDetections(searchQuery));
                 break;
             case GPS:
                 PositionFixDao positionFixDao = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao();
                 Page<PositionFix> positionFixPage = positionFixDao.getPage(searchQuery,offset, nbrObjectsPerPage);
                 nbrObjectsThisPage = positionFixPage.getObjects().size();
                 totalCount = positionFixPage.getCount();
                 modelAndView.addObject("positionFixList", positionFixPage.getObjects());
                 break;
             default:
                 break;
        }

        modelAndView.addObject("offset", offset);
        modelAndView.addObject("nbrObjectsPerPage", nbrObjectsPerPage);
        modelAndView.addObject("nbrObjectsThisPage", nbrObjectsThisPage);
        modelAndView.addObject("totalCount", totalCount);

        return modelAndView;
        //return super.showForm(request, response, errors);    //To change body of overridden methods use File | Settings | File Templates.

    }














}

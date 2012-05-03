package org.oztrack.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 4/08/11
 * Time: 11:38 AM
 */
public class KMLExportController implements Controller{

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        /* parameters from OpenLayers HTTP request */
        String projectId = request.getParameter("projectId");
        String animalId = request.getParameter("animalId");
        SearchQuery searchQuery = new SearchQuery();

        if ((projectId != null) && (animalId != null))  {
            logger.debug("for projectId: " + projectId);
            
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            Project project = projectDao.getProjectById(Long.valueOf(projectId));
            searchQuery.setProject(project);
            
            AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
            Animal animal = animalDao.getAnimalById(Long.valueOf(animalId));
            ArrayList<Animal> animalList = new ArrayList<Animal>(1); // searchQuery.getAnimalList();
            animalList.add(animal);
            searchQuery.setAnimalList(animalList);
            

        }
        else {
            logger.debug("no projectId or queryType");
        }

        return new ModelAndView("java_KMLExport","searchQuery", searchQuery);
    }



    /*
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

    }
    */
}

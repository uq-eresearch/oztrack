package org.oztrack.controller;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.GmlObjectStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.xml.Encoder;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.*;

import org.oztrack.data.model.types.MapQueryType;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.metal.MetalIconFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.SimpleFormController;

//import java.io.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Vector;






public class ProjectMapController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the request parameters and establish type of Layer required
        logger.debug("Parm project_id = " + request.getParameter("project_id"));
        String errorMessage = null;

        Long project_id;
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();

        if (request.getParameter("project_id") == null) {
            Project tempProject =  (Project) request.getSession().getAttribute("project");
            project_id = tempProject.getId();
        } else {
            project_id = Long.parseLong(request.getParameter("project_id"));
        }

        Project project = projectDao.getProjectById(project_id);
        request.getSession().setAttribute("project", project);

        if (project ==  null) {
                errorMessage = "Couldn't find any project sorry.";
        }

        MapQueryType [] mapQueryTypeList = MapQueryType.values();

        // get a list of animals for the form to use
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        ModelAndView modelAndView = new ModelAndView( "projectmap");
        modelAndView.addObject("errorStr", errorMessage);
        modelAndView.addObject("project", project);
        modelAndView.addObject("mapQueryTypeList", mapQueryTypeList);
        modelAndView.addObject("projectAnimalsList", projectAnimalsList);

        return modelAndView;
    }

}




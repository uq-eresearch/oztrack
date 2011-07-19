package org.oztrack.controller;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.DataFile;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ProjectMapController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

	@Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        logger.debug("Parm project_id = " + httpServletRequest.getParameter("project_id"));
        String errorStr = null;

        Long project_id;
        ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();

        if (httpServletRequest.getParameter("project_id") == null) {
            Project tempProject =  (Project) httpServletRequest.getSession().getAttribute("project");
            project_id = tempProject.getId();
        } else {
            project_id = Long.parseLong(httpServletRequest.getParameter("project_id"));
        }

        Project project = projectDao.getProjectById(project_id);
        httpServletRequest.getSession().setAttribute("project", project);

        if (project ==  null) {
                errorStr = "Couldn't find any project sorry.";
        }

        String modelAndViewName = "projectmap"; // = httpServletRequest.getRequestURI().replaceAll("/oztrack*/","").split(";")[0];

        //EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        //Project projectTest = entityManager.find(Project.class, project.getId());
        //entityManager.refresh(projectTest);
        //projectDao.refresh(project);

        RConnection rConnection = new RConnection();
        REXP rexp = rConnection.eval("R.version.string");
        String rResult = rexp.asString();

        List <PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery().queryProjectPositionFixes(project.getId());

        String [] fieldNames = {"animalid","date","lat","long"};
        RList rPositionFixList = new RList();

        // build arraylists for each column

        int [] animalIds = new int[positionFixList.size()];
        //RList detectionTimes= new RList();
        double [] latitudes= new double[positionFixList.size()];
        double [] longitudes= new double[positionFixList.size()];

        //for (PositionFix positionFix : positionFixList) {
        for (int i=0; i < positionFixList.size(); i++) {

            PositionFix positionFix = positionFixList.get(i);
            animalIds[i] = Integer.parseInt(positionFix.getAnimal().getId().toString());
            latitudes[i] = Double.parseDouble(positionFix.getLatitude());
            longitudes[i] = Double.parseDouble(positionFix.getLongitude());

            //animalIds.add(Integer.parseInt(positionFix.getAnimal().getId().toString()));
            //detectionTimes.add(positionFix.getDetectionTime());
            //latitudes.add(Double.parseDouble(positionFix.getLatitude()));
            //longitudes.add(Double.parseDouble(positionFix.getLongitude()));
        }

        REXPInteger a = new REXPInteger(animalIds);
        REXPDouble lat = new REXPDouble(latitudes);
        REXPDouble lon = new REXPDouble(longitudes);

        rPositionFixList.put("animalid", a);
        //rPositionFixList.put("detectionTime", detectionTimes);
        rPositionFixList.put("latitude", lat);
        rPositionFixList.put("longitude", lon);

        logger.debug("RList created");

        REXP output = null;

        try {
            //RDataFrame rDataFrame = new RDataFrame();
            REXP rDataFrame = REXP.createDataFrame(rPositionFixList);
            rConnection.assign("posfix", rDataFrame);
            output = rConnection.eval("posfix");

        } catch (RserveException e) {
            errorStr = "R problem creating dataframe: " + e.getMessage();
        }


        // try to read the output into a java object

        ModelAndView modelAndView = new ModelAndView(modelAndViewName);
        modelAndView.addObject("errorStr", errorStr);
        modelAndView.addObject("rOutput", output.toDebugString());
        modelAndView.addObject("project", project);
        modelAndView.addObject("rResult", rResult);

        return modelAndView;
    }
}

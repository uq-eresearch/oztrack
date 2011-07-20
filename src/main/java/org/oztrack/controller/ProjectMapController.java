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

import java.text.SimpleDateFormat;
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

        RConnection rConnection = new RConnection();

        REXP rExpVersion = rConnection.eval("R.version.string");
        String rVersion = rExpVersion.asString();

        List <PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery().queryProjectPositionFixes(project.getId());
        RList rInputList = new RList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");

        // build arrays for each column
        int [] animalIds = new int[positionFixList.size()];
        String [] detectionTimes= new String[positionFixList.size()];
        double [] latitudes= new double[positionFixList.size()];
        double [] longitudes= new double[positionFixList.size()];

        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            animalIds[i] = Integer.parseInt(positionFix.getAnimal().getId().toString());
            detectionTimes[i] = sdf.format(positionFix.getDetectionTime());
            latitudes[i] = Double.parseDouble(positionFix.getLatitude());
            longitudes[i] = Double.parseDouble(positionFix.getLongitude());
        }

        // add them to the RList to become the dataframe (add the name+array)
        rInputList.put("detectionTime", new REXPString(detectionTimes));
        rInputList.put("animalid", new REXPInteger(animalIds));
        rInputList.put("latitude", new REXPDouble(latitudes));
        rInputList.put("longitude", new REXPDouble(longitudes));

        logger.debug("RList created");

        RList rOutputList = new RList();

        try {
            REXP rDataFrame = REXP.createDataFrame(rInputList);
            rConnection.eval("posfix <- NULL");
            rConnection.assign("posfix", rDataFrame);
            rOutputList = rConnection.eval("posfix").asList();
            rConnection.close();
        } catch (RserveException e) {
            errorStr = "R problem : " + e.getMessage();
        } catch (REXPMismatchException e) {
            errorStr = "R problem : " + e.toString();
        }

        // read the output into a 2 dim String array
        int cols = rOutputList.size();
        int rows = rOutputList.at(0).length();
        String [][] s = new String[cols][];

        for (int i=0;i < cols; i++)
            s[i] = rOutputList.at(i).asStrings();

        ModelAndView modelAndView = new ModelAndView( "projectmap");
        modelAndView.addObject("origDetectionTimes", detectionTimes);
        modelAndView.addObject("errorStr", errorStr);
        modelAndView.addObject("rOutput",s );
        modelAndView.addObject("project", project);
        modelAndView.addObject("rVersion", rVersion);

        return modelAndView;
    }
}

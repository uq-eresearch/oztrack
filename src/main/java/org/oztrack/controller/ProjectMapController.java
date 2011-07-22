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

        List <PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery().queryProjectPositionFixes(project.getId());
        List <Animal> animalList = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao().getAnimalsByProjectId(project.getId());
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        //String [] detectionTimes= new String[positionFixList.size()];

        // build arrays for each column
        int [] animalIds = new int[positionFixList.size()];
        double [] latitudes= new double[positionFixList.size()];
        double [] longitudes= new double[positionFixList.size()];
        HashSet<Long> hashSet = new HashSet<Long>();

        // data list
        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            //detectionTimes[i] = sdf.format(positionFix.getDetectionTime());
            animalIds[i] = Integer.parseInt(positionFix.getAnimal().getId().toString());
            latitudes[i] = Double.parseDouble(positionFix.getLatitude());
            longitudes[i] = Double.parseDouble(positionFix.getLongitude());

            if (!hashSet.contains(positionFix.getAnimal().getId())) {
                hashSet.add(positionFix.getAnimal().getId());
            }
        }

        // animal reference list
        int j=0;
        int [] animalIdRef = new int[hashSet.size()];
        String [] animalNameRef = new String[hashSet.size()];
        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            Animal animal = animalDao.getAnimalById((Long)iterator.next());
            animalIdRef[j] = animal.getId().intValue();
            animalNameRef[j] = animal.getAnimalName();
            j++;
        }

        // add them to the RList to become the dataframe (add the name+array)
        //rInputList.put("detectionTime", new REXPString(detectionTimes));
        RList rPositionFixList = new RList();
        RList rAnimalRefList = new RList();
        rPositionFixList.put("Id", new REXPInteger(animalIds));
        rPositionFixList.put("X", new REXPDouble(latitudes));
        rPositionFixList.put("Y", new REXPDouble(longitudes));
        rAnimalRefList.put("Id", new REXPInteger(animalIdRef));
        rAnimalRefList.put("Name", new REXPString(animalNameRef));

        logger.debug("RList for shapeFile created");

        RConnection rConnection = new RConnection();
        RList rPosFixOutputList = new RList();
        RList rAnimalOutputList = new RList();
        String rLog = null;

        try {

            REXP rExpVersion = rConnection.eval("R.version.string");
            rLog = rExpVersion.asString();

            rConnection.eval("library(adehabitatHR);library(adehabitatMA);library(maptools);library(shapefiles)");
            rLog = rLog + " | Libraries Loaded";

            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            REXP rAnimalRefDataFrame = REXP.createDataFrame(rAnimalRefList);
            rConnection.eval("positionfix <- NULL");
            rConnection.eval("animalref <- NULL");
            rConnection.assign("positionfix", rPosFixDataFrame);
            rConnection.assign("animalref", rAnimalRefDataFrame);
            rLog = rLog + " | Data frames assigned";

            rPosFixOutputList = rConnection.eval("positionfix").asList();
            rAnimalOutputList = rConnection.eval("animalref").asList();

            String evalString = "javaTestShp <- convert.to.shapefile(positionfix,animalref,\"Id\",1)";
            rLog = rLog + " | Create shapeFile using : " + evalString;
            rConnection.eval(evalString);
            rLog = rLog + " | Shapefile created" + evalString;
            rConnection.eval("write.shapefile(javaTestShp, \"D:/test/R/javaTestShp\", arcgis=T)");
            rLog = rLog + " | Shapefile written";

            rConnection.close();

        } catch (RserveException e) {
            errorStr = "RserveException : " + e.toString() + "Log: " +rLog;
        } catch (REXPMismatchException e) {
            errorStr = "REXPMismatchException : " + e.toString()  + "Log: " +rLog;
        }

        // debugging: read the output into a 2 dim String array
        //int rows = rOutputList.at(0).length();
        Vector posFixNames = rPosFixOutputList.names;
        Vector animalRefNames = rAnimalOutputList.names;

        String [][] s = new String[rPosFixOutputList.size()][];
        for (int i=0;i < rPosFixOutputList.size() ; i++)
            s[i] = rPosFixOutputList.at(i).asStrings();

        String [][] a = new String[rAnimalOutputList.size()][];
        for (int i=0;i < rAnimalOutputList.size(); i++)
             a[i] = rAnimalOutputList.at(i).asStrings();


        ModelAndView modelAndView = new ModelAndView( "projectmap");
        modelAndView.addObject("errorStr", errorStr);
        modelAndView.addObject("rData",s );
        modelAndView.addObject("rAnimals",a );
        modelAndView.addObject("posFixNames",posFixNames );
        modelAndView.addObject("animalRefNames",animalRefNames );
        modelAndView.addObject("project", project);

        return modelAndView;
    }
}

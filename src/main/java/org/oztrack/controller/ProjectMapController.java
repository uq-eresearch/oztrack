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
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.DataFile;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RFileInputStream;
import org.rosuda.REngine.Rserve.RserveException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.metal.MetalIconFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

/*
        List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery().queryProjectPositionFixes(project.getId());
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

        logger.debug("RList for shapeFile created containing record count: " + rPositionFixList.size());

        RConnection rConnection = new RConnection();
        RList rPosFixOutputList = new RList();
        RList rAnimalOutputList = new RList();
        String rLog = null;

        String dataDir = OzTrackApplication.getApplicationContext().getDataDir();
        if ((dataDir == null) || (dataDir.isEmpty())) {
                logger.debug("dataDir property not set");
                dataDir = System.getProperty("user.home");
            } else {
                logger.debug("dataDir: " + dataDir);
            }

        String spatialDirPath = dataDir + File.separator + "oztrack" + File.separator
                         + "project-" + project.getId().toString() + File.separator + "spatial" + File.separator;
                         //+ "shapefile" + dataFile.getId().toString() + ".csv";
        File spatialDir = new File(spatialDirPath);
        spatialDir.mkdirs();

        //File shapeFile = new File()
        String shapeFilePath = spatialDir.getAbsolutePath().replace("\\","/") + "/points";
        //File kmlFile = new File(spatialDirPath + "points.kml");
        String spatialWorkingDir = spatialDir.getAbsolutePath().replace("\\","/");
        //FileOutputStream out = new FileOutputStream(kmlFile);


        try {

            rConnection.eval("library(adehabitatHR);library(adehabitatMA);library(maptools);library(rgdal);library(shapefiles)");
            rLog = rLog + " | Libraries Loaded";

            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            REXP rAnimalRefDataFrame = REXP.createDataFrame(rAnimalRefList);
            rConnection.assign("positionfix", new REXPNull());
            rConnection.assign("animalref",new REXPNull());
            rConnection.assign("positionfix", rPosFixDataFrame);
            rConnection.assign("animalref", rAnimalRefDataFrame);
            rLog = rLog + " | Data frames assigned";

            rPosFixOutputList = rConnection.eval("positionfix").asList();
            rAnimalOutputList = rConnection.eval("animalref").asList();

            // shapefile
            String rCommand = "javaTestShp <- convert.to.shapefile(positionfix,animalref,\"Id\",1)";
            rLog = rLog + " | Create shapeFile: " + rCommand;
            rConnection.eval(rCommand);
            rLog = rLog + " | Shapefile created" + rCommand;
            rCommand = "write.shapefile(javaTestShp,\"" + shapeFilePath + "\",arcgis=T)";
            logger.debug(rCommand);
            rConnection.eval(rCommand);
            rLog = rLog + " | Shapefile written";
            logger.debug(rLog);

            // kml
            rCommand = "coordinates(positionfix) <- c(\"Y\",\"X\");proj4string(positionfix)=CRS(\"+init=epsg:4326\")";
            rLog = rLog + "coordinates + projection defined for KML";
            logger.debug(rCommand);
            rConnection.eval(rCommand);

            rCommand = "writeOGR(positionfix, dsn=\"D:/oztrack_related/points.kml\", layer= \"positionfix\", driver=\"KML\", dataset_options=c(\"NameField=Name\"))";
            logger.debug(rCommand);
            rConnection.eval(rCommand);
            rLog = rLog + "KML written";

            rConnection.close();

        } catch (RserveException e) {
            errorStr = "RserveException : " + e.toString() + "Log: " +rLog;
        } catch (REXPMismatchException e) {
            errorStr = "REXPMismatchException : " + e.toString()  + "Log: " +rLog;
        }

        File kmlFile = new File("D:\\oztrack_related\\points.kml");
        FileInputStream fileIn = new FileInputStream(kmlFile);
        byte [] fileContent = new byte[(int) kmlFile.length()];
        fileIn.read(fileContent);
        fileIn.close();

        String kml = new String(fileContent, "UTF-8");



        // geotools shapefile to kml
        FileDataStore fileDataStore = FileDataStoreFinder.getDataStore(new File(spatialDirPath + "points.shp"));
        SimpleFeatureSource simpleFeatureSource = fileDataStore.getFeatureSource();
        SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();
        Encoder encoder = new Encoder(new KMLConfiguration());
        encoder.setIndenting(true);
        encoder.encode(simpleFeatureCollection, KML.kml, out);

       // debugging: read the output into a 2 dim String array
        //int rows = rOutputList.at(0).length();
        Vector posFixNames = rPosFixOutputList.names;
        Vector animalRefNames = rAnimalOutputList.names;

        String [][] posfixin = new String[rPositionFixList.size()][];
        for (int i=0;i < rPositionFixList.size() ; i++)
            posfixin[i] = rPositionFixList.at(i).asStrings();

        String [][] animalin = new String[rAnimalRefList.size()][];
        for (int i=0;i < rAnimalRefList.size(); i++)
             animalin[i] = rAnimalRefList.at(i).asStrings();

        String [][] posfixout = new String[rPosFixOutputList.size()][];
        for (int i=0;i < rPosFixOutputList.size() ; i++)
            posfixout[i] = rPosFixOutputList.at(i).asStrings();

        String [][] animalout = new String[rAnimalOutputList.size()][];
        for (int i=0;i < rAnimalOutputList.size(); i++)
             animalout[i] = rAnimalOutputList.at(i).asStrings();

        ModelAndView modelAndView = new ModelAndView( "projectmap");
        modelAndView.addObject("errorStr", errorStr);
        modelAndView.addObject("rDataIn",posfixin );
        modelAndView.addObject("rAnimalsIn",animalin );
        modelAndView.addObject("rDataOut",posfixout );
        modelAndView.addObject("rAnimalsOut",animalout );
        modelAndView.addObject("posFixNames",posFixNames );
        modelAndView.addObject("animalRefNames",animalRefNames );
        modelAndView.addObject("project", project);
        modelAndView.addObject("pointsKml",kml);
        return modelAndView;
    */
        return new ModelAndView("projectmap");
    }
}

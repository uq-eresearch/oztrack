package org.oztrack.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.error.RServeInterfaceException;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/08/11
 * Time: 2:06 PM
 */
public class RServeInterface {

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());

    /* inputs
    private List<PositionFix> positionFixList;
    private MapQueryType mapQueryType;
    private String outFileName;
    */

    private RConnection rConnection;
    private SearchQuery searchQuery;
    private String rLog;

    public RServeInterface() {
    }

    /*
    public RServeInterface(List<PositionFix> positionFixList, MapQueryType mapQueryType, String outFileName) {
       // this.positionFixList = positionFixList;
        this.mapQueryType = mapQueryType;
        this.outFileName = outFileName;
    }
    */

    public File createKml(SearchQuery searchQuery) throws RServeInterfaceException {

        this.searchQuery = searchQuery;
        startRConnection();

        // find project directory path to write the file to
        Project project = this.searchQuery.getProject();
        String fileName = project.getDataDirectoryPath() + File.separator + searchQuery.getMapQueryType() + ".kml";

        // project type determines what sort of dataframe to build
        switch (this.searchQuery.getProject().getProjectType()) {
            case GPS:
                createRPositionFixDataFrame();
                break;
            default:
                throw new RServeInterfaceException("Unhandled Project Type: " + project.getProjectType().toString());
        }

        // mapQueryType tells us what R function to call
        switch (this.searchQuery.getMapQueryType()) {
            case ALL_POINTS:
                writePositionFixKmlFile(fileName);
                break;
            default:
                throw new RServeInterfaceException("Unhandled MapQueryType: " + searchQuery.getMapQueryType());
        }

        rConnection.close();
        return new File(fileName);


    }

    /*
    public void createPositionFixKml() throws RServeInterfaceException {

        startRConnection();
        createRPositionFixDataFrame();

        switch (this.mapQueryType) {
            case ALL_POINTS:
                //writePositionFixKmlFile();
                break;
            default:
                throw new RServeInterfaceException("Unhandled MapQueryType: " + this.mapQueryType);
        }
        rConnection.close();
    }
    */


    protected void startRConnection() throws RServeInterfaceException {

        try {
            this.rConnection = new RConnection();
            this.rConnection.eval("library(adehabitatHR);library(adehabitatMA);library(maptools);library(rgdal);library(shapefiles)");
            rLog = rLog + " | Libraries Loaded ";
        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString());
        }
    }

    protected void createRPositionFixDataFrame() throws RServeInterfaceException {

        List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery().queryProjectPositionFixes(this.searchQuery);

        int [] animalIds = new int[positionFixList.size()];
        double [] latitudes= new double[positionFixList.size()];
        double [] longitudes= new double[positionFixList.size()];
        String [] detectionTimes= new String[positionFixList.size()];

        /* load up the arrays from the database result set*/
        for (int i=0; i < positionFixList.size(); i++) {
            PositionFix positionFix = positionFixList.get(i);
            detectionTimes[i] = "foo"; //sdf.format(positionFix.getDetectionTime());
            animalIds[i] = Integer.parseInt(positionFix.getAnimal().getId().toString());
            latitudes[i] = Double.parseDouble(positionFix.getLatitude());
            longitudes[i] = Double.parseDouble(positionFix.getLongitude());
        }

        /* create the RList to become the dataFrame (add the name+array) */
        RList rPositionFixList = new RList();
        rPositionFixList.put("Id", new REXPInteger(animalIds));
        rPositionFixList.put("X", new REXPDouble(latitudes));
        rPositionFixList.put("Y", new REXPDouble(longitudes));
        rPositionFixList.put("Foo", new REXPString(detectionTimes));


        /* assign the dataFrame */
        try {
            REXP rPosFixDataFrame = REXP.createDataFrame(rPositionFixList);
            this.rConnection.assign("positionFix", new REXPNull());
            this.rConnection.assign("positionFix", rPosFixDataFrame);
            rLog = rLog + " | PositionFix dataFrame assigned ";
        } catch (REXPMismatchException e) {
             throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        } catch (RserveException e) {
             throw new RServeInterfaceException(e.toString()+ "Log: " + rLog);
        }
    }

    protected void writePositionFixKmlFile(String fileName) throws RServeInterfaceException {

        String rCommand;
        String outFileNameFix = fileName.replace("\\","/"); /* for R in windows */

        try {

            rCommand = "coordinates(positionFix) <- c(\"Y\",\"X\");proj4string(positionFix)=CRS(\"+init=epsg:4326\")";
            rLog = rLog + "coordinates + projection defined for KML";
            logger.debug(rCommand);
            rConnection.eval(rCommand);

            REXP foo = rConnection.eval("positionFix");

            rCommand = "writeOGR(positionFix, dsn=\"" + outFileNameFix + "\", layer= \"positionFix\", driver=\"KML\", dataset_options=c(\"NameField=Name\"))";
            logger.debug(rCommand);
            rConnection.eval(rCommand);
            rLog = rLog + "KML written";

        } catch (RserveException e) {
            throw new RServeInterfaceException(e.toString() + "Log: " + rLog);
        }

    }






}

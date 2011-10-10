package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.model.*;
import org.oztrack.error.FileProcessingException;
import java.util.*;

import static org.oztrack.util.OzTrackUtil.removeDuplicateLinesFromFile;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 21/04/11
 * Time: 8:59 AM
 */
public class DataFileLoader {
    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());
    protected DataFile dataFile;
    protected DataFileDao dataFileDao;

    public DataFileLoader(DataFile dataFile) {

        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        DataFile thisDataFile = dataFileDao.getDataFileById(dataFile.getId());
        this.dataFileDao=dataFileDao;
        this.dataFile=thisDataFile;
    }

    public void process() throws FileProcessingException {

        removeDuplicateLinesFromFile(this.dataFile.getOzTrackFileName());
        insertRawObservations();
        checkAnimals();
        checkReceivers();
        createFinalObservations();

    }

    public void insertRawObservations() throws FileProcessingException {};
    public void checkReceivers() throws FileProcessingException {};

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public void checkAnimals() throws FileProcessingException {

        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();


        if (dataFile.getSingleAnimalInFile()) {
            // only one animal in the file being uploaded. Create it.
            Animal animal = new Animal();
            animal.setProject(dataFile.getProject());
            animal.setAnimalName("unknown");
            animal.setAnimalDescription("created in datafile upload: "
                                        + dataFile.getUserGivenFileName()
                                        + " on " + dataFile.getCreateDate());

            animal.setCreateDate(new java.util.Date());
            animalDao.save(animal);
            animal.setProjectAnimalId(animal.getId().toString());
            animalDao.update(animal);

        } else {

            // get a list of the animal IDs in the raw file just loaded
            List<String> newAnimalIdList = this.dataFileDao.getAllAnimalIds(this.dataFile);
            // all the animals for this project
            List<Animal> projectAnimalList = animalDao.getAnimalsByProjectId(this.dataFile.getProject().getId());
            boolean animalFound;
            String projectAnimalId;

            for (String newAnimalId  : newAnimalIdList) {
                 animalFound=false;
                 for (Animal projectAnimal : projectAnimalList) {
                     projectAnimalId = projectAnimal.getProjectAnimalId();
                     if (newAnimalId.equals(projectAnimalId))   {
                         animalFound=true;
                     }
                 }

                 if (!animalFound) {
                     Animal animal = new Animal();
                     animal.setAnimalName("Unknown");
                     animal.setAnimalDescription("Unknown");
                     animal.setSpeciesName("Unknown");
                     animal.setVerifiedSpeciesName("Unknown");
                     animal.setProjectAnimalId(newAnimalId);
                     animal.setProject(dataFile.getProject());
                     animal.setCreateDate(new java.util.Date());
                     // TODO:
                     // name = transmitter name
                     // transmitterID = transmitter SN where sensor1 is null
                     // sensorTransmitterID= transmitter SN where sensor1 is not null
                     // transmitter type code = dependent on how sensor works (C=temp; m=depth?)
                    animalDao.save(animal);
                 }
            }
        }
    }

    public void createFinalObservations() throws FileProcessingException {

        JdbcAccess jdbcAccess = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcAccess();

        int nbrObservationsCreated = 0;
        try {

            nbrObservationsCreated = jdbcAccess.loadObservations(dataFile);
            dataFile.setNumberDetections(nbrObservationsCreated);
            dataFileDao.update(dataFile);

            //int projectUpdated = jdbcAccess.setProjectBoundingBox(dataFile.getProject());
            int projectUpdated = jdbcAccess.updateProjectMetadata(dataFile.getProject());
            if (projectUpdated != 1) {
                throw new FileProcessingException("Problem recalculating project metadata - bounding box or start and end dates.");
            }
            jdbcAccess.truncateRawObservations(dataFile);

        } catch (Exception e) {
            jdbcAccess.truncateRawObservations(dataFile);
            throw new FileProcessingException(e.toString());
        }
    }


}
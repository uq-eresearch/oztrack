package org.oztrack.data.loader;

import static org.oztrack.util.OzTrackUtil.removeDuplicateLinesFromFile;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.error.FileProcessingException;

public abstract class DataFileLoader {
    protected final Log logger = LogFactory.getLog(getClass());
    
    protected DataFile dataFile;
    protected DataFileDao dataFileDao;
    protected EntityManager entityManager;
    
    private AnimalDao animalDao;
    private JdbcAccess jdbcAccess;

    public DataFileLoader(DataFile dataFile, DataFileDao dataFileDao, AnimalDao animalDao, EntityManager entityManager, JdbcAccess jdbcAccess) {
        this.dataFile = dataFileDao.getDataFileById(dataFile.getId());
        this.dataFileDao = dataFileDao;
        this.animalDao = animalDao;
        this.entityManager = entityManager;
        this.jdbcAccess = jdbcAccess;
    }

    public void process() throws FileProcessingException {
        removeDuplicateLinesFromFile(this.dataFile.getOzTrackFileName());
        processRawObservations();
        processFinalObservations();
    }

    private void processRawObservations() throws FileProcessingException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            logger.info("Inserting raw observations for " + this.dataFile.getOzTrackFileName());
            createRawObservations();
            logger.info("Checking animals for " + this.dataFile.getOzTrackFileName());
            checkAnimals();
            logger.info("Checking data for " + this.dataFile.getOzTrackFileName());
            checkData();
            transaction.commit();
        }
        catch (FileProcessingException e) {
            transaction.rollback();
            throw e;
        }
    }

    private void processFinalObservations() throws FileProcessingException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            logger.info("Creating final observations for " + this.dataFile.getOzTrackFileName());
            createFinalObservations();
            logger.info("Updating data file metdata for " + this.dataFile.getOzTrackFileName());
            updateDataFileMetadata();
            transaction.commit();
        }
        catch (FileProcessingException e) {
            transaction.rollback();
            throw e;
        }
    }

    public abstract void createRawObservations() throws FileProcessingException;

    private void checkAnimals() throws FileProcessingException {
        if (dataFile.getSingleAnimalInFile()) {
            // only one animal in the file being uploaded. Create it.
            Animal animal = new Animal();
            animal.setProject(dataFile.getProject());
            animal.setAnimalDescription("created in datafile upload: "
                                        + dataFile.getUserGivenFileName()
                                        + " on " + dataFile.getCreateDate());

            animal.setCreateDate(new java.util.Date());
            animalDao.save(animal);
            animal.setProjectAnimalId(animal.getId().toString());
            animal.setAnimalName("Animal_" + animal.getId().toString());
            animalDao.update(animal);
        }
        else {
        	// get a list of the animal IDs in the raw file just loaded
            List<String> newAnimalIdList = this.dataFileDao.getAllAnimalIds(this.dataFile);
            
            if (newAnimalIdList.size() > 20) {
            	
            	throw new FileProcessingException(
            	    "OzTrack only allows 20 animals per file. The Id/Animal Id field in the " +
            		"file contains more than 20. An easy fix may be to rename the Id field to something else. " +
            		"Fix and retry the upload."
        		);
            }
            
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
                     animal.setAnimalName("Animal_" + newAnimalId);
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
    
    public void checkData() throws FileProcessingException {
    }

    private void createFinalObservations() throws FileProcessingException {
        int nbrObservationsCreated = 0;
        try {
            nbrObservationsCreated = jdbcAccess.loadObservations(dataFile);
            
            dataFile.setDetectionCount(nbrObservationsCreated);
            dataFileDao.update(dataFile);

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
    
    public void updateDataFileMetadata() throws FileProcessingException {
    }
}
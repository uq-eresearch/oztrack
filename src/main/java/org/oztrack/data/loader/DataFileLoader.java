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
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.error.FileProcessingException;

public abstract class DataFileLoader {
    protected final Log logger = LogFactory.getLog(getClass());

    protected DataFile dataFile;
    protected DataFileDao dataFileDao;
    protected EntityManager entityManager;

    private AnimalDao animalDao;
    private PositionFixDao positionFixDao;
    private JdbcAccess jdbcAccess;

    public DataFileLoader(
        DataFile dataFile,
        DataFileDao dataFileDao,
        AnimalDao animalDao,
        PositionFixDao positionFixDao,
        EntityManager entityManager,
        JdbcAccess jdbcAccess
    ) {
        this.dataFile = dataFileDao.getDataFileById(dataFile.getId());
        this.dataFileDao = dataFileDao;
        this.animalDao = animalDao;
        this.positionFixDao = positionFixDao;
        this.entityManager = entityManager;
        this.jdbcAccess = jdbcAccess;
    }

    public void process() throws FileProcessingException {
        removeDuplicateLinesFromFile(this.dataFile.getAbsoluteDataFilePath());
        processRawObservations();
        processFinalObservations();
    }

    private void processRawObservations() throws FileProcessingException {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            logger.info("Inserting raw observations for " + this.dataFile.getAbsoluteDataFilePath());
            createRawObservations();
            logger.info("Checking animals for " + this.dataFile.getAbsoluteDataFilePath());
            checkAnimals();
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
            logger.info("Creating final observations for " + this.dataFile.getAbsoluteDataFilePath());
            createFinalObservations();
            transaction.commit();
        }
        catch (FileProcessingException e) {
            transaction.rollback();
            throw e;
        }
    }

    public abstract void createRawObservations() throws FileProcessingException;

    private final String[] colours = new String[] {
        "#8DD3C7",
        "#FFFFB3",
        "#BEBADA",
        "#FB8072",
        "#80B1D3",
        "#FDB462",
        "#B3DE69",
        "#FCCDE5",
        "#D9D9D9",
        "#BC80BD",
        "#CCEBC5",
        "#FFED6F"
    };

    private void checkAnimals() throws FileProcessingException {
        if (dataFile.getSingleAnimalInFile()) {
            // only one animal in the file being uploaded. Create it.
            Animal animal = new Animal();
            animal.setProject(dataFile.getProject());
            animal.setCreateDescription("Created from data file " + dataFile.getUserGivenFileName() + " on " + dataFile.getCreateDate());
            animal.setCreateDate(new java.util.Date());
            animalDao.save(animal);
            animal.setProjectAnimalId(animal.getId().toString());
            animal.setAnimalName(animal.getId().toString());
            animal.setColour(colours[(int) (animal.getId() % colours.length)]);
            animalDao.update(animal);
        }
        else {
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
                     animal.setProjectAnimalId(newAnimalId);
                     animal.setAnimalName(newAnimalId);
                     animal.setAnimalDescription(null);
                     animal.setSpeciesName(null);
                     animal.setProject(dataFile.getProject());
                     animal.setCreateDate(new java.util.Date());
                     animalDao.save(animal);
                     animal.setColour(colours[(int) (animal.getId() % colours.length)]);
                     animalDao.update(animal);
                 }
            }
        }
    }

    private void createFinalObservations() throws FileProcessingException {
        try {
            jdbcAccess.loadObservations(dataFile);
            dataFileDao.update(dataFile);
            jdbcAccess.truncateRawObservations(dataFile);
            positionFixDao.renumberPositionFixes(dataFile.getProject());
        }
        catch (Exception e) {
            jdbcAccess.truncateRawObservations(dataFile);
            throw new FileProcessingException(e.getMessage(), e);
        }
    }
}
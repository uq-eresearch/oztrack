package org.oztrack.data.loader;

import java.io.File;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.impl.AnimalDaoImpl;
import org.oztrack.data.access.impl.DataFileDaoImpl;
import org.oztrack.data.access.impl.JdbcAccessImpl;
import org.oztrack.data.access.impl.PositionFixDaoImpl;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.error.FileProcessingException;
import org.springframework.beans.factory.annotation.Autowired;

public class DataFileRunner {
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    private DataSource dataSource;
    
    public DataFileRunner() {
    }
    
    public void processNext() {        // Initialise our entity manager and DAOs (no Spring injection here)
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        DataFileDaoImpl dataFileDao = new DataFileDaoImpl();
        dataFileDao.setEntityManger(entityManager);
        
        AnimalDaoImpl animalDao = new AnimalDaoImpl();
        animalDao.setEntityManger(entityManager);

        PositionFixDaoImpl positionFixDao = new PositionFixDaoImpl();
        positionFixDao.setEntityManger(entityManager);
        positionFixDao.setDataSource(dataSource);
        
        JdbcAccessImpl jdbcAccess = new JdbcAccessImpl();
        jdbcAccess.setDataSource(dataSource);
        
        // Only proceed if we have a new data file. 
        DataFile nextDataFile = null;
        try {
            nextDataFile = dataFileDao.getNextDataFile();
            if (nextDataFile == null) {
                return;
            }
        }
        catch (Exception e) {
            logger.error("Exception checking for new data file", e);
            return;
        }

        // Run the data file loader
        
        Long dataFileId = nextDataFile.getId();
        
        EntityTransaction startTransaction = entityManager.getTransaction();
        startTransaction.begin();
        try {
        	nextDataFile.setStatus(DataFileStatus.PROCESSING);
        	dataFileDao.update(nextDataFile);
        	startTransaction.commit();
        }
        catch (Exception e) {
            logger.error("Exception processing data file " + dataFileId, e);
            startTransaction.rollback();
        }

    	try {
        	try {
                switch (nextDataFile.getProject().getProjectType()) {
                    case GPS:
                        PositionFixFileLoader positionFixFileLoader = new PositionFixFileLoader(nextDataFile, dataFileDao, animalDao, entityManager, jdbcAccess, positionFixDao);
                        positionFixFileLoader.process();
                        break;
                    default:
                        break;
                }

                EntityTransaction finishTransaction = entityManager.getTransaction();
                finishTransaction.begin();
                DataFile completeDataFile = dataFileDao.getDataFileById(dataFileId);
                completeDataFile.setStatus(DataFileStatus.COMPLETE);
                String statusMessage = "File processing successfully completed on " + (new Date()).toString() + ".";
                if (completeDataFile.getLocalTimeConversionRequired()) {
                    statusMessage += " Local time conversion is " + completeDataFile.getLocalTimeConversionHours() + " hours.";
                }
                completeDataFile.setStatusMessage(statusMessage);
                dataFileDao.update(completeDataFile);
                finishTransaction.commit();
                
                logger.info("Completed processing file " + dataFileId);
            }
        	catch (FileProcessingException e) {
        	    logger.info("File processing exception", e);
        	    
        	    EntityTransaction failureTransaction = entityManager.getTransaction();
                failureTransaction.begin();
                DataFile failureDataFile = dataFileDao.getDataFileById(dataFileId);
            	failureDataFile.setStatus(DataFileStatus.FAILED);
                failureDataFile.setStatusMessage(e.getMessage());
                dataFileDao.update(failureDataFile);
                failureTransaction.commit();

                File file = new File(failureDataFile.getAbsoluteDataFilePath());
                File origFile = new File(failureDataFile.getAbsoluteDataFilePath().replace(".csv",".orig"));
                file.delete();
                origFile.delete();
                
                //JdbcAccess jdbcAccess = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcAccess();
                //jdbcAccess.truncateRawObservations(dataFile);
            }
        }
        catch (Exception e) {
            logger.error("Exception processing data file " + dataFileId, e);
        }
    }
}
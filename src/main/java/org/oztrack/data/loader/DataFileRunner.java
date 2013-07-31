package org.oztrack.data.loader;

import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.oztrack.data.access.impl.AnimalDaoImpl;
import org.oztrack.data.access.impl.DataFileDaoImpl;
import org.oztrack.data.access.impl.JdbcAccessImpl;
import org.oztrack.data.access.impl.PositionFixDaoImpl;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.types.DataFileStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class DataFileRunner {
    private final Logger logger = Logger.getLogger(getClass());

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DataSource dataSource;

    public DataFileRunner() {
    }

    public void processNext() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        DataFileDaoImpl dataFileDao = new DataFileDaoImpl();
        dataFileDao.setEntityManger(entityManager);

        AnimalDaoImpl animalDao = new AnimalDaoImpl();
        animalDao.setEntityManger(entityManager);

        PositionFixDaoImpl positionFixDao = new PositionFixDaoImpl();
        positionFixDao.setEntityManger(entityManager);

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
            try {
                startTransaction.rollback();
            }
            catch (Exception e2) {
            }
            throw e;
        }

        try {
            PositionFixFileLoader positionFixFileLoader = new PositionFixFileLoader(nextDataFile, dataFileDao, animalDao, positionFixDao, entityManager, jdbcAccess);
            positionFixFileLoader.process();

            EntityTransaction finishTransaction = entityManager.getTransaction();
            finishTransaction.begin();
            try {
                DataFile completeDataFile = dataFileDao.getDataFileById(dataFileId);
                completeDataFile.setStatus(DataFileStatus.COMPLETE);
                String statusMessage = "File processing successfully completed on " + (new Date()).toString() + ".";
                if (completeDataFile.getLocalTimeConversionRequired()) {
                    String hoursStr = new DecimalFormat("0.##").format(completeDataFile.getLocalTimeConversionHours());
                    statusMessage += " Local time conversion is " + hoursStr + " hours.";
                }
                completeDataFile.setStatusMessage(statusMessage);
                dataFileDao.update(completeDataFile);
                finishTransaction.commit();
            }
            catch (Exception e1) {
                try {
                    finishTransaction.rollback();
                }
                catch (Exception e2) {
                }
                throw e1;
            }

            logger.info("Completed processing file " + dataFileId);
        }
        catch (Exception e) {
            logger.info("Exception processing data file " + dataFileId, e);

            entityManager.clear();
            EntityTransaction failureTransaction = entityManager.getTransaction();
            failureTransaction.begin();
            try {
                DataFile failureDataFile = dataFileDao.getDataFileById(dataFileId);
                failureDataFile.setStatus(DataFileStatus.FAILED);
                failureDataFile.setStatusMessage(e.getMessage());
                dataFileDao.update(failureDataFile);
                failureTransaction.commit();
            }
            catch (Exception e1) {
                try {
                    failureTransaction.rollback();
                }
                catch (Exception e2) {
                }
                throw e1;
            }

            //JdbcAccess jdbcAccess = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcAccess();
            //jdbcAccess.truncateRawObservations(dataFile);
        }
    }
}
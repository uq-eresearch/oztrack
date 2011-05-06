package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.*;
import org.oztrack.data.model.types.DataFileHeader;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.DataFileType;
import org.oztrack.error.FileProcessingException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 21/04/11
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataFileLoader {
    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    public void processNext() {

        // get the next datafile waiting to be processed if there's not one processing at the moment
        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        DataFile dataFile = dataFileDao.getNextDataFile();

        if (dataFile != null) {

            dataFile.setStatus(DataFileStatus.PROCESSING);
            dataFileDao.update(dataFile);
            dataFileDao.refresh(dataFile);

            if (dataFile.getDataFileType().equals(DataFileType.ACOUSTIC)) {

                try {

                    // get the file into a raw table
                    int nbrRowsProcessed = processRawAcoustic(dataFile);
                    dataFile.setNumberRawDetections(nbrRowsProcessed);

                    checkAnimalsExist(dataFile);
                    checkReceiversExist(dataFile);
                    createDetections(dataFile);

                    dataFile.setStatus(DataFileStatus.COMPLETE);
                    dataFile.setStatusMessage("File processing successfully completed.");

                } catch (FileProcessingException e) {
                    dataFile.setStatus(DataFileStatus.FAILED);
                    dataFile.setStatusMessage(e.toString());
                }

                dataFileDao.save(dataFile);
                dataFileDao.refresh(dataFile);

            } else if (dataFile.getDataFileType().equals(DataFileType.POSITION_FIX)) {
                processRawPositionFix(dataFile);
            }


        }

    }

    public int processRawAcoustic(DataFile dataFile) throws FileProcessingException {

        int lineNumber = 0;
        logger.info("processing raw acoustic file : " + dataFile.getOzTrackFileName());

        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(dataFile.getOzTrackFileName());
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.");
        }

         DataInputStream in = new DataInputStream(fileInputStream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));


        /* Populate a HashMap containing which columns contains which bits of data
         eg.  column 1 DATETIME
              column 2 ANIMALID    */

        HashMap<Integer, DataFileHeader> headerMap = new HashMap<Integer, DataFileHeader>();
        String strLine;


        try {
            strLine = br.readLine();
            lineNumber++;
        } catch (IOException e) {
            throw new FileProcessingException("Problem reading file.");
        }

        String[] colHeadings = strLine.split(",");
        String[] dataRow;

        // determine which columns contain which data
        for (int i = 0; i < colHeadings.length; i++) {

            String heading = colHeadings[i].replaceAll("/", "").replaceAll(" ", "").toUpperCase();
            boolean headingFound = false;

            for (DataFileHeader dataFileHeader : DataFileHeader.values()) {
                if (heading.equals(dataFileHeader.toString())) {
                    headerMap.put(i, dataFileHeader);
                    headingFound = true;
                }
            }
            if (!headingFound) {
                logger.debug("Unknown Heading in file: " + heading);
                throw new FileProcessingException("Unknown Heading in file: " + heading);
            }
        }

        logger.debug("File opened + header read.");


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
               while ((strLine = br.readLine()) != null) {

                lineNumber++;
                dataRow = strLine.split(",");
                RawAcousticDetection rawAcousticDetection = new RawAcousticDetection();

                // loop through the dataRow elements
                for (int i = 0; i < dataRow.length; i++) {

                    // if data in this cell
                    if (dataRow[i] != null || !dataRow[i].isEmpty()) {

                        // retrieve the header for this column and use it to determine which field to update
                        DataFileHeader dataFileHeader = headerMap.get(i);

                        switch (dataFileHeader) {
                            case DATETIME:
                                try {
                                  rawAcousticDetection.setDatetime(sdf.parse(dataRow[i]));
                                } catch (ParseException e) {
                                    transaction.rollback();
                                    throw new FileProcessingException("Incorrect date format on line "+ lineNumber +". Must be 2007-11-27 12:57:00");
                                }
                                break;
                            case ID:
                                rawAcousticDetection.setAnimalid(dataRow[i]);
                                break;
                            case SENSOR1:
                                if (!dataRow[i].isEmpty()) {
                                 try {
                                     rawAcousticDetection.setSensor1(Double.parseDouble(dataRow[i]));
                                 } catch (NumberFormatException e) {
                                     transaction.rollback();
                                     throw new FileProcessingException("Sensor1 value is not a number on line " + lineNumber);
                                 }
                                }
                                break;
                            case UNITS1:
                                rawAcousticDetection.setUnits1(dataRow[i]);
                                break;
                            case CODESPACE:
                                rawAcousticDetection.setCodespace(dataRow[i]);
                                break;
                            case UNITS2:
                                rawAcousticDetection.setUnits2(dataRow[i]);
                                break;
                            case SENSOR2:
                                if (!dataRow[i].isEmpty()) {
                                 try {
                                     rawAcousticDetection.setSensor2(Double.parseDouble(dataRow[i]));
                                 } catch (NumberFormatException e) {
                                     transaction.rollback();
                                     throw new FileProcessingException("Sensor2 value is not a number on line " + lineNumber);
                                 }
                                }
                                break;
                            case TRANSMITTERNAME:
                                rawAcousticDetection.setTransmittername(dataRow[i]);
                                break;
                            case TRANSMITTERSN:
                                rawAcousticDetection.setTransmittersn(dataRow[i]);
                                break;
                            case RECEIVERNAME:
                                rawAcousticDetection.setReceivername(dataRow[i]);
                                break;
                            case RECEIVERSN:
                                rawAcousticDetection.setReceiversn(dataRow[i]);
                                break;
                            case STATIONNAME:
                                rawAcousticDetection.setStationname(dataRow[i]);
                                break;
                            case STATIONLATITUDE:
                                rawAcousticDetection.setStationlatitude(dataRow[i]);
                                break;
                            case STATIONLONGITUDE:
                                rawAcousticDetection.setStationlongitude(dataRow[i]);
                                break;
                            default:
                                logger.debug("Problem in switch(dataFileHeader)");
                                break;
                        }
                    }
                }

                entityManager.persist(rawAcousticDetection);

            }

        } catch (IOException e) {
             transaction.rollback();
             throw new FileProcessingException("Problem reading file at line number :" + lineNumber);
        }
        // commit after reading whole file
        transaction.commit();
        logger.debug(lineNumber + "rows persisted");
        return lineNumber;
    }

    public void processRawPositionFix(DataFile dataFile) {
        logger.info("processing a raw position fix file : " + dataFile.getOzTrackFileName());

    }

    public void checkAnimalsExist(DataFile dataFile)  {

        // the Daos
        RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();

        // the new animals
        List<String> newAnimalIdList = rawAcousticDetectionDao.getAllAnimalIds();
        // all the animals for this project
        List<Animal> projectAnimalList = animalDao.getAnimalsByProjectId(dataFile.getProject().getId());
        boolean animalFound = false;

        for (String newAnimalId  : newAnimalIdList) {
             for (Animal projectAnimal : projectAnimalList) {
                 if (newAnimalId.equals(projectAnimal.getProjectAnimalId()))
                     animalFound=true;
             }
             if (!animalFound) {
                 Animal animal = new Animal();
                 animal.setAnimalName("Unknown");
                 animal.setAnimalDescription("Unknown");
                 animal.setSpeciesName("Unknown");
                 animal.setVerifiedSpeciesName("Unknown");
                 animal.setProjectAnimalId(newAnimalId);
                 animal.setProject(dataFile.getProject());
                 // TODO:
                 // name = transmitter name
                 // transmitterID = transmitter SN where sensor1 is null
                 // sensorTransmitterID= transmitter SN where sensor1 is not null
                 // transmitter type code = dependent on how sensor works (C=temp; m=depth?)
                animalDao.save(animal);
             }
        }


    }

    public void checkReceiversExist(DataFile dataFile)  {

        // the Daos
        RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();
        ReceiverDeploymentDao receiverDeploymentDao = OzTrackApplication.getApplicationContext().getDaoManager().getReceiverDeploymentDao();

        List<String> newReceiverIdList = rawAcousticDetectionDao.getAllReceiverIds();
        List<ReceiverDeployment> projectReceiversList = receiverDeploymentDao.getReceiversByProjectId(dataFile.getProject().getId());
        boolean receiverFound=false;

        for (String newReceiverId : newReceiverIdList) {
            for (ReceiverDeployment receiverDeployment : projectReceiversList) {
                if (newReceiverId.equals(receiverDeployment.getOriginalId()))
                    receiverFound = true;
            }
            if (!receiverFound) {
                ReceiverDeployment receiverDeployment = new ReceiverDeployment();
                receiverDeployment.setOriginalId(newReceiverId);
                receiverDeployment.setReceiverName("Unknown");
                receiverDeployment.setReceiverDescription("Unknown");
                receiverDeployment.setProject(dataFile.getProject());
                //TODO:
                // get more stuff out of the file from here re location
                receiverDeploymentDao.save(receiverDeployment);
            }
        }

    }

    public void createDetections(DataFile dataFile) {

        RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        ReceiverDeploymentDao receiverDeploymentDao = OzTrackApplication.getApplicationContext().getDaoManager().getReceiverDeploymentDao();
        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();


        List <RawAcousticDetection> allRawAcousticDetections = rawAcousticDetectionDao.getAll();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        for (RawAcousticDetection rawAcousticDetection : allRawAcousticDetections) {

            AcousticDetection acousticDetection = new AcousticDetection();
            acousticDetection.setDetectionTime(rawAcousticDetection.getDatetime());
            acousticDetection.setAnimal(animalDao.getAnimal(rawAcousticDetection.getAnimalid(), dataFile.getProject().getId()));
            acousticDetection.setReceiverDeployment(receiverDeploymentDao.getReceiverDeployment(rawAcousticDetection.getReceiversn(), dataFile.getProject().getId()));
            acousticDetection.setDataFile(dataFile);
            acousticDetection.setSensor1Value(rawAcousticDetection.getSensor1());
            acousticDetection.setSensor1Units(rawAcousticDetection.getUnits1());
            acousticDetection.setSensor2Value(rawAcousticDetection.getSensor2());
            acousticDetection.setSensor2Units(rawAcousticDetection.getUnits2());

            entityManager.persist(acousticDetection);

        }

        transaction.commit();

    }

}

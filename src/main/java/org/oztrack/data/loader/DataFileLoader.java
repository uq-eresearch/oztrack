package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.model.*;
import org.oztrack.data.model.types.DataFileHeader;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.DataFileType;
import org.oztrack.data.model.types.ProjectType;
import org.oztrack.error.FileProcessingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.oztrack.util.OzTrackUtil.*;

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

    public void processNext() {

        // get the next datafile waiting to be processed if there's not one processing at the moment
        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        DataFile dataFile = dataFileDao.getNextDataFile();
        JdbcAccess jdbcAccess = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcAccess();


        if (dataFile != null) {


            dataFile.setStatus(DataFileStatus.PROCESSING);
            dataFileDao.update(dataFile);
            dataFileDao.refresh(dataFile);

            //if (dataFile.getDataFileType().equals(DataFileType.ACOUSTIC)) {
            if (dataFile.getProject().getProjectType().equals(ProjectType.ACOUSTIC)) {

                try {

                    // de-duplicate file
                    removeDuplicateLinesFromFile(dataFile.getOzTrackFileName());

                    // get the file into a raw table
                    processRawAcoustic(dataFile);

                    // get reference data in order: create animals, receivers
                    checkAnimalsExist(dataFile);
                    checkReceiversExist(dataFile);

                    // create the detections
                    int nbrDetectionsCreated = 0;
                    try {
                        // avoid hibernate for performance
                        nbrDetectionsCreated = jdbcAccess.loadAcousticDetections(dataFile.getProject().getId(), dataFile.getId());
                        jdbcAccess.truncateRawAcousticDetections();

                    } catch (Exception e) {
                        throw new FileProcessingException(e.toString());
                    }

                    dataFile.setStatus(DataFileStatus.COMPLETE);
                    dataFile.setNumberDetections(nbrDetectionsCreated);
                    dataFile.setStatusMessage( "File processing successfully completed on " + new Date().toString() + ". " +
                                               (dataFile.getLocalTimeConversionRequired()
                                                ? "Local time conversion is " + dataFile.getLocalTimeConversionHours()+ " hours." : "")
                                              );

                } catch (FileProcessingException e) {
                    dataFile.setStatus(DataFileStatus.FAILED);
                    dataFile.setStatusMessage(e.toString());

                    // clean up
                    File file = new File(dataFile.getOzTrackFileName());
                    File origFile = new File(dataFile.getOzTrackFileName().replace(".csv",".orig"));
                    file.delete();
                    origFile.delete();

                    jdbcAccess.truncateRawAcousticDetections();
                }

                dataFileDao.save(dataFile);
                dataFileDao.refresh(dataFile);

            } else if (dataFile.getDataFileType().equals(DataFileType.POSITION_FIX)) {
                processRawPositionFix(dataFile);
            }

        }

    }

    public void processRawAcoustic(DataFile dataFile) throws FileProcessingException {

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
        Calendar calendar = Calendar.getInstance();
        boolean localTimeConversionRequired = dataFile.getLocalTimeConversionRequired();
        long localTimeConversionHours = dataFile.getLocalTimeConversionHours();

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
                                    calendar.setTime(sdf.parse(dataRow[i]));
                                    if (localTimeConversionRequired)
                                        calendar.add(Calendar.HOUR,(int)localTimeConversionHours);
                                    rawAcousticDetection.setDatetime(calendar.getTime());
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
        logger.debug(lineNumber + " rows persisted.");

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
        boolean receiverFound;
        String originalId;

        for (String newReceiverId : newReceiverIdList) {
            receiverFound=false;
            for (ReceiverDeployment receiverDeployment : projectReceiversList) {
                originalId = receiverDeployment.getOriginalId();
                if (newReceiverId.equals(originalId))
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

    /*
    public void removeFileDuplicates(DataFile dataFile) throws FileProcessingException {

        File inFile = new File(dataFile.getOzTrackFileName());
        File outFile = new File(dataFile.getOzTrackFileName() + ".dedup");
        HashSet<String> hashSet = new HashSet<String>(10000);
        FileInputStream fileInputStream;
        String headers;
        String strLine;

        try {
             fileInputStream = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.");
        }

        DataInputStream in = new DataInputStream(fileInputStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException e) {
            throw new FileProcessingException("Couldn't create new file");
        }


        try {
             headers = br.readLine();
             bw.write(headers);
             bw.newLine();

             while ((strLine = br.readLine()) != null) {
                 if (!hashSet.contains(strLine)) {
                   hashSet.add(strLine);
                   bw.write(strLine);
                   bw.newLine();
                 }
             }
             hashSet.clear();
             br.close();
             bw.close();

        } catch (IOException e) {
             throw new FileProcessingException("Problem creating de-duplicates file.");
        }

        // write over originalFile


    }
    */

}
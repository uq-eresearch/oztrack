package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.RawAcousticDetection;
import org.oztrack.data.model.types.DataFileHeader;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.DataFileType;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 21/04/11
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataFileLoader
{
    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    public void processNext() {

        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();

        // gets the next datafile waiting to be processed if there's not one processing at the moment
        DataFile dataFile = dataFileDao.getNextDataFile();

        if (!(dataFile == null)) {

            dataFile.setStatus(DataFileStatus.PROCESSING);
            dataFileDao.update(dataFile);
            dataFileDao.refresh(dataFile);

            if (dataFile.getDataFileType().equals(DataFileType.ACOUSTIC)) {
                 processRawAcoustic(dataFile);
                 dataFileDao.update(dataFile);
                 dataFileDao.refresh(dataFile);
            } else if (dataFile.getDataFileType().equals(DataFileType.POSITION_FIX)) {
                 processRawPositionFix(dataFile);
            }


        }

    }

    public void processRawAcoustic(DataFile dataFile) {

        logger.info("processing raw acoustic file : " + dataFile.getOzTrackFileName());

        int lineNumber = 1;

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {

            FileInputStream fstream = new FileInputStream(dataFile.getOzTrackFileName());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            /* Populate a HashMap containing which columns contains which bits of data
             eg.  column 1 DATETIME
                  column 2 ANIMALID    */

            HashMap <Integer, DataFileHeader > headerMap = new HashMap<Integer, DataFileHeader>();
            String strLine = br.readLine();
            String [] colHeadings =  strLine.split(",");
            String [] dataRow;

            // find out which columns contain which data
            // fix this for unknown column headings

            for (int i=0; i < colHeadings.length; i++) {

                String heading = colHeadings[i].replaceAll("/","").replaceAll(" ","").toUpperCase();
                boolean headingFound = false;

                for (DataFileHeader dataFileHeader : DataFileHeader.values()) {
                    if (heading.equals(dataFileHeader.toString())) {
                        headerMap.put(i, dataFileHeader);
                        headingFound = true;
                    }
                }
                if (headingFound == false) {
                   logger.debug("Unknown Heading in file: " + heading);
                   throw new Exception("Unknown Heading in file: " + heading);
                }

            }

            logger.debug("File opened + header read.");

            while ((strLine = br.readLine()) != null) {

                lineNumber++;
                dataRow = strLine.split(",");
                RawAcousticDetection rawAcousticDetection = new RawAcousticDetection();

                // loop through the dataRow elements
                for (int i=0; i < dataRow.length; i++) {

                    // if data in this cell
                    if (!dataRow[i].equals(null) || !dataRow[i].isEmpty()) {

                        // retrieve the header for this column and use it to determine which field to update
                        DataFileHeader dataFileHeader = headerMap.get(i);

                        switch (dataFileHeader) {
                            case DATETIME: rawAcousticDetection.setDatetime(dataRow[i]); break;
                            case ID: rawAcousticDetection.setAnimalid(dataRow[i]);   break;
                            case SENSOR1: rawAcousticDetection.setSensor1(dataRow[i]); break;
                            case UNITS1: rawAcousticDetection.setUnits1(dataRow[i]); break;
                            case RECEIVERID: rawAcousticDetection.setReceiverid(dataRow[i]); break;
                            case CODESPACE: rawAcousticDetection.setCodespace(dataRow[i]); break;
                            case UNITS2: rawAcousticDetection.setUnits2(dataRow[i]); break;
                            case SENSOR2: rawAcousticDetection.setSensor2(dataRow[i]); break;
                            case TRANSMITTERNAME: rawAcousticDetection.setTransmittername(dataRow[i]); break;
                            case TRANSMITTERSN: rawAcousticDetection.setTransmittersn(dataRow[i]); break;
                            case RECEIVERNAME: rawAcousticDetection.setReceivername(dataRow[i]); break;
                            case RECEIVERSN: rawAcousticDetection.setReceiversn(dataRow[i]); break;
                            case STATIONNAME: rawAcousticDetection.setStationname(dataRow[i]); break;
                            case STATIONLATITUDE: rawAcousticDetection.setStationlatitude (dataRow[i]); break;
                            case STATIONLONGITUDE: rawAcousticDetection.setStationlongitude (dataRow[i]); break;
                            default: logger.debug("Problem in switch(dataFileHeader)"); break;
                        }
                    }
                }
                entityManager.persist(rawAcousticDetection);
                logger.debug(lineNumber + "rows persisted");
            }
            transaction.commit();

            // start processing the raw data
            RawAcousticDetectionDao rawAcousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getRawAcousticDetectionDao();
            dataFile.setNumberRawDetections(rawAcousticDetectionDao.getNumberRawDetections());

             //   List <String> animalIdList =  rawAcousticDetectionDao.getAllAnimalIds();
             //   checkAnimalsExist(animalIdList, dataFile.getProject());


             // check receivers


             dataFile.setStatus(DataFileStatus.COMPLETE);
             dataFile.setStatusMessage("File processing successfully completed.");

        }
        catch (FileNotFoundException e) {
            dataFile.setStatus(DataFileStatus.FAILED);
            dataFile.setStatusMessage("File not found.");
        }
        catch (IOException e) {
            dataFile.setStatus(DataFileStatus.FAILED);
            dataFile.setStatusMessage("Problem reading file.");
        }
        catch (Exception e) {
            dataFile.setStatus(DataFileStatus.FAILED);
            dataFile.setStatusMessage("Problem in file at line number: " + lineNumber + "Check all the file headings are correct. Some error occurred: " + e.toString());
        }

        //DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        //dataFileDao.save(dataFile);
        //dataFileDao.refresh(dataFile);

    }

    public void processRawPositionFix(DataFile dataFile) {
        logger.info("processing a raw position fix file : " + dataFile.getOzTrackFileName());

    }

    public void checkAnimalsExist(List<String> animalIdList, Project project) {

        /*
        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        for (String animalId  : animalIdList) {
        }
        */

    }



}

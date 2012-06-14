package org.oztrack.data.loader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.RawAcousticDetection;
import org.oztrack.data.model.ReceiverDeployment;
import org.oztrack.data.model.types.AcousticFileHeader;
import org.oztrack.error.FileProcessingException;

public class AcousticFileLoader extends DataFileLoader {
    private ReceiverDeploymentDao receiverDeploymentDao;
    
    public AcousticFileLoader(
        DataFile dataFile,
        DataFileDao dataFileDao,
        AnimalDao animalDao,
        EntityManager entityManager,
        JdbcAccess jdbcAccess,
        ReceiverDeploymentDao receiverDeploymentDao
    ) {
        super(dataFile, dataFileDao, animalDao, entityManager, jdbcAccess);
        this.receiverDeploymentDao = receiverDeploymentDao;
    }

    @Override
    public void process() throws FileProcessingException {
        super.process();
    }

    @Override
    public void createRawObservations() throws FileProcessingException {
        int lineNumber = 0;

        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(this.dataFile.getOzTrackFileName());
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.");
        }

         DataInputStream in = new DataInputStream(fileInputStream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));


        /* Populate a HashMap containing which columns contains which bits of data
         eg.  column 1 DATETIME
              column 2 ANIMALID    */

        HashMap<Integer, AcousticFileHeader> headerMap = new HashMap<Integer, AcousticFileHeader>();
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

            for (AcousticFileHeader acousticFileHeader : AcousticFileHeader.values()) {
                if (heading.equals(acousticFileHeader.toString())) {
                    headerMap.put(i, acousticFileHeader);
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
        boolean localTimeConversionRequired = this.dataFile.getLocalTimeConversionRequired();
        long localTimeConversionHours = this.dataFile.getLocalTimeConversionHours();

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
                        AcousticFileHeader acousticFileHeader = headerMap.get(i);

                        switch (acousticFileHeader) {
                            case DATETIME:
                                try {
                                    calendar.setTime(sdf.parse(dataRow[i]));
                                    if (localTimeConversionRequired)
                                        calendar.add(Calendar.HOUR,(int)localTimeConversionHours);
                                    rawAcousticDetection.setDatetime(calendar.getTime());
                                } catch (ParseException e) {
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
        }
        catch (IOException e) {
             throw new FileProcessingException("Problem reading file at line number :" + lineNumber);
        }
        logger.debug(lineNumber + " rows persisted.");
    }

    @Override
    public void checkData() throws FileProcessingException {
        checkReceivers();
    }

    private void checkReceivers() {
        List<String> newReceiverIdList = dataFileDao.getAllReceiverIds();
        List<ReceiverDeployment> projectReceiversList = receiverDeploymentDao.getReceiversByProjectId(dataFile.getProject().getId());
        boolean receiverFound;
        String originalId;

        for (String newReceiverId : newReceiverIdList) {
            receiverFound = false;
            for (ReceiverDeployment receiverDeployment : projectReceiversList) {
                originalId = receiverDeployment.getOriginalId();
                if (newReceiverId.equals(originalId)) {
                    receiverFound = true;
                }
            }
            if (!receiverFound) {
                ReceiverDeployment receiverDeployment = new ReceiverDeployment();
                receiverDeployment.setOriginalId(newReceiverId);
                receiverDeployment.setReceiverName("Unknown");
                receiverDeployment.setReceiverDescription("Unknown");
                receiverDeployment.setProject(this.dataFile.getProject());
                receiverDeployment.setCreateDate(new java.util.Date());
                //TODO:
                // get more stuff out of the file from here re location
                receiverDeploymentDao.save(receiverDeployment);
            }
        }
    }
}
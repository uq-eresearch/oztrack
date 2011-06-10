package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.RawPositionFix;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.error.FileProcessingException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static org.oztrack.util.OzTrackUtil.removeDuplicateLinesFromFile;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 10/06/11
 * Time: 10:07 AM
 */
public class PositionFixFileLoader {

     /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());
    private DataFile dataFile;

    PositionFixFileLoader(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public void process() throws FileProcessingException {

        removeDuplicateLinesFromFile(this.dataFile.getOzTrackFileName());
        createRawPositionFixes();

    }

    public void createRawPositionFixes() throws FileProcessingException {

        int lineNumber = 0;
        logger.info("processing raw position fix file : " + this.dataFile.getOzTrackFileName());

        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(this.dataFile.getOzTrackFileName());
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.");
        }

         DataInputStream in = new DataInputStream(fileInputStream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));


        /* Populate a HashMap containing which columns contains which bits of data
         eg.   1 DATE
               2 ANIMALID    */

        HashMap<Integer, PositionFixFileHeader> headerMap = new HashMap<Integer, PositionFixFileHeader>();
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

            for (PositionFixFileHeader positionFixFileHeader : PositionFixFileHeader.values()) {
                if (heading.equals(positionFixFileHeader.toString())) {
                    headerMap.put(i, positionFixFileHeader);
                    headingFound = true;
                }
            }
            if (!headingFound) {
                logger.debug("Unknown Heading in file: " + heading);
                throw new FileProcessingException("Unknown Heading in file: " + heading);
            }
        }

        logger.debug("File opened + header read.");

        boolean localTimeConversionRequired = this.dataFile.getLocalTimeConversionRequired();
        long localTimeConversionHours = this.dataFile.getLocalTimeConversionHours();

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
               while ((strLine = br.readLine()) != null) {

                lineNumber++;
                dataRow = strLine.split(",");
                RawPositionFix rawPositionFix= new RawPositionFix();

                // loop through the dataRow elements
                for (int i = 0; i < dataRow.length; i++) {

                    // if data in this cell
                    if (dataRow[i] != null || !dataRow[i].isEmpty()) {

                        // retrieve the header for this column and use it to determine which field to update
                        PositionFixFileHeader positionFixFileHeader = headerMap.get(i);

                        switch (positionFixFileHeader) {
                            case DATE:
                            case LOCDATE:
                                rawPositionFix.setDatetime(dateHandler(dataRow[i]));
                                break;
                            case TIME:
                                rawPositionFix.setDatetime(timeHandler(rawPositionFix.getDatetime(),dataRow[i]));
                                break;
                            case ID:
                            case ANIMALID:
                            case PLATFORMID:
                            case ARGOSID:
                                rawPositionFix.setAnimalId(dataRow[i]);
                                break;
                            case LAT:
                            case LATITUDE:
                                rawPositionFix.setLatitude(dataRow[i]);
                                break;
                            case LONG:
                            case LON:
                            case LONGITUDE:
                                 rawPositionFix.setLatitude(dataRow[i]);
                                 break;
                            case GDOP:
                            case HDOP:
                                if (!dataRow[i].isEmpty()) {
                                 try {
                                     rawPositionFix.setHDOP(Double.parseDouble(dataRow[i]));
                                 } catch (NumberFormatException e) {
                                     transaction.rollback();
                                     throw new FileProcessingException("HDOP value is not a number on line " + lineNumber);
                                 }
                                }
                            case SENSOR01:
                                if (!dataRow[i].isEmpty()) {
                                 try {
                                     rawPositionFix.setSensor1(Double.parseDouble(dataRow[i]));
                                 } catch (NumberFormatException e) {
                                     transaction.rollback();
                                     throw new FileProcessingException("Sensor1 value is not a number on line " + lineNumber);
                                 }
                                }
                            case SENSOR02:
                                if (!dataRow[i].isEmpty()) {
                                 try {
                                     rawPositionFix.setSensor1(Double.parseDouble(dataRow[i]));
                                 } catch (NumberFormatException e) {
                                     transaction.rollback();
                                     throw new FileProcessingException("Sensor2 value is not a number on line " + lineNumber);
                                 }
                                }
                                break;
                            default:
                                logger.debug("Problem in switch(dataFileHeader)");
                                break;
                        }
                    }
                }

                entityManager.persist(rawPositionFix);

            }

        } catch (IOException e) {
             transaction.rollback();
             throw new FileProcessingException("Problem reading file at line number :" + lineNumber);
        }
        // commit after reading whole file
        transaction.commit();
        logger.debug(lineNumber + " rows persisted.");

    }

    public Date dateHandler(String dateString) throws FileProcessingException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy H:m:s");
        Calendar calendar = Calendar.getInstance();

        //if (dateString.matches("(0[1-9]|[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012]|[1-9])/(19|20)\\\\d{2}") {
        if (dateString.matches("dd/dd/dddd")) {
            simpleDateFormat.applyPattern("dd/MM/yyyy");
         } else if (dateString.matches("dddd/dd/dd dd:dd:dd")) {
             simpleDateFormat.applyPattern("dd/MM/yyyy H:m:s");
        } else if (dateString.matches("dddd/dd/dd dd:dd:dd.ddd")) {
             simpleDateFormat.applyPattern("dd/MM/yyyy H:m:s.S");
        } else if (dateString.matches("dddd/dd/dd")) {
             simpleDateFormat.applyPattern("yyyy/MM/dd");
        } else if (dateString.matches("dd.dd.dddd")) {
             simpleDateFormat.applyPattern("dd.MM.yyyy");
        } else if (dateString.matches("dddd/dd/dd dd:dd:dd")) {
             simpleDateFormat.applyPattern("yyyy/MM/dd H:m:s");
        }

        try {
            calendar.setTime(simpleDateFormat.parse(dateString));
        } catch (ParseException e) {
            throw new FileProcessingException("Cannot read this date: " + dateString);
        }

        return calendar.getTime();

     }

    public Date timeHandler(Date date, String timeString) throws FileProcessingException {

        Calendar calendar = (Calendar) date.clone();
         String [] timeBits = timeString.split(":");

        if (!timeBits[0].isEmpty()) {
             calendar.set(Calendar.HOUR_OF_DAY, Integer.getInteger(timeBits[0]));
         }
         if (!timeBits[1].isEmpty()) {
             calendar.set(Calendar.MINUTE, Integer.getInteger(timeBits[1]));
         }
         if (!timeBits[2].isEmpty()) {
             String [] seconds = timeBits[2].split(".");
             if (!seconds[0].isEmpty()) {
                 calendar.set(Calendar.SECOND, Integer.getInteger(seconds[0]));
             }
             if (!seconds[1].isEmpty()) {
                 calendar.set(Calendar.MILLISECOND, Integer.getInteger(seconds[1]));
             }
         }
             calendar.set(Calendar.SECOND, Integer.getInteger(timeBits[2]));
         calendar.set(Calendar.SECOND, Integer.getInteger(timeBits[2]));

        return calendar.getTime();

    }


}

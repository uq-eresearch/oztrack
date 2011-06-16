package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.RawPositionFix;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.error.FileProcessingException;
import org.oztrack.util.OzTrackUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }


    PositionFixFileLoader(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public void process() throws FileProcessingException {

        removeDuplicateLinesFromFile(this.dataFile.getOzTrackFileName());
        createRawPositionFixes();

    }

    public void createRawPositionFixes() throws FileProcessingException {

        int lineNumber = 0;
        logger.info("processing raw position fix file : " + dataFile.getOzTrackFileName());

        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(dataFile.getOzTrackFileName());
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.");
        }

         DataInputStream in = new DataInputStream(fileInputStream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String strLine;

        try {
            strLine = br.readLine();
            lineNumber++;
        } catch (IOException e) {
            throw new FileProcessingException("Problem reading file.");
        }

        /* Populate a HashMap containing which columns contains which bits of data
         eg.   1 DATE
               2 ANIMALID    */

        HashMap<Integer, PositionFixFileHeader> headerMap = new HashMap<Integer, PositionFixFileHeader>();
        String[] colHeadings = strLine.split(",");
        String[] dataRow;

        boolean dateFieldFound = false;
        boolean latFieldFound = false;
        boolean longFieldFound = false;

        // determine which columns contain which data
        for (int i = 0; i < colHeadings.length; i++) {

            String heading = colHeadings[i].replaceAll("/", "").replaceAll(" ", "").toUpperCase();

            for (PositionFixFileHeader positionFixFileHeader : PositionFixFileHeader.values()) {

                if (heading.equals(positionFixFileHeader.toString())) {

                    headerMap.put(i,positionFixFileHeader);

                    if (heading.contains("DATE") && !dateFieldFound) {
                        dateFieldFound=true;
                    }

                    if (heading.contains("LAT") && !latFieldFound) {
                        latFieldFound=true;
                    }

                    if (heading.contains("LON") && !longFieldFound) {
                        longFieldFound=true;
                    }
                }
            }
        }

        if (!dateFieldFound) {
             throw new FileProcessingException("No DATE field found in file.");
        }
        if (!latFieldFound) {
             throw new FileProcessingException("No LATITUDE field found in file.");
        }
        if (!longFieldFound) {
             throw new FileProcessingException("No LONGITUDE field found in file.");
        }

        logger.debug("File opened + header read.");

        boolean localTimeConversionRequired = dataFile.getLocalTimeConversionRequired();
        long localTimeConversionHours = dataFile.getLocalTimeConversionHours();

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
               while ((strLine = br.readLine()) != null) {

                lineNumber++;
                dataRow = strLine.split(",");
                RawPositionFix rawPositionFix= new RawPositionFix();
                logger.debug("file linenumber: " + lineNumber);

                // loop through the dataRow elements
                for (int i = 0; i < dataRow.length; i++) {

                    // if data in this cell
                    if (dataRow[i] != null || !dataRow[i].isEmpty()) {

                        // retrieve the header for this column and use it to determine which field to update


                        if (headerMap.get(i) != null ) {

                            PositionFixFileHeader positionFixFileHeader = headerMap.get(i);
                            switch (positionFixFileHeader) {
                                case DATE:
                                case LOCDATE:
                                case UTCDATE:
                                    try {
                                        rawPositionFix.setDatetime(dateHandler(dataRow[i]));
                                    } catch (FileProcessingException e) {
                                        transaction.rollback();
                                        throw new FileProcessingException("Unable to read date format on line " + lineNumber + ": " + dataRow[i] );
                                    }
                                    break;
                                case TIME:
                                case UTCTIME:
                                    try {
                                        rawPositionFix.setDatetime(timeHandler(rawPositionFix.getDatetime(),dataRow[i]));
                                    } catch (FileProcessingException e) {
                                        transaction.rollback();
                                        throw new FileProcessingException("Unable to read time format on line " + lineNumber + ": " + dataRow[i] );
                                    }
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
                                     rawPositionFix.setLongitude(dataRow[i]);
                                     break;
                                case GDOP:
                                case HDOP:
                                     try {
                                         rawPositionFix.setHDOP(Double.parseDouble(dataRow[i]));
                                     } catch (NumberFormatException e) {
                                         transaction.rollback();
                                         throw new FileProcessingException("HDOP value is not a number on line " + lineNumber);
                                     }
                                    break;
                                case SENSOR01:
                                     try {
                                         rawPositionFix.setSensor1(Double.parseDouble(dataRow[i]));
                                     } catch (NumberFormatException e) {
                                         transaction.rollback();
                                         throw new FileProcessingException("Sensor1 value is not a number on line " + lineNumber);
                                     }
                                case SENSOR02:
                                     try {
                                         rawPositionFix.setSensor1(Double.parseDouble(dataRow[i]));
                                     } catch (NumberFormatException e) {
                                         transaction.rollback();
                                         throw new FileProcessingException("Sensor2 value is not a number on line " + lineNumber);
                                     }
                                    break;
                                default:
                                    logger.debug("Problem in switch(dataFileHeader)");
                                    break;
                            }
                        }
                    }
                }

                entityManager.persist(rawPositionFix);

            }

        } catch (Exception e) {
             transaction.rollback();
             throw new FileProcessingException("Problem reading file at line number :" + lineNumber);
        }
        // commit after reading whole file
        transaction.commit();
        logger.debug(lineNumber + " rows persisted.");

    }

    public  Date dateHandler(String dateString) throws FileProcessingException {

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
        logger.debug("dateHandler dateString = " + dateString);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy H:m:s");
        Calendar calendar = Calendar.getInstance();

        String dateDots = "(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012]|[1-9])\\.(19[0-9][0-9]|20[0-9][0-9])";
        String dateDotsPattern = "dd.MM.yyyy";

        String dateSlashes = "(0[1-9]|[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012]|[1-9])/(19[0-9][0-9]|20[0-9][0-9])";
        String dateSlashesPattern = "dd/MM/yyyy";

        String time24 =   ".(0[1-9]|[1-9]|[1][0-9]|2[0-3]).([1-9]|[0-5][1-9]).([1-9]|[0-5][1-9])" ;
        String time24Pattern = " H:m:s";

        String time24Ms = ".(0[1-9]|[1-9]|[1][0-9]|2[0-3]).([1-9]|[0-5][1-9]).([1-9]|[0-5][1-9]).([0-9])+" ;
        String time24MsPattern = " H:m:s.S";

        if (dateString.matches(dateDots)) {
            simpleDateFormat.applyPattern(dateDotsPattern);
        } else if (dateString.matches(dateDots + time24)) {
            simpleDateFormat.applyPattern(dateDotsPattern + time24Pattern);
        } else if (dateString.matches(dateDots + time24Ms)) {
                    simpleDateFormat.applyPattern(dateDotsPattern + time24MsPattern);
        } else if (dateString.matches(dateSlashes)) {
            simpleDateFormat.applyPattern(dateSlashesPattern);
        } else if (dateString.matches(dateSlashes + time24)) {
                    simpleDateFormat.applyPattern(dateSlashesPattern + time24Pattern);
        } else if (dateString.matches(dateSlashes + time24Ms)) {
                    simpleDateFormat.applyPattern(dateSlashesPattern + time24MsPattern);
        }

        try {
            calendar.setTime(simpleDateFormat.parse(dateString));

        } catch (ParseException e) {

            throw new FileProcessingException(e.toString());
        }

        return calendar.getTime();

     }

    public Date timeHandler(Date date, String timeString) throws FileProcessingException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String [] timeBits = timeString.split(":");

        try {
         if (!timeBits[0].isEmpty()) {
             int hours = Integer.parseInt(timeBits[0]);
             calendar.set(Calendar.HOUR_OF_DAY, hours);
          }
          if (!timeBits[1].isEmpty()) {
              calendar.set(Calendar.MINUTE, Integer.parseInt(timeBits[1]));
          }
          if (!timeBits[2].isEmpty()) {
              if (timeBits[2].contains(".")) {
                String [] seconds = timeBits[2].split("\\.");
                if (!seconds[0].isEmpty()) {
                  calendar.set(Calendar.SECOND, Integer.parseInt(seconds[0]));
                }
                if (!seconds[1].isEmpty()) {
                  calendar.set(Calendar.MILLISECOND, Integer.parseInt(seconds[1]));
                }
              } else {
                  calendar.set(Calendar.SECOND, Integer.parseInt(timeBits[2]));
              }
          }
        } catch (Exception e) {
            throw new FileProcessingException("Error in time format.");
        }

        return calendar.getTime();

    }



}

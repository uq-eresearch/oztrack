package org.oztrack.data.loader;

import com.vividsolutions.jts.geom.*;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.PositionFixDao;
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

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 10/06/11
 * Time: 10:07 AM
 */
public class PositionFixFileLoader extends DataFileLoader {

    PositionFixFileLoader(DataFile dataFile) {
        super(dataFile);
    }

    @Override
    public void process() throws FileProcessingException {
        super.process();
    }

    @Override
    public void insertRawObservations() throws FileProcessingException {

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
        boolean animalIdFieldFound = false;

        // determine which columns contain which data
        for (int i = 0; i < colHeadings.length; i++) {

            String heading = colHeadings[i].replaceAll("/", "").replaceAll(" ", "").toUpperCase();

            for (PositionFixFileHeader positionFixFileHeader : PositionFixFileHeader.values()) {

                if (heading.equals(positionFixFileHeader.toString())) {

                    headerMap.put(i,positionFixFileHeader);

//                    if (heading.contains("DATE") && !dateFieldFound) {
                    if (  (heading.equals("ACQUISITIONTIME") 
                    	|| heading.equals("DATE")	
                    	|| heading.equals("LOCDATE")	
                    	|| heading.equals("UTCDATE")	
                       )&& !dateFieldFound) {
                    	dateFieldFound=true;
                    }

                    if (heading.contains("LAT") && !latFieldFound) {
                        latFieldFound=true;
                    }

                    if (heading.contains("LON") && !longFieldFound) {
                        longFieldFound=true;
                    }
                    
                    if (heading.equals("ANIMALID") && !animalIdFieldFound) {
                    	animalIdFieldFound=true;
                    }

                    if (heading.equals("ID") && !animalIdFieldFound) {
                    	animalIdFieldFound=true;
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
        if (!animalIdFieldFound) {
        	// if there's not an animalId field, assume that this file contains a single animal.
        	dataFile.setSingleAnimalInFile(true);
        	dataFileDao.update(dataFile);
        }

        logger.debug("File opened + header read.");

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy H:m:s");

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
                            	case ACQUISITIONTIME:
                            	case DATE:
                                case LOCDATE:
                                case UTCDATE:
                                       try {
                                           if (lineNumber == 2) {
                                        	   simpleDateFormat = findDateFormat(dataRow[i]);
                                           }
                                           Calendar calendar = Calendar.getInstance();
                                           try {
                                        	   calendar.setTime(simpleDateFormat.parse(dataRow[i]));
                                           } catch (ParseException e) {
                                        	   throw new FileProcessingException("Date Parsing error. ");
                                           }
                                           rawPositionFix.setDetectionTime(calendar.getTime());
                                       } catch (FileProcessingException e) {
                                            transaction.rollback();
                                            throw new FileProcessingException(e.toString() + "Using date: "+ dataRow[i] + " from line : "  + lineNumber);
                                       }
                                    break;
                                case TIME:
                                case UTCTIME:
                                    try {
                                        rawPositionFix.setDetectionTime(timeHandler(rawPositionFix.getDetectionTime(),dataRow[i]));
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
                                         rawPositionFix.setSensor1value(Double.parseDouble(dataRow[i]));
                                     } catch (NumberFormatException e) {
                                         transaction.rollback();
                                         throw new FileProcessingException("Sensor1 value is not a number on line " + lineNumber);
                                     }
                                case SENSOR02:
                                     try {
                                         rawPositionFix.setSensor1value(Double.parseDouble(dataRow[i]));
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

                
                if ((rawPositionFix.getLatitude() != null) && (rawPositionFix.getLongitude() != null)) {
	                try {
		                // have lat/long now: create them
		                Point locationGeometry = findLocationGeometry(rawPositionFix.getLatitude(),rawPositionFix.getLongitude());
		                rawPositionFix.setLocationGeometry(locationGeometry);
	                } catch (Exception e) {
	                	transaction.rollback();
	                	throw new FileProcessingException("Problems at line number: " + lineNumber + " parsing these as Latitude/Longitude in WGS84: " + rawPositionFix.getLatitude() + "," + rawPositionFix.getLongitude());
	                }
                }

                if ((rawPositionFix.getDetectionTime() != null) && (rawPositionFix.getLocationGeometry() != null)) {
                    entityManager.persist(rawPositionFix);
                }

            }

        } catch (IOException e) {
             transaction.rollback();
             throw new FileProcessingException("Problem reading file at line number :" + lineNumber);
        }
        // commit after reading whole file
        transaction.commit();
        logger.debug(lineNumber + " rows persisted.");

    }

    // handles fields that are either just a date OR a date and time
    public SimpleDateFormat findDateFormat(String dateString) throws FileProcessingException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
        //Calendar calendar = Calendar.getInstance();

        String DateDotsRegex = "(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012]|[1-9])\\.(19[0-9][0-9]|20[0-9][0-9])";
        String dateDotsPattern = "dd.MM.yyyy";

        String dateSlashesRegex = "(0[1-9]|[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012]|[1-9])/(19[0-9][0-9]|20[0-9][0-9])";
        String dateSlashesPattern = "dd/MM/yyyy";

        String backwardsDateDotsRegex = "(19[0-9][0-9]|20[0-9][0-9])\\.(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|[1-9]|[12][0-9]|3[01])";
        String backwardsDateDotsPattern = "yyyy.MM.dd";
        
        String time24Regex =   ".(0[1-9]|[1-9]|[1][0-9]|2[0-3]).([1-9]|[0-5][0-9]).([1-9]|[0-5][0-9])" ;
        String time24Pattern = " H:m:s";

        String time24MsRegex = ".(0[1-9]|[1-9]|[1][0-9]|2[0-3]).([1-9]|[0-5][0-9]).([1-9]|[0-5][0-9]).([0-9])+" ;
        String time24MsPattern = " H:m:s.S";

        if (dateString.matches(DateDotsRegex)) {
            	simpleDateFormat.applyPattern(dateDotsPattern);
        } else if (dateString.matches(DateDotsRegex + time24Regex)) {
            	simpleDateFormat.applyPattern(dateDotsPattern + time24Pattern);
        } else if (dateString.matches(DateDotsRegex + time24MsRegex)) {
                simpleDateFormat.applyPattern(dateDotsPattern + time24MsPattern);
        
        // date with slashes        
        } else if (dateString.matches(dateSlashesRegex)) {
            	simpleDateFormat.applyPattern(dateSlashesPattern);
        } else if (dateString.matches(dateSlashesRegex + time24Regex)) {
                simpleDateFormat.applyPattern(dateSlashesPattern + time24Pattern);
        } else if (dateString.matches(dateSlashesRegex + time24MsRegex)) {
                simpleDateFormat.applyPattern(dateSlashesPattern + time24MsPattern);
        
	    // year first with dots
        } else if (dateString.matches(backwardsDateDotsRegex)) {
	    	simpleDateFormat.applyPattern(backwardsDateDotsPattern);
	    } else if (dateString.matches(backwardsDateDotsRegex + time24Regex)) {
	        simpleDateFormat.applyPattern(backwardsDateDotsPattern + time24Pattern);
	    } else if (dateString.matches(backwardsDateDotsRegex + time24MsRegex)) {
	        simpleDateFormat.applyPattern(backwardsDateDotsPattern + time24MsPattern);
	    } else {
	    	throw new FileProcessingException("Could not handle this date format: " + dateString + ". Please see the help screen.");
	    }
        
        return simpleDateFormat;

     }

    public Date timeHandler(Date date, String timeString) throws FileProcessingException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

//        boolean localTimeConversionRequired = dataFile.getLocalTimeConversionRequired();
//        long localTimeConversionHours = dataFile.getLocalTimeConversionHours();
        
        if (dataFile.getLocalTimeConversionRequired()) {
        	calendar.add(Calendar.HOUR, dataFile.getLocalTimeConversionHours().intValue());
        }
        
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

    public Point findLocationGeometry(String latitude, String longitude) throws FileProcessingException {

        Double dLatitude = Double.parseDouble(latitude);
        Double dLongitude = Double.parseDouble(longitude);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(1000000),4326);
        Coordinate coordinate = new Coordinate(dLongitude, dLatitude);

        return geometryFactory.createPoint(coordinate);

    }
    
    public void updateDataFileMetadata() throws FileProcessingException {
    	
    	// update datafile dates 
        PositionFixDao p = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao();
        dataFile.setFirstDetectionDate(p.getDataFileFirstDetectionDate(dataFile));
        dataFile.setLastDetectionDate(p.getDataFileLastDetectionDate(dataFile));
        dataFileDao.update(dataFile);
    }

}

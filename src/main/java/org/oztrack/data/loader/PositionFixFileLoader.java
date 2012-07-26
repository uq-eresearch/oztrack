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
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.RawPositionFix;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.error.FileProcessingException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class PositionFixFileLoader extends DataFileLoader {
    private PositionFixDao positionFixDao;

    private Pattern degPattern = Pattern.compile("^([^\\s]+)$");
    private Pattern degMinPattern = Pattern.compile("^([^\\s]+)\\s+([^\\s]+)$");
    private Pattern degMinSecPattern = Pattern.compile("^([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)$");

    public PositionFixFileLoader(
        DataFile dataFile,
        DataFileDao dataFileDao,
        AnimalDao animalDao,
        EntityManager entityManager,
        JdbcAccess jdbcAccess,
        PositionFixDao positionFixDao
    ) {
        super(dataFile, dataFileDao, animalDao, entityManager, jdbcAccess);
        this.positionFixDao = positionFixDao;
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
            fileInputStream = new FileInputStream(dataFile.getAbsoluteDataFilePath());
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.", e);
        }

         DataInputStream in = new DataInputStream(fileInputStream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String strLine;

        try {
            strLine = br.readLine();
            lineNumber++;
        } catch (IOException e) {
            throw new FileProcessingException("Problem reading file.", e);
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
                    switch (positionFixFileHeader) {
                        case ACQUISITIONTIME:
                        case UTCDATE:
                        case LOCDATE:
                        case DATE:
                            dateFieldFound = true;
                            break;
                        case UTCTIME:
                        case TIME:
                            break;
                        case LATITUDE:
                        case LATT:
                        case LAT:
                            latFieldFound = true;
                            break;
                        case LONGITUDE:
                        case LONG:
                        case LON:
                            longFieldFound = true;
                            break;
                        case ID:
                        case ANIMALID:
                            animalIdFieldFound = true;
                            break;
                        default:
                            logger.debug("Unhandled positionFixFileHeader: " + positionFixFileHeader);
                            break;
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
                            	case UTCDATE:
                            	case LOCDATE:
                            	case DATE:
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
                                            throw new FileProcessingException(e.toString() + "Using date: "+ dataRow[i] + " from line : "  + lineNumber);
                                       }
                                    break;
                            	case UTCTIME:
                                case TIME:
                                    try {
                                        rawPositionFix.setDetectionTime(timeHandler(rawPositionFix.getDetectionTime(),dataRow[i]));
                                    } catch (FileProcessingException e) {
                                        throw new FileProcessingException("Unable to read time format on line " + lineNumber + ": " + dataRow[i], e);
                                    }
                                    break;
                                case LATITUDE:
                                case LATT:
                                case LAT:
                                    rawPositionFix.setLatitude(dataRow[i]);
                                    break;
                                case LONGITUDE:
                                case LONG:
                                case LON:
                                     rawPositionFix.setLongitude(dataRow[i]);
                                     break;
                                case ID:
                                case ANIMALID:
                                    rawPositionFix.setAnimalId(dataRow[i]);
                                    break;
                                default:
                                    logger.debug("Unhandled positionFixFileHeader: " + positionFixFileHeader);
                                    break;
                            }
                        }
                    }
                }

                
                if ((rawPositionFix.getLatitude() != null) && (rawPositionFix.getLongitude() != null)) {
	                try {
		                Point locationGeometry = findLocationGeometry(rawPositionFix.getLatitude(),rawPositionFix.getLongitude());
		                rawPositionFix.setLocationGeometry(locationGeometry);
	                }
	                catch (Exception e) {
	                	throw new FileProcessingException("Problems at line number: " + lineNumber + " parsing these as Latitude/Longitude in WGS84: " + rawPositionFix.getLatitude() + "," + rawPositionFix.getLongitude());
	                }
                }

                if ((rawPositionFix.getDetectionTime() != null) && (rawPositionFix.getLocationGeometry() != null)) {
                    entityManager.persist(rawPositionFix);
                }
            }
        }
        catch (IOException e) {
             throw new FileProcessingException("Problem reading file at line number :" + lineNumber);
        }
        logger.debug(lineNumber + " rows persisted.");
    }
    
    @Override
    public void updateDataFileMetadata() throws FileProcessingException {
        dataFile.setFirstDetectionDate(positionFixDao.getDataFileFirstDetectionDate(dataFile));
        dataFile.setLastDetectionDate(positionFixDao.getDataFileLastDetectionDate(dataFile));
        dataFileDao.update(dataFile);
    }

    // handles fields that are either just a date OR a date and time
    private SimpleDateFormat findDateFormat(String dateString) throws FileProcessingException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
        //Calendar calendar = Calendar.getInstance();

        String DateDotsRegex = "(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012]|[1-9])\\.(19[0-9][0-9]|20[0-9][0-9])";
        String dateDotsPattern = "dd.MM.yyyy";

        String dateSlashesRegex = "(0[1-9]|[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012]|[1-9])/(19[0-9][0-9]|20[0-9][0-9])";
        String dateSlashesPattern = "dd/MM/yyyy";

        String backwardsDateDotsRegex = "(19[0-9][0-9]|20[0-9][0-9])\\.(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|[1-9]|[12][0-9]|3[01])";
        String backwardsDateDotsPattern = "yyyy.MM.dd";
        
        String timeRegexHM =   ".(0[0-9]|[0-9]|[1][0-9]|2[0-3]).([0-9]|[0-5][0-9])" ;
        String timePatternHM = " H:m";

        String timeRegexHMS =   ".(0[0-9]|[0-9]|[1][0-9]|2[0-3]).([0-9]|[0-5][0-9]).([0-9]|[0-5][0-9])" ;
        String timePatternHMS = " H:m:s";

        String timeRegexHMSMs = ".(0[0-9]|[0-9]|[1][0-9]|2[0-3]).([0-9]|[0-5][0-9]).([0-9]|[0-5][0-9]).([0-9])+" ;
        String timePatternHMSMs = " H:m:s.S";

        if (dateString.matches(DateDotsRegex)) {
            	simpleDateFormat.applyPattern(dateDotsPattern);
        } else if (dateString.matches(DateDotsRegex + timeRegexHM)) {
        		simpleDateFormat.applyPattern(dateDotsPattern + timePatternHM);
        } else if (dateString.matches(DateDotsRegex + timeRegexHMS)) {
            	simpleDateFormat.applyPattern(dateDotsPattern + timePatternHMS);
        } else if (dateString.matches(DateDotsRegex + timeRegexHMSMs)) {
                simpleDateFormat.applyPattern(dateDotsPattern + timePatternHMSMs);
        
        // date with slashes        
        } else if (dateString.matches(dateSlashesRegex)) {
            	simpleDateFormat.applyPattern(dateSlashesPattern);
        } else if (dateString.matches(dateSlashesRegex + timeRegexHM)) {
            	simpleDateFormat.applyPattern(dateSlashesPattern + timePatternHM);
        } else if (dateString.matches(dateSlashesRegex + timeRegexHMS)) {
                simpleDateFormat.applyPattern(dateSlashesPattern + timePatternHMS);
        } else if (dateString.matches(dateSlashesRegex + timeRegexHMSMs)) {
                simpleDateFormat.applyPattern(dateSlashesPattern + timePatternHMSMs);
        
	    // year first with dots
        } else if (dateString.matches(backwardsDateDotsRegex)) {
	    	simpleDateFormat.applyPattern(backwardsDateDotsPattern);
	    } else if (dateString.matches(backwardsDateDotsRegex + timeRegexHM)) {
	        simpleDateFormat.applyPattern(backwardsDateDotsPattern + timePatternHM);
	    } else if (dateString.matches(backwardsDateDotsRegex + timeRegexHMS)) {
	        simpleDateFormat.applyPattern(backwardsDateDotsPattern + timePatternHMS);
	    } else if (dateString.matches(backwardsDateDotsRegex + timeRegexHMSMs)) {
	        simpleDateFormat.applyPattern(backwardsDateDotsPattern + timePatternHMSMs);
	    } else {
	    	throw new FileProcessingException("Could not handle this date format: " + dateString + ". Please see the help screen.");
	    }
        
        return simpleDateFormat;

     }

    private Date timeHandler(Date date, String timeString) throws FileProcessingException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

//        boolean localTimeConversionRequired = dataFile.getLocalTimeConversionRequired();
//        long localTimeConversionHours = dataFile.getLocalTimeConversionHours();
        
        if (dataFile.getLocalTimeConversionRequired()) {
        	calendar.add(Calendar.HOUR, dataFile.getLocalTimeConversionHours().intValue());
        }
        
        String [] timeTokens = timeString.split(":");

        try {
         if ((timeTokens.length >= 1) && !timeTokens[0].trim().isEmpty()) {
             int hours = Integer.parseInt(timeTokens[0].trim());
             calendar.set(Calendar.HOUR_OF_DAY, hours);
          }
          if ((timeTokens.length >= 2) && !timeTokens[1].trim().isEmpty()) {
              calendar.set(Calendar.MINUTE, Integer.parseInt(timeTokens[1].trim()));
          }
          if ((timeTokens.length >= 3) && !timeTokens[2].trim().isEmpty()) {
              if (timeTokens[2].contains(".")) {
                String [] secondsTokens = timeTokens[2].split("\\.");
                if ((secondsTokens.length >= 1) && !secondsTokens[0].trim().isEmpty()) {
                  calendar.set(Calendar.SECOND, Integer.parseInt(secondsTokens[0].trim()));
                }
                if ((secondsTokens.length >= 2) && !secondsTokens[1].trim().isEmpty()) {
                  calendar.set(Calendar.MILLISECOND, Integer.parseInt(secondsTokens[1].trim()));
                }
              }
              else {
                  calendar.set(Calendar.SECOND, Integer.parseInt(timeTokens[2].trim()));
              }
          }
        } catch (Exception e) {
            throw new FileProcessingException("Error in time format.", e);
        }

        return calendar.getTime();

    }

    private Point findLocationGeometry(String latitude, String longitude) throws FileProcessingException {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(1000000), 4326);
        Coordinate coordinate = new Coordinate(parseCoordinate(longitude), parseCoordinate(latitude));
        return geometryFactory.createPoint(coordinate);
    }
    
    private Double parseCoordinate(String s) throws FileProcessingException {
        Matcher matcher = null;
        if ((matcher = degPattern.matcher(s)).find()) {
            return Double.parseDouble(matcher.group(1));
        }
        if ((matcher = degMinPattern.matcher(s)).find()) {
            double deg = Double.parseDouble(matcher.group(1));
            double signFactor = deg / Math.abs(deg);
            return
                deg +
                signFactor * (Double.parseDouble(matcher.group(2)) / 60d);
        }
        if ((matcher = degMinSecPattern.matcher(s)).find()) {
            double deg = Double.parseDouble(matcher.group(1));
            double signFactor = deg / Math.abs(deg);
            return
                deg +
                signFactor * (Double.parseDouble(matcher.group(2)) / 60d) +
                signFactor * (Double.parseDouble(matcher.group(3)) / 3600d);
        }
        throw new FileProcessingException("Could not parse coordinate: " + s);
    }
}
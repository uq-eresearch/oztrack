package org.oztrack.data.loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.RawPositionFix;
import org.oztrack.data.model.types.PositionFixFileHeader;
import org.oztrack.error.FileProcessingException;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class PositionFixFileLoader extends DataFileLoader {
    private Pattern degPattern = Pattern.compile("^([^\\s]+)$");
    private Pattern degMinPattern = Pattern.compile("^([^\\s]+)\\s+([^\\s]+)$");
    private Pattern degMinSecPattern = Pattern.compile("^([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)$");

    public PositionFixFileLoader(
        DataFile dataFile,
        DataFileDao dataFileDao,
        AnimalDao animalDao,
        PositionFixDao positionFixDao,
        EntityManager entityManager,
        JdbcAccess jdbcAccess
    ) {
        super(dataFile, dataFileDao, animalDao, positionFixDao, entityManager, jdbcAccess);
    }

    @Override
    public void createRawObservations() throws FileProcessingException {
        FileInputStream fileInputStream = null;
        CSVReader csvReader = null;
        try {
            try {
                fileInputStream = new FileInputStream(dataFile.getAbsoluteDataFilePath());
            }
            catch (FileNotFoundException e) {
                throw new FileProcessingException("File not found.", e);
            }

            try {
                csvReader = new CSVReader(new InputStreamReader(fileInputStream, "UTF-8"));
            }
            catch (IOException e) {
                throw new FileProcessingException("Error reading CSV file.", e);
            }

            String[] colHeadings = null;
            int lineNumber = 0;

            try {
                colHeadings = csvReader.readNext();
                lineNumber++;
            } catch (IOException e) {
                throw new FileProcessingException("Problem reading file.", e);
            }

            /* Populate a HashMap containing which columns contains which bits of data
             eg.   1 DATE
                   2 ANIMALID    */

            HashMap<Integer, PositionFixFileHeader> headerMap = new HashMap<Integer, PositionFixFileHeader>();

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
                            case DELETED:
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

            SimpleDateFormat dateFormat = null;
            Boolean dateIncludesTime = null;

            try {
                String[] dataRow = null;
                while ((dataRow = csvReader.readNext()) != null) {
                    lineNumber++;
                    RawPositionFix rawPositionFix= new RawPositionFix();
                    rawPositionFix.setDeleted(false);

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
                                                FindDateFormatResult findDateFormatResult = findDateFormat(dataRow[i]);
                                                dateFormat = findDateFormatResult.dateFormat;
                                                dateIncludesTime = findDateFormatResult.includesTime;
                                            }
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                calendar.setTime(dateFormat.parse(dataRow[i]));
                                                if (dateIncludesTime && dataFile.getLocalTimeConversionRequired()) {
                                                    calendar.add(Calendar.HOUR, dataFile.getLocalTimeConversionHours().intValue());
                                                }
                                            }
                                            catch (ParseException e) {
                                                throw new FileProcessingException("Date Parsing error. ");
                                            }
                                            rawPositionFix.setDetectionTime(calendar.getTime());
                                        }
                                        catch (FileProcessingException e) {
                                            throw new FileProcessingException(e.toString() + "Using date: "+ dataRow[i] + " from line : "  + lineNumber);
                                        }
                                        break;
                                    case UTCTIME:
                                    case TIME:
                                        try {
                                            rawPositionFix.setDetectionTime(timeHandler(rawPositionFix.getDetectionTime(),dataRow[i]));
                                        }
                                        catch (FileProcessingException e) {
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
                                    case DELETED:
                                        rawPositionFix.setDeleted(Boolean.valueOf(dataRow[i].toLowerCase(Locale.ENGLISH)));
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
        finally {
            try {csvReader.close();} catch (Exception e) {};
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private static class FindDateFormatResult {
        public SimpleDateFormat dateFormat;
        public Boolean includesTime;
    }

    // handles fields that are either just a date OR a date and time
    private FindDateFormatResult findDateFormat(String dateString) throws FileProcessingException {
        String[] dateRegexes = new String[] {
            "(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012]|[1-9])\\.(19[0-9][0-9]|20[0-9][0-9])",
            "(0[1-9]|[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012]|[1-9])/(19[0-9][0-9]|20[0-9][0-9])",
            "(19[0-9][0-9]|20[0-9][0-9])\\.(0[1-9]|[1-9]|[12][0-9]|3[01])\\.(0[1-9]|[1-9]|[12][0-9]|3[01])",
            "(19[0-9][0-9]|20[0-9][0-9])\\-(0[1-9]|[1-9]|[12][0-9]|3[01])\\-(0[1-9]|[1-9]|[12][0-9]|3[01])"
        };
        String[] datePatterns = new String[] {
            "dd.MM.yyyy",
            "dd/MM/yyyy",
            "yyyy.MM.dd",
            "yyyy-MM-dd"
        };
        String[] timeRegexes = new String[] {
            ".(0[0-9]|[0-9]|[1][0-9]|2[0-3]).([0-9]|[0-5][0-9])",
            ".(0[0-9]|[0-9]|[1][0-9]|2[0-3]).([0-9]|[0-5][0-9]).([0-9]|[0-5][0-9])",
            ".(0[0-9]|[0-9]|[1][0-9]|2[0-3]).([0-9]|[0-5][0-9]).([0-9]|[0-5][0-9]).([0-9])+"
        };
        String[] timePatterns = new String[] {
            " H:m",
            " H:m:s",
            " H:m:s.S"
        };
        for (int dateIndex = 0; dateIndex < dateRegexes.length; dateIndex++) {
            // Try matching just a date value
            if (dateString.matches(dateRegexes[dateIndex])) {
                FindDateFormatResult result = new FindDateFormatResult();
                result.dateFormat = new SimpleDateFormat(datePatterns[dateIndex]);
                result.includesTime = false;
                return result;
            }
            // Try matching a date value followed by a time value
            for (int timeIndex = 0; timeIndex < timeRegexes.length; timeIndex++) {
                if (dateString.matches(dateRegexes[dateIndex] + timeRegexes[timeIndex])) {
                    FindDateFormatResult result = new FindDateFormatResult();
                    result.dateFormat = new SimpleDateFormat(datePatterns[dateIndex] + timePatterns[timeIndex]);
                    result.includesTime = true;
                    return result;
                }
            }
        }
        throw new FileProcessingException("Could not handle this date format: " + dateString + ". Please see the help screen.");
     }

    private Date timeHandler(Date date, String timeString) throws FileProcessingException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

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

        if (dataFile.getLocalTimeConversionRequired()) {
            calendar.add(Calendar.HOUR, dataFile.getLocalTimeConversionHours().intValue());
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
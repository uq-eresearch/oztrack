package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.model.DataFile;
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

            if (dataFile.getDataFileType().equals(DataFileType.ACOUSTIC)) {
                 processRawAcoustic(dataFile);
            } else if (dataFile.getDataFileType().equals(DataFileType.POSITION_FIX)) {
                 processRawPositionFix(dataFile);
            }

            dataFileDao.save(dataFile);
        }

    }

    public void processRawAcoustic(DataFile dataFile) {

        logger.info("processing raw acoustic file : " + dataFile.getOzTrackFileName());

        EntityManager entityManager = OzTrackApplication.getApplicationContext().getDaoManager().getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {

            FileInputStream fstream = new FileInputStream(dataFile.getOzTrackFileName());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // get headers
            //HashMap <String, Integer> headingsMap = new HashMap<String, Integer>();
            HashMap <DataFileHeader, Integer> headerMap = new HashMap<DataFileHeader, Integer>();
            String strLine = br.readLine();
            String [] colHeadings =  strLine.split(",");

            // find out which columns contain which data
            for (int i=0; i < colHeadings.length; i++) {

                String heading = colHeadings[i].replaceAll("/","").replaceAll(" ","").toUpperCase();

                if (heading.equals(DataFileHeader.DATETIME.toString())) headerMap.put(DataFileHeader.DATETIME, i);
                if (heading.equals(DataFileHeader.ANIMALID.toString())) headerMap.put(DataFileHeader.ANIMALID, i);
                if (heading.equals(DataFileHeader.SENSOR1.toString())) headerMap.put(DataFileHeader.SENSOR1, i);
                if (heading.equals(DataFileHeader.UNITS1.toString())) headerMap.put(DataFileHeader.UNITS1, i);
                if (heading.equals(DataFileHeader.RECEIVERID.toString())) headerMap.put(DataFileHeader.RECEIVERID, i);
            }

            while ((strLine = br.readLine()) != null) {

            colHeadings = strLine.split(",");
            RawAcousticDetection rawAcousticDetection = new RawAcousticDetection();
            rawAcousticDetection.setDatetime(colHeadings[headerMap.get(DataFileHeader.DATETIME)]);
            rawAcousticDetection.setAnimalid(colHeadings[headerMap.get(DataFileHeader.ANIMALID)]);
            rawAcousticDetection.setSensor1(colHeadings[headerMap.get(DataFileHeader.SENSOR1)]);
            rawAcousticDetection.setUnits1(colHeadings[headerMap.get(DataFileHeader.UNITS1)]);
            rawAcousticDetection.setReceiverid(colHeadings[headerMap.get(DataFileHeader.RECEIVERID)]);
            entityManager.persist(rawAcousticDetection);
        }
        transaction.commit();

        dataFile.setStatus(DataFileStatus.COMPLETE);

        }
        catch (FileNotFoundException e) {
            dataFile.setStatus(DataFileStatus.FAILED);
            logger.error("Couldn't find the file");
        }
        catch (IOException e) {
            dataFile.setStatus(DataFileStatus.FAILED);
            logger.error("Couldn't read the file");
        }

        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        dataFileDao.update(dataFile);

    }

    public void processRawPositionFix(DataFile dataFile) {
        logger.info("processing a raw position fix file : " + dataFile.getOzTrackFileName());

    }



}

package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.*;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.ProjectType;
import org.oztrack.error.FileProcessingException;

import javax.swing.text.Position;
import java.io.*;
import java.util.*;

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

        if (dataFile != null) {

            dataFile.setStatus(DataFileStatus.PROCESSING);
            dataFileDao.update(dataFile);
            dataFileDao.refresh(dataFile);

            try {

                if (dataFile.getProject().getProjectType().equals(ProjectType.PASSIVE_ACOUSTIC)) {
                    AcousticFileLoader acousticFileLoader = new AcousticFileLoader(dataFile);
                    acousticFileLoader.process();
                    dataFile = acousticFileLoader.getDataFile();
                } else if (dataFile.getProject().getProjectType().equals(ProjectType.GPS)
                           || dataFile.getProject().getProjectType().equals(ProjectType.ARGOS)) {
                    PositionFixFileLoader positionFixFileLoader = new PositionFixFileLoader((dataFile));
                    positionFixFileLoader.process();
                }

                dataFile.setStatus(DataFileStatus.COMPLETE);
                dataFile.setStatusMessage( "File processing successfully completed on " + new Date().toString() + ". " +
                                           (dataFile.getLocalTimeConversionRequired()
                                            ? "Local time conversion is " + dataFile.getLocalTimeConversionHours()+ " hours." : "")
                                          );

            } catch (FileProcessingException e) {

                dataFile.setStatus(DataFileStatus.FAILED);
                dataFile.setStatusMessage(e.toString());

                // clean up on fail
                File file = new File(dataFile.getOzTrackFileName());
                File origFile = new File(dataFile.getOzTrackFileName().replace(".csv",".orig"));
                file.delete();
                origFile.delete();

            }

            dataFileDao.save(dataFile);
            dataFileDao.refresh(dataFile);

        }

    }


    public void processRawPositionFix(DataFile dataFile) {
        logger.info("processing a raw position fix file : " + dataFile.getOzTrackFileName());

    }



}
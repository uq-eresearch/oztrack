package org.oztrack.data.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.error.FileProcessingException;

import java.io.File;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 16/06/11
 * Time: 11:38 AM
 */
public class DataFileRunner {

    protected final Log logger = LogFactory.getLog(getClass());

    public void processNext() {

        // get the next datafile waiting to be processed if there's not one processing at the moment
        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        DataFile dataFile = dataFileDao.getNextDataFile();

        if (dataFile != null) {

            try {

                dataFile.setStatus(DataFileStatus.PROCESSING);
                dataFileDao.update(dataFile);

                switch (dataFile.getProject().getProjectType()) {
                    case PASSIVE_ACOUSTIC:
                        AcousticFileLoader acousticFileLoader = new AcousticFileLoader(dataFile);
                        acousticFileLoader.process();
                        break;
                    case ARGOS:
                    case GPS:
                        PositionFixFileLoader positionFixFileLoader = new PositionFixFileLoader(dataFile);
                        positionFixFileLoader.process();
                        break;
                    case ACTIVE_ACOUSTIC:
                    case RADIO:
                        break;
                    default:
                        break;
                }

                dataFile.setStatus(DataFileStatus.COMPLETE);
                dataFile.setStatusMessage( "File processing successfully completed on "
                                            + new Date().toString() + ". "
                                            + (dataFile.getLocalTimeConversionRequired()
                                                ? "Local time conversion is " + dataFile.getLocalTimeConversionHours()+ " hours."
                                                : "")
                                          );

            } catch (FileProcessingException e) {

                dataFile.setStatus(DataFileStatus.FAILED);
                dataFile.setStatusMessage(e.toString());

                // clean up on fail
                File file = new File(dataFile.getOzTrackFileName());
                File origFile = new File(dataFile.getOzTrackFileName().replace(".csv",".orig"));
                file.delete();
                origFile.delete();
                
                JdbcAccess jdbcAccess = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcAccess();
                jdbcAccess.truncateRawObservations(dataFile);
            }

            dataFileDao.update(dataFile);//dataFileDao.refresh(dataFile);

        }
    }

}

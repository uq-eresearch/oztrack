package org.oztrack.data.loader;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.types.DataFileStatus;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 21/04/11
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataFileLoader
{

    public void processNext() {

        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        DataFile dataFile = dataFileDao.getDataFileById(Long.valueOf("1"));

        dataFile.setStatus(DataFileStatus.PROCESSING);

        dataFileDao.save(dataFile);

    }


}

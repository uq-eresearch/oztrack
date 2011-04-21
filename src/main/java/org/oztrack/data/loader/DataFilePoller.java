package org.oztrack.data.loader;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 21/04/11
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataFilePoller extends QuartzJobBean {

    private DataFileLoader dataFileLoader;

    public void setDataFileLoader(DataFileLoader dataFileLoader) {
        this.dataFileLoader = dataFileLoader;
    }

    protected void executeInternal(JobExecutionContext context)
        throws JobExecutionException {
        dataFileLoader.processNext();
    }
}

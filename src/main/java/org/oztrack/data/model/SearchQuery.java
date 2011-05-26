package org.oztrack.data.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 12:27 PM
 */

public class SearchQuery {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private Date fromDate;
    private Date toDate;
    private String projectAnimalId;
    private String receiverOriginalId;
    private String sensorType;

    public SearchQuery() {
        this.fromDate = null;
        this.toDate = null;
        this.projectAnimalId = "";
        this.receiverOriginalId = "";
        this.sensorType = "";
    }


    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getProjectAnimalId() {
        return projectAnimalId;
    }

    public void setProjectAnimalId(String projectAnimalId) {
        this.projectAnimalId = projectAnimalId;
    }

    public String getReceiverOriginalId() {
        return receiverOriginalId;
    }

    public void setReceiverOriginalId(String receiverOriginalId) {
        this.receiverOriginalId = receiverOriginalId;
    }


    public String buildQuery() {

        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        String select = "SELECT ad.id as acousticdetectionid "
                      + ", ad.detectionTime "
                      + ", ad.animal_id "
                      + ", ad.receiverdeployment_id "
                      + ", ad.datafile_id "
                      + ", ad.sensor1value "
                      + ", ad.sensor1units "
                      + ", a.id as animalid "
                      + ", a.projectanimalid "
                      + ", d.uploaddate as datafile_uploaddate"
                      + ", rd.originalid as receiverdeployment_originalid";

        String from = " FROM acousticdetection ad"
                    + ", animal a "
                    + ", datafile d "
                    + ", receiverdeployment rd ";

        String joinClause = " WHERE ad.animal_id=a.id"
                     + " AND ad.receiverdeployment_id=rd.id "
                     + " AND ad.datafile_id = d.id ";

        String where = "";

        if (this.projectAnimalId.length() != 0) {
            where = where + " AND a.projectanimalid = '"
                          + this.projectAnimalId + "'";
        }

        if (this.toDate != null) {
            where = where + " AND ad.detectiontime <= to_date('"
                          + sdf.format(this.toDate) + "','" + dateFormat + "')";
        }

        if (this.fromDate != null) {
            where = where + " AND ad.detectiontime >= to_date('"
                          + sdf.format(this.fromDate) + "','" + dateFormat + "')";
        }

        if (this.receiverOriginalId.length() != 0) { //(this.receiverOriginalId.length() != 0)  {
            where = where + " AND rd.originalid = '"
                          + this.receiverOriginalId + "'";
        }

        String sql = select + from + joinClause + where;

        logger.debug(sql);
        return sql;

    }

}

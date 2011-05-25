package org.oztrack.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 12:27 PM
 */

public class SearchQuery {

    private Date fromDate;
    private Date toDate;
    private String projectAnimalId;
    private String receiverOriginalId;
    private String sensorType;


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




}

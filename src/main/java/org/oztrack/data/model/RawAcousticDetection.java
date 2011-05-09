package org.oztrack.data.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 13/04/11
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class RawAcousticDetection {

    // ultimately passed to the acousticdetection table
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acousticdetectionid_seq")
    @SequenceGenerator(name = "acousticdetectionid_seq", sequenceName = "acousticdetectionid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    private Date datetime;
    private String animalid;
    private Double sensor1;
    private String units1;
    private String codespace;
    private String units2;
    private Double sensor2;
    private String transmittername;
    private String transmittersn;
    private String receivername;
    private String receiversn;
    private String stationname;
    private String stationlatitude;
    private String stationlongitude;

//    private Long oztrackAnimalId;
//    private Long oztrackReceiverDeploymentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getAnimalid() {
        return animalid;
    }

    public void setAnimalid(String animalid) {
        this.animalid = animalid;
    }

    public Double getSensor1() {
        return sensor1;
    }

    public void setSensor1(Double sensor1) {
        this.sensor1 = sensor1;
    }

    public String getUnits1() {
        return units1;
    }

    public void setUnits1(String units1) {
        this.units1 = units1;
    }

    public String getCodespace() {
        return codespace;
    }

    public void setCodespace(String codespace) {
        this.codespace = codespace;
    }

    public String getUnits2() {
        return units2;
    }

    public void setUnits2(String units2) {
        this.units2 = units2;
    }

    public Double getSensor2() {
        return sensor2;
    }

    public void setSensor2(Double sensor2) {
        this.sensor2 = sensor2;
    }

    public String getTransmittername() {
        return transmittername;
    }

    public void setTransmittername(String transmittername) {
        this.transmittername = transmittername;
    }

    public String getTransmittersn() {
        return transmittersn;
    }

    public void setTransmittersn(String transmittersn) {
        this.transmittersn = transmittersn;
    }

    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }

    public String getReceiversn() {
        return receiversn;
    }

    public void setReceiversn(String receiversn) {
        this.receiversn = receiversn;
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname;
    }

    public String getStationlatitude() {
        return stationlatitude;
    }

    public void setStationlatitude(String stationlatitude) {
        this.stationlatitude = stationlatitude;
    }

    public String getStationlongitude() {
        return stationlongitude;
    }

    public void setStationlongitude(String stationlongitude) {
        this.stationlongitude = stationlongitude;
    }
/*
    public Long getOztrackAnimalId() {
         return oztrackAnimalId;
     }

     public void setOztrackAnimalId(Long oztrackAnimalId) {
         this.oztrackAnimalId = oztrackAnimalId;
     }

     public Long getOztrackReceiverDeploymentId() {
         return oztrackReceiverDeploymentId;
     }

     public void setOztrackReceiverDeploymentId(Long oztrackReceiverDeploymentId) {
         this.oztrackReceiverDeploymentId = oztrackReceiverDeploymentId;
     }
*/



}
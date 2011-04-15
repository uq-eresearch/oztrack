package org.oztrack.data.model;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 13/04/11
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class RawAcousticDetection {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rawacousticdetectionid_seq")
    @SequenceGenerator(name = "rawacousticdetectionid_seq", sequenceName = "rawacousticdetectionid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    private String datetime;
    private String animalid;
    private String sensor1;
    private String units1;
    private String receiverid;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAnimalid() {
        return animalid;
    }

    public void setAnimalid(String animalid) {
        this.animalid = animalid;
    }

    public String getSensor1() {
        return sensor1;
    }

    public void setSensor1(String sensor1) {
        this.sensor1 = sensor1;
    }

    public String getUnits1() {
        return units1;
    }

    public void setUnits1(String units1) {
        this.units1 = units1;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }




}
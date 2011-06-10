package org.oztrack.data.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 10/06/11
 * Time: 10:12 AM
 */

@Entity
public class RawPositionFix {

    // ultimately passed to the positionfix table
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "positionfixid_seq")
    @SequenceGenerator(name = "positionfixid_seq", sequenceName = "positionfixid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
    private String animalId;
    private String latitude;
    private String longitude;
    private Double HDOP;

    private Double sensor1;
    private Double sensor2;


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

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Double getHDOP() {
        return HDOP;
    }

    public void setHDOP(Double HDOP) {
        this.HDOP = HDOP;
    }

    public Double getSensor1() {
        return sensor1;
    }

    public void setSensor1(Double sensor1) {
        this.sensor1 = sensor1;
    }

    public Double getSensor2() {
        return sensor2;
    }

    public void setSensor2(Double sensor2) {
        this.sensor2 = sensor2;
    }





}

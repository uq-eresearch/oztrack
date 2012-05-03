package org.oztrack.data.model;

//import org.postgis.Point;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

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
    private Date detectionTime;
    private String animalId;
    private String latitude;
    private String longitude;
    private Double HDOP;

    private Double sensor1value;
    private String sensor1units;
    private Double sensor2value;
    private String sensor2units;

    @Column(name = "locationgeometry", columnDefinition="GEOMETRY")
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Point locationGeometry;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(Date detectionTime) {
        this.detectionTime = detectionTime;
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

    public Double getSensor1value() {
         return sensor1value;
     }

     public void setSensor1value(Double sensor1value) {
         this.sensor1value = sensor1value;
     }

     public String getSensor1units() {
         return sensor1units;
     }

     public void setSensor1units(String sensor1units) {
         this.sensor1units = sensor1units;
     }

     public Double getSensor2value() {
         return sensor2value;
     }

     public void setSensor2value(Double sensor2value) {
         this.sensor2value = sensor2value;
     }

     public String getSensor2units() {
         return sensor2units;
     }

     public void setSensor2units(String sensor2units) {
         this.sensor2units = sensor2units;
     }

    public Point getLocationGeometry() {
        return locationGeometry;
    }

    public void setLocationGeometry(Point locationGeometry) {
        this.locationGeometry = locationGeometry;
    }


}

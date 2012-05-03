package org.oztrack.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 16/06/11
 * Time: 3:19 PM
 */
@Entity(name="PositionFix")
public class PositionFix implements Serializable{

    // id set in raw position fix

    @Id
    @Column(nullable=false)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date detectionTime;

    private String latitude;
    private String longitude;

    @ManyToOne
    private Animal animal;

    @ManyToOne
    private DataFile dataFile;

    @Column(name = "locationgeometry", columnDefinition="GEOMETRY")
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Point locationGeometry;

    private Double HDOP;
    private Double sensor1Value;
    private Double sensor2Value;
    private String sensor1Units;
    private String sensor2Units;

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



    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public Double getSensor1Value() {
        return sensor1Value;
    }

    public void setSensor1Value(Double sensor1Value) {
        this.sensor1Value = sensor1Value;
    }

    public Double getSensor2Value() {
        return sensor2Value;
    }

    public void setSensor2Value(Double sensor2Value) {
        this.sensor2Value = sensor2Value;
    }

       public Double getHDOP() {
        return HDOP;
    }

    public void setHDOP(Double HDOP) {
        this.HDOP = HDOP;
    }

    public Point getLocationGeometry() {
        return locationGeometry;
    }

    public void setLocationGeometry(Point locationGeometry) {
        this.locationGeometry = locationGeometry;
    }

        public String getSensor1Units() {
        return sensor1Units;
    }

    public void setSensor1Units(String sensor1Units) {
        this.sensor1Units = sensor1Units;
    }

    public String getSensor2Units() {
        return sensor2Units;
    }

    public void setSensor2Units(String sensor2Units) {
        this.sensor2Units = sensor2Units;
    }



}

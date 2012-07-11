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

@Entity(name="PositionFix")
public class PositionFix implements Serializable{
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
    
    private Boolean deleted;

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

    public Point getLocationGeometry() {
        return locationGeometry;
    }

    public void setLocationGeometry(Point locationGeometry) {
        this.locationGeometry = locationGeometry;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
package org.oztrack.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.oztrack.data.model.types.ArgosClass;

import com.vividsolutions.jts.geom.Point;

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

    @Column(name = "locationgeometry", columnDefinition="GEOMETRY")
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Point locationGeometry;

    @Column(nullable=false)
    private Boolean deleted;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="argosclass")
    private ArgosClass argosClass;

    @Column(name="dop", columnDefinition="numeric")
    private Double dop;

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

    public ArgosClass getArgosClass() {
        return argosClass;
    }

    public void setArgosClass(ArgosClass argosClass) {
        this.argosClass = argosClass;
    }

    public Double getDop() {
        return dop;
    }

    public void setDop(Double dop) {
        this.dop = dop;
    }
}
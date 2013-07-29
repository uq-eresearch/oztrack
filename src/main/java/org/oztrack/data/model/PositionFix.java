package org.oztrack.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.oztrack.data.model.types.ArgosClass;

import com.vividsolutions.jts.geom.Point;

@Entity(name="PositionFix")
public class PositionFix {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="positionfixid_seq")
    @SequenceGenerator(name="positionfixid_seq", sequenceName="positionfixid_seq", allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date detectionTime;

    @Column(nullable=false)
    private String latitude;

    @Column(nullable=false)
    private String longitude;

    @ManyToOne
    @JoinColumn(name="animal_id", nullable=false)
    private Animal animal;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="datafile_id", nullable=true)
    private DataFile dataFile;

    @Column(name="locationgeometry", columnDefinition="GEOMETRY", nullable=false)
    @Type(type="org.hibernatespatial.GeometryUserType")
    private Point locationGeometry;

    @Column(nullable=false)
    private Boolean deleted = Boolean.FALSE;

    @Column(nullable=false)
    private Boolean probable = Boolean.FALSE;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="argosclass")
    private ArgosClass argosClass;

    @Column(name="dop", columnDefinition="numeric")
    private Double dop;

    @Column(name="sst", columnDefinition="numeric")
    private Double sst;

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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    public Boolean getProbable() {
        return probable;
    }

    public void setProbable(Boolean probable) {
        this.probable = probable;
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

    public Double getSst() {
        return sst;
    }

    public void setSst(Double sst) {
        this.sst = sst;
    }
}
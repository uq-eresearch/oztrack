package org.oztrack.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "Animal")
public class Animal extends OztrackBaseEntity implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animalid_seq")
    @SequenceGenerator(name = "animalid_seq", sequenceName = "animalid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    private String projectAnimalId;
    private String animalName;
    private String animalDescription;
    private String speciesName;
    private String verifiedSpeciesName;
    private String transmitterTypeCode;
    private String transmitterId;
    private Long pingIntervalSeconds;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transmitterDeployDate;
    @ManyToOne
    private Project project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectAnimalId() {
        return projectAnimalId;
    }

    public void setProjectAnimalId(String projectAnimalId) {
        this.projectAnimalId = projectAnimalId;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getAnimalDescription() {
        return animalDescription;
    }

    public void setAnimalDescription(String animalDescription) {
        this.animalDescription = animalDescription;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getVerifiedSpeciesName() {
        return verifiedSpeciesName;
    }

    public void setVerifiedSpeciesName(String verifiedSpeciesName) {
        this.verifiedSpeciesName = verifiedSpeciesName;
    }

    public String getTransmitterTypeCode() {
        return transmitterTypeCode;
    }

    public void setTransmitterTypeCode(String transmitterTypeCode) {
        this.transmitterTypeCode = transmitterTypeCode;
    }

    public String getTransmitterId() {
        return transmitterId;
    }

    public void setTransmitterId(String transmitterId) {
        this.transmitterId = transmitterId;
    }

    public Long getPingIntervalSeconds() {
        return pingIntervalSeconds;
    }

    public void setPingIntervalSeconds(Long pingIntervalSeconds) {
        this.pingIntervalSeconds = pingIntervalSeconds;
    }

    public Date getTransmitterDeployDate() {
        return transmitterDeployDate;
    }

    public void setTransmitterDeployDate(Date transmitterDeployDate) {
        this.transmitterDeployDate = transmitterDeployDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}

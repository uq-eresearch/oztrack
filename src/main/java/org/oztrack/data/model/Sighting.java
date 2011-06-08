package org.oztrack.data.model;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/06/11
 * Time: 9:05 AM
 */
@Entity(name = "sighting")
public class Sighting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sightingid_seq")
    @SequenceGenerator(name = "sightingid_seq", sequenceName = "sightingid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sightingDate;

    private String sightingTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private double latitude;
    private double longitude;
    private String localityDescription;
    private String speciesCommonName;
    private String speciesScientificName;
    private String animalDescription;
    private String comments;
    private String contactName;
    private String contactEmail;
    private String imageLocation;

    @Transient
    private MultipartFile imageFile;
    public void setImageFile(MultipartFile imageFile) {this.imageFile = imageFile;}
    public MultipartFile getImageFile() {return imageFile;}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSightingDate() {
        return sightingDate;
    }

    public void setSightingDate(Date sightingDate) {
        this.sightingDate = sightingDate;
    }

    public String getSightingTime() {
        return sightingTime;
    }

    public void setSightingTime(String sightingTime) {
        this.sightingTime = sightingTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocalityDescription() {
        return localityDescription;
    }

    public void setLocalityDescription(String localityDescription) {
        this.localityDescription = localityDescription;
    }

    public String getSpeciesCommonName() {
        return speciesCommonName;
    }

    public void setSpeciesCommonName(String speciesCommonName) {
        this.speciesCommonName = speciesCommonName;
    }

    public String getSpeciesScientificName() {
        return speciesScientificName;
    }

    public void setSpeciesScientificName(String speciesScientificName) {
        this.speciesScientificName = speciesScientificName;
    }

    public String getAnimalDescription() {
        return animalDescription;
    }

    public void setAnimalDescription(String animalDescription) {
        this.animalDescription = animalDescription;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }



}

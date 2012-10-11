package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name="data_licence")
public class DataLicence {
    @Id
    @Column(name="id", nullable=false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="data_licence_id_seq")
    @SequenceGenerator(name="data_licence_id_seq", sequenceName="data_licence_id_seq", allocationSize=1)
    private Long id;

    @Column(name="title", columnDefinition="text", unique=true, nullable=false)
    private String title;

    @Column(name="description", columnDefinition="text", nullable=false)
    private String description;

    @Column(name="info_url", columnDefinition="text", nullable=false)
    private String infoUrl;

    @Column(name="image_url", columnDefinition="text", nullable=false)
    private String imageUrl;

    public DataLicence() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
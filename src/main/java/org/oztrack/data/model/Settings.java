package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name="settings")
public class Settings {
    @Id
    @Column(name="id")
    @GeneratedValue(generator="settings_id_seq", strategy=GenerationType.IDENTITY)
    @SequenceGenerator(name="settings_id_seq", sequenceName="settings_id_seq")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String homeText;

    @Column(columnDefinition = "TEXT")
    private String aboutText;

    @Column(columnDefinition = "TEXT")
    private String contactText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHomeText() {
        return homeText;
    }

    public void setHomeText(String homeText) {
        this.homeText = homeText;
    }

    public String getAboutText() {
        return aboutText;
    }

    public void setAboutText(String aboutText) {
        this.aboutText = aboutText;
    }

    public String getContactText() {
        return contactText;
    }

    public void setContactText(String contactText) {
        this.contactText = contactText;
    }
}

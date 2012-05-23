package org.oztrack.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name="settings")
public class Settings implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(generator="settings_id_seq", strategy=GenerationType.IDENTITY)
    @SequenceGenerator(name="settings_id_seq", sequenceName="settings_id_seq")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String aboutText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAboutText() {
        return aboutText;
    }

    public void setAboutText(String aboutText) {
        this.aboutText = aboutText;
    }
}

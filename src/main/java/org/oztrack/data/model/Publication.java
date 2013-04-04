package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity(name="publication")
public class Publication {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="publication_id_seq")
    @SequenceGenerator(name="publication_id_seq", sequenceName="publication_id_seq", allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable=false)
    private Project project;

    @Column(name="ordinal", nullable=false)
    private Integer ordinal;

    @Column(name="title", columnDefinition="TEXT", nullable=false)
    private String title;

    @Column(name="url", columnDefinition="TEXT", nullable=true)
    private String url;

    public Publication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
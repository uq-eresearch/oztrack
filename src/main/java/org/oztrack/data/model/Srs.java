package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Polygon;

@Entity(name="srs")
public class Srs {
    @Id
    @Column(name="id", nullable=false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="srs_id_seq")
    @SequenceGenerator(name="srs_id_seq", sequenceName="srs_id_seq", allocationSize=1)
    private Long id;

    @Column(name="identifier", columnDefinition="text", unique=true, nullable=false)
    private String identifier;

    @Column(name="title", columnDefinition="text", unique=true, nullable=false)
    private String title;

    @Column(name="bounds", columnDefinition="geometry", nullable=false)
    @Type(type="org.hibernatespatial.GeometryUserType")
    private Polygon bounds;

    public Srs() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public void setBounds(Polygon bounds) {
        this.bounds = bounds;
    }
}

package org.oztrack.data.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity(name = "Animal")
public class Animal extends OzTrackBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "animalid_seq")
    @SequenceGenerator(name = "animalid_seq", sequenceName = "animalid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    @Column(nullable=true)
    private String projectAnimalId;

    @Column(nullable=true)
    private String animalName;

    private String animalDescription;
    private String speciesName;

    @Column(name="colour", nullable=true)
    private String colour;

    @ManyToOne
    @JoinColumn(nullable=false)
    private Project project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "animal")
    private List<PositionFix> positionFixes = new LinkedList<PositionFix>();

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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<PositionFix> getPositionFixes() {
        return positionFixes;
    }

    public void setPositionFixes(List<PositionFix> positionFixes) {
        this.positionFixes = positionFixes;
    }
}
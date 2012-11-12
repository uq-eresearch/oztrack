package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.oztrack.data.model.types.MapQueryType;

@Entity
@Table(name="analysis")
public class Analysis extends OzTrackBaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="analysis_id_seq")
    @SequenceGenerator(name="analysis_id_seq", sequenceName="analysis_id_seq",allocationSize=1)
    @Column(name="id", nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Enumerated(STRING)
    @Column(name="analysistype", columnDefinition="text", nullable=false)
    private MapQueryType analysisType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fromdate", nullable=true)
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="todate", nullable=true)
    private Date toDate;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="analysis_animal",
        joinColumns=@JoinColumn(name="analysis_id"),
        inverseJoinColumns=@JoinColumn(name="animal_id")
    )
    private Set<Animal> animals;

    @OneToMany(mappedBy="analysis", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private Set<AnalysisParameter> parameters;

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

    public MapQueryType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(MapQueryType analysisType) {
        this.analysisType = analysisType;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Set<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(Set<Animal> animals) {
        this.animals = animals;
    }

    public Set<AnalysisParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<AnalysisParameter> parameters) {
        this.parameters = parameters;
    }
}
package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.model.types.AnalysisParameterType;
import org.oztrack.data.model.types.AnalysisStatus;
import org.oztrack.data.model.types.AnalysisType;

@Entity
@Table(name="analysis")
public class Analysis extends OzTrackBaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="analysis_id_seq")
    @SequenceGenerator(name="analysis_id_seq", sequenceName="analysis_id_seq",allocationSize=1)
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="createsession", columnDefinition="text")
    private String createSession;

    @Enumerated(STRING)
    @Column(name="status", columnDefinition="text", nullable=false)
    private AnalysisStatus status;

    @Column(name="message", columnDefinition="text")
    private String message;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Enumerated(STRING)
    @Column(name="analysistype", columnDefinition="text", nullable=false)
    private AnalysisType analysisType;

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

    @Column(name="resultfilepath", columnDefinition="text")
    private String resultFilePath;

    @OneToMany(mappedBy="analysis", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private Set<AnalysisResultFeature> resultFeatures;

    @Column(name="saved", nullable=false)
    private boolean saved;

    @Column(name="description", columnDefinition="text")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateSession() {
        return createSession;
    }

    public void setCreateSession(String createSession) {
        this.createSession = createSession;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType) {
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

    public AnalysisParameter getParameter(String name) {
        for (AnalysisParameter parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    public Object getParameterValue(String name, boolean useDefault) {
        AnalysisParameterType parameterType = analysisType.getParameterType(name);
        AnalysisParameter parameter = getParameter(parameterType.getIdentifier());
        if ((parameter == null) || !parameterType.isValid(parameter.getValue())) {
            return null;
        }
        String stringValue =
            StringUtils.isNotBlank(parameter.getValue()) ? parameter.getValue()
            : useDefault ? parameterType.getDefaultValue()
            : null;
        return
            (stringValue == null) ? null
            : parameterType.getDataType().equals("double") ? Double.valueOf(stringValue)
            : parameterType.getDataType().equals("boolean") ?  Boolean.valueOf(stringValue)
            : stringValue;
    }

    public Set<AnalysisResultFeature> getResultFeatures() {
        return resultFeatures;
    }

    public void setResultFeatures(Set<AnalysisResultFeature> resultFeatures) {
        this.resultFeatures = resultFeatures;
    }

    public String getResultFilePath() {
        return resultFilePath;
    }

    public void setResultFilePath(String resultFilePath) {
        this.resultFilePath = resultFilePath;
    }

    public String getAbsoluteResultFilePath() {
        return project.getAbsoluteDataDirectoryPath() + File.separator + getResultFilePath();
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SearchQuery toSearchQuery() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(this.getProject());
        searchQuery.setFromDate(this.getFromDate());
        searchQuery.setToDate(this.getToDate());
        List<Long> animalIds = new ArrayList<Long>();
        for (Animal animal : this.getAnimals()) {
            animalIds.add(animal.getId());
        }
        searchQuery.setAnimalIds(animalIds);
        return searchQuery;
    }
}
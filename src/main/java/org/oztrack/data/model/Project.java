package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.types.ProjectType;

@Entity(name="Project")
public class Project extends OzTrackBaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="projectid_seq")
    @SequenceGenerator(name="projectid_seq", sequenceName="projectid_seq",allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(columnDefinition="TEXT")
    private String description;

    private boolean isGlobal;

    private String spatialCoverageDescr;
    @ManyToOne
    private User dataSpaceAgent;

    @Column(columnDefinition="TEXT")
    private String publicationTitle;

    @Column(columnDefinition="TEXT")
    private String publicationUrl;

    @Column(columnDefinition="TEXT")
    private String dataDirectoryPath;

    @OneToMany(mappedBy="project", fetch=FetchType.EAGER, cascade={CascadeType.ALL}, orphanRemoval=true)
    private List<ProjectUser> projectUsers = new LinkedList<ProjectUser>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy="project", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Animal> animals = new LinkedList<Animal>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy="project", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<DataFile> dataFiles = new LinkedList<DataFile>();

    @Enumerated(STRING)
    @Column(name="projecttype")
    private ProjectType projectType;

    private String speciesCommonName;

    private String speciesScientificName;

    @Column(name="srsidentifier")
    private String srsIdentifier;

    @Column(columnDefinition="TEXT")
    private String dataSpaceURI;
    private Date dataSpaceUpdateDate;
    private String rightsStatement;

    @ManyToOne
    @JoinColumn(name="data_licence_id")
    private DataLicence dataLicence;

    public Project() {
    }

    public Project(String title) {
        this.title = title;
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

    public boolean getIsGlobal() {
        return isGlobal;
    }

    public void setIsGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public List<ProjectUser> getProjectUsers() {
        return this.projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public List<DataFile> getDataFiles() {
        return dataFiles;
    }

    public void setDataFiles(List<DataFile> dataFiles) {
        this.dataFiles = dataFiles;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public String getSpatialCoverageDescr() {
        return spatialCoverageDescr;
    }

    public void setSpatialCoverageDescr(String spatialCoverageDescr) {
        this.spatialCoverageDescr = spatialCoverageDescr;
    }

    public User getDataSpaceAgent() {
        return dataSpaceAgent;
    }
    public void setDataSpaceAgent(User dataSpaceAgent) {
        this.dataSpaceAgent = dataSpaceAgent;
    }
    public String getPublicationTitle() {
        return publicationTitle;
    }

    public void setPublicationTitle(String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }

    public String getPublicationUrl() {
        return publicationUrl;
    }

    public void setPublicationUrl(String publicationUrl) {
        this.publicationUrl = publicationUrl;
    }


    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
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

    public String getSrsIdentifier() {
        return srsIdentifier;
    }

    public void setSrsIdentifier(String srsIdentifier) {
        this.srsIdentifier = srsIdentifier;
    }

    public String getDataDirectoryPath() {
        return dataDirectoryPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        this.dataDirectoryPath = dataDirectoryPath;
    }

    public String getAbsoluteDataDirectoryPath() {
        return OzTrackApplication.getApplicationContext().getDataDir() + File.separator + getDataDirectoryPath();
    }

    public String getDataSpaceURI() {
        return dataSpaceURI;
    }

    public void setDataSpaceURI(String dataSpaceURI) {
        this.dataSpaceURI = dataSpaceURI;
    }

    public Date getDataSpaceUpdateDate() {
        return dataSpaceUpdateDate;
    }

    public void setDataSpaceUpdateDate(Date dataSpaceUpdateDate) {
        this.dataSpaceUpdateDate = dataSpaceUpdateDate;
    }

    public String getRightsStatement() {
        return rightsStatement;
    }

    public void setRightsStatement(String rightsStatement) {
        this.rightsStatement = rightsStatement;
    }

    public DataLicence getDataLicence() {
        return dataLicence;
    }

    public void setDataLicence(DataLicence dataLicence) {
        this.dataLicence = dataLicence;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Project)) {
            return false;
        }
        Project other = (Project) obj;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }
}

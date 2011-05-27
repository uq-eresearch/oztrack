package org.oztrack.data.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: peggy
 * Date: 28/03/2010
 */

@Entity(name = "Project")
public class Project implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectid_seq")
    @SequenceGenerator(name = "projectid_seq", sequenceName = "projectid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;
    
    @Column(unique = true)
    private String title;
    private String description;
    private boolean isGlobal;
    private String spatialCoverageDescr;
    private String temporalCoverageDescr;
    private String contactName;
    private String contactOrganisation;
    private String contactEmail;
    private String contactUrl;
    private String custodianName;
    private String custodianOrganisation;
    private String custodianEmail;
    private String custodianUrl;
    private String publicationTitle;
    private String publicationUrl;
    private Date createDate;
    private Date updateDate;
    private User createdBy;
    private User updatedBy;



    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.project", cascade =
    {CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
    org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<ProjectUser> projectUsers = new LinkedList<ProjectUser>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade =
    {CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
    org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<DataFile> dataFiles = new LinkedList<DataFile>();

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

    public String getCustodianName() {
        return custodianName;
    }

    public void setCustodianName(String custodianName) {
        this.custodianName = custodianName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public List<ProjectUser> getProjectUsers() {
        return this.projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public List<DataFile> getDataFiles() {
        return this.dataFiles;
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

    public String getTemporalCoverageDescr() {
        return temporalCoverageDescr;
    }

    public void setTemporalCoverageDescr(String temporalCoverageDescr) {
        this.temporalCoverageDescr = temporalCoverageDescr;
    }

    public String getContactOrganisation() {
        return contactOrganisation;
    }

    public void setContactOrganisation(String contactOrganisation) {
        this.contactOrganisation = contactOrganisation;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getCustodianOrganisation() {
        return custodianOrganisation;
    }

    public void setCustodianOrganisation(String custodianOrganisation) {
        this.custodianOrganisation = custodianOrganisation;
    }

    public String getCustodianEmail() {
        return custodianEmail;
    }

    public void setCustodianEmail(String custodianEmail) {
        this.custodianEmail = custodianEmail;
    }

    public String getCustodianUrl() {
        return custodianUrl;
    }

    public void setCustodianUrl(String custodianUrl) {
        this.custodianUrl = custodianUrl;
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


    
}

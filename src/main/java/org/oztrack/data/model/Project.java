package org.oztrack.data.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

import java.io.Serializable;
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
    private String organisationName;
    private String custodianName;
    private String contactName;
    
    
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
    
    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
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
    
    
}

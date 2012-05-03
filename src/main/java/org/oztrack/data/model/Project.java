package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.oztrack.data.model.types.ProjectType;
import org.springframework.web.multipart.MultipartFile;

import com.vividsolutions.jts.geom.Polygon;

/**
 * Author: peggy
 * Date: 28/03/2010
 */

@Entity(name = "Project")
public class Project extends OztrackBaseEntity implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projectid_seq")
    @SequenceGenerator(name = "projectid_seq", sequenceName = "projectid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;
    
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private boolean isGlobal;
    private String spatialCoverageDescr;
    @ManyToOne
    private User dataSpaceAgent;
//    private String contactGivenName;
//    private String contactFamilyName;
//    private String contactOrganisation;
//    private String contactEmail;
//    @Column(columnDefinition = "TEXT")
//    private String contactUrl;
    @Column(columnDefinition = "TEXT")
    private String publicationTitle;
    @Column(columnDefinition = "TEXT")
    private String publicationUrl;
    @Column(columnDefinition = "TEXT")
    private String dataDirectoryPath;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.project", cascade =
    {CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
    org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<ProjectUser> projectUsers = new LinkedList<ProjectUser>();

    @Enumerated(STRING)
    @Column(name="projecttype")
    private ProjectType projectType;

    private String speciesCommonName;
    private String speciesScientificName;
    private String imageFileLocation;

    @Transient
    private MultipartFile imageFile;
    public void setImageFile(MultipartFile imageFile) {this.imageFile = imageFile;}
    public MultipartFile getImageFile() {return imageFile;}

    @Column(name = "boundingbox", columnDefinition="GEOMETRY")
    @Type(type = "org.hibernatespatial.GeometryUserType")
    private Polygon boundingBox;
	private Date firstDetectionDate;
    private Date lastDetectionDate;
    private Integer detectionCount; 
    
    @Column(columnDefinition = "TEXT")
    private String dataSpaceURI;
    private Date dataSpaceUpdateDate;
    private String rightsStatement;

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

//    public String getContactGivenName() {
//		return contactGivenName;
//	}
//    
//	public void setContactGivenName(String contactGivenName) {
//		this.contactGivenName = contactGivenName;
//	}
//	public String getContactFamilyName() {
//		return contactFamilyName;
//	}
//	public void setContactFamilyName(String contactFamilyName) {
//		this.contactFamilyName = contactFamilyName;
//	}
//
//    public String getContactOrganisation() {
//        return contactOrganisation;
//    }
//
//    public void setContactOrganisation(String contactOrganisation) {
//        this.contactOrganisation = contactOrganisation;
//    }
//
//    public String getContactEmail() {
//        return contactEmail;
//    }
//
//    public void setContactEmail(String contactEmail) {
//        this.contactEmail = contactEmail;
//    }
//
//    public String getContactUrl() {
//        return contactUrl;
//    }
//
//    public void setContactUrl(String contactUrl) {
//        this.contactUrl = contactUrl;
//    }
//    
    public List<ProjectUser> getProjectUsers() {
        return this.projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
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

    public String getImageFileLocation() {
        return imageFileLocation;
    }

    public void setImageFileLocation(String imageFileLocation) {
        this.imageFileLocation = imageFileLocation;
    }

    public String getDataDirectoryPath() {
        return dataDirectoryPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        this.dataDirectoryPath = dataDirectoryPath;
    }

    public Polygon getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Polygon boundingBox) {
        this.boundingBox = boundingBox;
    }
    
	public Date getFirstDetectionDate() {
		return firstDetectionDate;
	}
	public void setFirstDetectionDate(Date firstDetectionDate) {
		this.firstDetectionDate = firstDetectionDate;
	}
	public Date getLastDetectionDate() {
		return lastDetectionDate;
	}
	public void setLastDetectionDate(Date lastDetectionDate) {
		this.lastDetectionDate = lastDetectionDate;
	}

	public Integer getDetectionCount() {
		return detectionCount;
	}
	public void setDetectionCount(Integer detectionCount) {
		this.detectionCount = detectionCount;
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
	
	public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }
}

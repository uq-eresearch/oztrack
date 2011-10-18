package org.oztrack.data.model;

import com.vividsolutions.jts.geom.Polygon;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.oztrack.data.model.types.ProjectType;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static javax.persistence.EnumType.STRING;

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
    
    @Column(unique = true)
    private String title;
    private String description;
    private boolean isGlobal;
    private String spatialCoverageDescr;
    private String temporalCoverageDescr;
    private String contactGivenName;
    private String contactFamilyName;
    private String contactOrganisation;
    private String contactEmail;
    private String contactUrl;
    private String custodianName;
    private String custodianOrganisation;
    private String custodianEmail;
    private String custodianUrl;
    private String publicationTitle;
    private String publicationUrl;
    private String dataDirectoryPath;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.project", cascade =
    {CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
    org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<ProjectUser> projectUsers = new LinkedList<ProjectUser>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade =
//    {CascadeType.PERSIST, CascadeType.MERGE})
//    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
//    org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
//    private List<DataFile> dataFiles = new LinkedList<DataFile>();

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

    public String getContactGivenName() {
		return contactGivenName;
	}
	public void setContactGivenName(String contactGivenName) {
		this.contactGivenName = contactGivenName;
	}
	public String getContactFamilyName() {
		return contactFamilyName;
	}
	public void setContactFamilyName(String contactFamilyName) {
		this.contactFamilyName = contactFamilyName;
	}
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
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DataFile)) {
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

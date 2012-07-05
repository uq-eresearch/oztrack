package org.oztrack.data.model;


import static javax.persistence.EnumType.STRING;

import java.io.File;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.oztrack.data.model.types.DataFileStatus;
import org.springframework.web.multipart.MultipartFile;

@Entity(name = "datafile")
public class DataFile extends OztrackBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "datafileid_seq")
    @SequenceGenerator(name = "datafileid_seq", sequenceName = "datafileid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;
    
    private String dataFilePath;
    private String userGivenFileName;
    @Column(columnDefinition = "TEXT")
    private String fileDescription;
    private String contentType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    private Boolean localTimeConversionRequired;
    private Long localTimeConversionHours;
    private Boolean singleAnimalInFile;

    // metadata for convenience
    private Integer detectionCount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstDetectionDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDetectionDate;

    @Enumerated(STRING)
    @Column(name="datafilestatus")
    private DataFileStatus status;
    @Column(columnDefinition = "TEXT")
    private String statusMessage;

    @ManyToOne(fetch = FetchType.LAZY, cascade={}) //persist project yourself
    private Project project;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PositionFix> positionFixes = new LinkedList<PositionFix>();

    @Transient
    private MultipartFile  file;
    public void setFile(MultipartFile file) {this.file = file;}
    public MultipartFile getFile() {return file;}

    
    public DataFile() {
    }
    
    public DataFile(String fileName) {
    	this.userGivenFileName = fileName;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }
    
    public String getAbsoluteDataFilePath() {
        return project.getAbsoluteDataDirectoryPath() + File.separator + getDataFilePath();
    }
        
    public String getUserGivenFileName() {
        return userGivenFileName;
    }

    public void setUserGivenFileName(String userGivenFileName) {
        this.userGivenFileName = userGivenFileName;
    }
    
    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public DataFileStatus getStatus() {
        return status;
    }

    public void setStatus(DataFileStatus status) {
        this.status = status;
    }

    public Boolean getLocalTimeConversionRequired() {
        return localTimeConversionRequired;
    }

    public void setLocalTimeConversionRequired(Boolean localTimeConversionRequired) {
        this.localTimeConversionRequired = localTimeConversionRequired;
    }

    public Long getLocalTimeConversionHours() {
        return localTimeConversionHours;
    }

    public void setLocalTimeConversionHours(Long localTimeConversionHours) {
        this.localTimeConversionHours = localTimeConversionHours;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Boolean getSingleAnimalInFile() {
         return singleAnimalInFile;
     }

     public void setSingleAnimalInFile(Boolean singleAnimalInFile) {
         this.singleAnimalInFile = singleAnimalInFile;
     }
	public Integer getDetectionCount() {
		return detectionCount;
	}
	public void setDetectionCount(Integer detectionCount) {
		this.detectionCount = detectionCount;
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
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DataFile)) {
            return false;
        }
        DataFile other = (DataFile) obj;
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

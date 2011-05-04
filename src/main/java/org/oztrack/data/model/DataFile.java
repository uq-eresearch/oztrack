package org.oztrack.data.model;


import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.DataFileType;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.EnumType.STRING;

/**
 * Author: peggy
 * Date: 05/04/2011
 */

@Entity(name = "datafile")
public class DataFile implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "datafileid_seq")
    @SequenceGenerator(name = "datafileid_seq", sequenceName = "datafileid_seq",allocationSize = 1)
    @Column(nullable=false)
    private Long id;
    
    private String ozTrackFileName;
    private String userGivenFileName;
    @Column(columnDefinition = "TEXT")
    private String fileDescription;
    private String contentType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    private String uploadUser;
    @Temporal(TemporalType.TIMESTAMP)
    private Date minDetectionDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date maxDetectionDate;
    private Boolean hasLocalTimeConversion;
    private Long localTimeConversionHours;
    private Integer numberRawDetections;


    @Enumerated(STRING)
    @Column(name="datafiletype")
    private DataFileType dataFileType;

    @Enumerated(STRING)
    @Column(name="datafilestatus")
    private DataFileStatus status;
    @Column(columnDefinition = "TEXT")
    private String statusMessage;

    @ManyToOne
    private Project project;

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
    
    public String getOzTrackFileName() {
        return ozTrackFileName;
    }

    public void setOzTrackFileName(String ozTrackFileName) {
        this.ozTrackFileName = ozTrackFileName;
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

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

       public Date getMinDetectionDate() {
        return minDetectionDate;
    }

    public void setMinDetectionDate(Date minDetectionDate) {
        this.minDetectionDate = minDetectionDate;
    }

    public Date getMaxDetectionDate() {
        return maxDetectionDate;
    }

    public void setMaxDetectionDate(Date maxDetectionDate) {
        this.maxDetectionDate = maxDetectionDate;
    }

     public DataFileStatus getStatus() {
        return status;
    }

    public void setStatus(DataFileStatus status) {
        this.status = status;
    }

    public DataFileType getDataFileType() {
        return dataFileType;
    }

    public void setDataFileType(DataFileType dataFileType) {
        this.dataFileType = dataFileType;
    }

        public Boolean getHasLocalTimeConversion() {
        return hasLocalTimeConversion;
    }

    public void setHasLocalTimeConversion(Boolean hasLocalTimeConversion) {
        this.hasLocalTimeConversion = hasLocalTimeConversion;
    }

    public Long getLocalTimeConversionHours() {
        return localTimeConversionHours;
    }

    public void setLocalTimeConversionHours(Long localTimeConversionHours) {
        this.localTimeConversionHours = localTimeConversionHours;
    }

    public Integer getNumberRawDetections() {
        return numberRawDetections;
    }

    public void setNumberRawDetections(Integer numberRawDetections) {
        this.numberRawDetections = numberRawDetections;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }



}

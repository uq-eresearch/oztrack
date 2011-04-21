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
    private String fileDescription;
    private String contentType;
    private Date uploadDate;
    private String uploadUser;
    private Date minDetectionDate;
    private Date maxDetectionDate;
    private boolean convertedToAEST;

    @Enumerated(STRING)
    @Column(name="datafiletype")
    private DataFileType dataFileType;


    @Enumerated(STRING)
    @Column(name="datafilestatus")
    private DataFileStatus status;

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

    public boolean isConvertedToAEST() {
        return convertedToAEST;
    }

    public void setConvertedToAEST(boolean convertedToAEST) {
        this.convertedToAEST = convertedToAEST;
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


}

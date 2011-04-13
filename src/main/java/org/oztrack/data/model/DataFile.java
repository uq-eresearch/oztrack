package org.oztrack.data.model;


import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;

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
    
    private String ozTrackZipFileName;
    private String userGivenFileName;
    private String fileDescription;
    private String fileType;
    
    @ManyToOne
    private Project project;

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
    
    public String getOzTrackZipFileName() {
        return ozTrackZipFileName;
    }

    public void setOzTrackZipFileName(String ozTrackZipFileName) {
        this.ozTrackZipFileName = ozTrackZipFileName;
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
    
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}

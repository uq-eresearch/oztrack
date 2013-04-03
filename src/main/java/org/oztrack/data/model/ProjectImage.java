package org.oztrack.data.model;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity(name="projectimage")
public class ProjectImage extends OzTrackBaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="projectimage_id_seq")
    @SequenceGenerator(name="projectimage_id_seq", sequenceName="projectimage_id_seq",allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @Column(name="datadirectorypath", columnDefinition="text", nullable=true)
    private String dataDirectoryPath;

    @Column(name="thumbnailpath", columnDefinition="text", nullable=true)
    private String thumbnailPath;

    @Column(name="thumbnailmimetype", columnDefinition="text", nullable=true)
    private String thumbnailMimeType;

    @Column(name="filepath", columnDefinition="text", nullable=true)
    private String filePath;

    @Column(name="filemimetype", columnDefinition="text", nullable=true)
    private String fileMimeType;

    @Column(name="originalfilename", columnDefinition="text", nullable=true)
    private String originalFileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDataDirectoryPath() {
        return dataDirectoryPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        this.dataDirectoryPath = dataDirectoryPath;
    }

    public String getAbsoluteDataDirectoryPath() {
        return project.getAbsoluteDataDirectoryPath() + File.separator + getDataDirectoryPath();
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getAbsoluteThumbnailPath() {
        return getAbsoluteDataDirectoryPath() + File.separator + getThumbnailPath();
    }

    public String getThumbnailMimeType() {
        return thumbnailMimeType;
    }

    public void setThumbnailMimeType(String thumbnailMimeType) {
        this.thumbnailMimeType = thumbnailMimeType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAbsoluteFilePath() {
        return getAbsoluteDataDirectoryPath() + File.separator + getFilePath();
    }

    public String getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ProjectImage)) {
            return false;
        }
        ProjectImage other = (ProjectImage) obj;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        else {
            return super.hashCode();
        }
    }
}

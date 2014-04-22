package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.oztrack.data.model.types.ProjectVisitType;

@Entity
@Table(name="projectvisit")
public class ProjectVisit {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="projectvisit_id_seq")
    @SequenceGenerator(name="projectvisit_id_seq", sequenceName="projectvisit_id_seq",allocationSize=1)
    @Column(nullable=false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;
    
    @Enumerated(STRING)
    @Column(name="visittype", columnDefinition="text", nullable=false)
    private ProjectVisitType visitType;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="visitdate", nullable=false)
    private Date visitDate;
    
    public ProjectVisit(Project project, ProjectVisitType visitType, Date visitDate) {
        this.project = project;
        this.visitType = visitType;
        this.visitDate = visitDate;
    }

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

    public ProjectVisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(ProjectVisitType visitType) {
        this.visitType = visitType;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }
}

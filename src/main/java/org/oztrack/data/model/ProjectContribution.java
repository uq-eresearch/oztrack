package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="project_contribution", uniqueConstraints=@UniqueConstraint(columnNames={"project_id", "contributor_id"}))
public class ProjectContribution {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="project_contribution_id_seq")
    @SequenceGenerator(name="project_contribution_id_seq", sequenceName="project_contribution_id_seq",allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="contributor_id", nullable=false)
    private Person contributor;

    @Column(name="ordinal", nullable=false)
    private Integer ordinal;

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

    public Person getContributor() {
        return contributor;
    }

    public void setContributor(Person contributor) {
        this.contributor = contributor;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }
}

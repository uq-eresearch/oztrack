package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

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
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.oztrack.data.model.types.Role;

@Entity
@Table(name="project_user", uniqueConstraints=@UniqueConstraint(columnNames={"project_id", "user_id"}))
public class ProjectUser {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="project_user_id_seq")
    @SequenceGenerator(name="project_user_id_seq", sequenceName="project_user_id_seq",allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="project_id", nullable=false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(STRING)
    @Column(name="role", columnDefinition="text", nullable=false)
    private Role role;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProjectUser that = (ProjectUser) obj;
        return new EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(this.project, that.project)
            .append(this.user, that.user)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 31)
            .append(project)
            .append(user)
            .toHashCode();
    }
}
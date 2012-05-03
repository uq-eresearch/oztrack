package org.oztrack.data.model;

import static javax.persistence.EnumType.STRING;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.oztrack.data.model.types.Role;

@Entity
@Table(name = "project_user")
@AssociationOverrides({
 @AssociationOverride(name = "pk.appuser", joinColumns = @JoinColumn(name = "user_id")),
 @AssociationOverride(name = "pk.project", joinColumns = @JoinColumn(name = "project_id"))
        })
public class ProjectUser implements Serializable {

	private ProjectUserPk pk = new ProjectUserPk();
	
    @Enumerated(STRING)
    @Column(name="role")
    private Role role;
	
	@EmbeddedId
    public ProjectUserPk getPk() {
        return pk;
    }

    public void setPk(ProjectUserPk pk) {
        this.pk = pk;
    }


	@Transient
    public User getUser() {
        return getPk().getUser();
    }

    public void setUser(User user) {
        getPk().setUser(user);
    }

    @Transient
    public Project getProject() {
        return getPk().getProject();
    }

    public void setProject(Project project) {
        getPk().setProject(project);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectUser that = (ProjectUser) o;

        if (getPk() != null ? !getPk().equals(that.getPk()) : that.getPk() != null) return false;

        return true;
    }

    public int hashCode() {
        return (getPk() != null ? getPk().hashCode() : 0);
    }
    
    public Role getRole() {
    	return role;
    }

    public void setRole(Role role) {
    	this.role = role;
    }
 


}

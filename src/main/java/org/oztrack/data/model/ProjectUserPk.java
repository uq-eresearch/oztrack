package org.oztrack.data.model;

import java.io.Serializable;
import javax.persistence.*;

@Embeddable
public class ProjectUserPk implements Serializable {

    private User user;
    private Project project;

    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectUserPk that = (ProjectUserPk) o;

        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (project != null ? !project.equals(that.project) : that.project!= null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (user != null ? user.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        return result;
    }
}

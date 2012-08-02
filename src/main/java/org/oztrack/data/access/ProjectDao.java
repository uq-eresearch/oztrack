package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.springframework.stereotype.Service;

@Service
public interface ProjectDao {
    List<Project> getAll();
    Project getProjectById(Long id);
    void save(Project object);
    Project update(Project object);
    void delete(Project project);
    void create(Project project, User currentUser) throws Exception;
    List<Project> getProjectsByPublished(boolean published);
    List<ProjectUser> getProjectUsersWithRole(Project project, Role role);
}
package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface ProjectDao {
    List<Project> getAll();
    Project getProjectById(Long id);
    void save(Project object);
    Project update(Project object);
    void delete(Project project);
    void create(Project project, User currentUser) throws Exception;
    void saveProjectImageFile(Project project) throws Exception;
    List<Project> getProjectsByPublished(boolean published);
}
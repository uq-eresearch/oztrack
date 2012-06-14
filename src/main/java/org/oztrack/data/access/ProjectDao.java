package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Project;
import org.springframework.stereotype.Service;

@Service
public interface ProjectDao {
    List<Project> getAll();
    Project getProjectById(Long id);
    void save(Project object);
    Project update(Project object);
}
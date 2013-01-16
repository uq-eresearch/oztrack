package org.oztrack.data.access;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.ProjectAccess;
import org.oztrack.data.model.types.Role;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Polygon;

@Service
public interface ProjectDao {
    List<Project> getAll();
    Project getProjectById(Long id);
    void save(Project object);
    Project update(Project object);
    void delete(Project project);
    void create(Project project, User currentUser) throws Exception;
    Range<Date> getDetectionDateRange(Project project, boolean includeDeleted);
    int getDetectionCount(Project project, boolean includeDeleted);
    Polygon getBoundingBox(Project project);
    HashMap<Long, Polygon> getBoundingBoxes(Project project, List<Animal> animals);
    List<Project> getProjectsByAccess(ProjectAccess access);
    List<ProjectUser> getProjectUsersWithRole(Project project, Role role);
    List<Project> getProjectsWithExpiredEmbargo(Date expiryDate);
}
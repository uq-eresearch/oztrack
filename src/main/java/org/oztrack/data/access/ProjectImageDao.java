package org.oztrack.data.access;

import java.io.IOException;

import org.oztrack.data.model.ProjectImage;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface ProjectImageDao {
    ProjectImage getProjectImageById(Long id);
    void create(ProjectImage projectImage, User currentUser) throws Exception;
    void save(ProjectImage projectImage);
    ProjectImage update(ProjectImage projectImage);
    void delete(ProjectImage projectImage) throws IOException;
}
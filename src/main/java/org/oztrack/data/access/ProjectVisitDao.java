package org.oztrack.data.access;

import java.util.EnumMap;

import org.oztrack.data.model.ProjectVisit;
import org.oztrack.data.model.types.ProjectVisitSummary;
import org.oztrack.data.model.types.ProjectVisitType;
import org.springframework.stereotype.Service;

@Service
public interface ProjectVisitDao {
    void save(ProjectVisit visit);
    EnumMap<ProjectVisitType, ProjectVisitSummary> getVisitSummaries();
}

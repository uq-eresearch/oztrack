package org.oztrack.data.access;

import java.util.EnumMap;

import org.oztrack.data.model.ProjectVisit;
import org.oztrack.data.model.types.ProjectVisitType;

public interface ProjectVisitDao {
    void save(ProjectVisit visit);
    EnumMap<ProjectVisitType, Long> getVisitCounts();
}

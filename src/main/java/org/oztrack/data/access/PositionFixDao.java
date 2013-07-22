package org.oztrack.data.access;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Range;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.ArgosClass;
import org.oztrack.data.model.types.PositionFixStats;
import org.oztrack.data.model.types.TrajectoryStats;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.MultiPolygon;

@Service
public interface PositionFixDao {
    void save(PositionFix positionFix);
    PositionFix update(PositionFix positionFix);
    int getNumPositionFixes();
    Page<PositionFix> getPage(SearchQuery searchQuery, int offset, int nbrObjectsPerPage);
    List<PositionFix> getProjectPositionFixList(SearchQuery searchQuery);
    int setDeleted(
        Project project,
        Date fromDate,
        Date toDate,
        List<Long> animalIds,
        MultiPolygon multiPolygon,
        Set<PositionFix> speedFilterPositionFixes,
        ArgosClass minArgosClass,
        Double maxDop,
        boolean deleted
    );
    void applyKalmanFilter(Analysis analysis);
    void renumberPositionFixes(Project project, List<Long> animalIds);
    Map<Long, PositionFixStats> getAnimalPositionFixStats(Project project, Date fromDate, Date toDate);
    Map<Long, TrajectoryStats> getAnimalTrajectoryStats(Project project, Date fromDate, Date toDate);
    Map<Long, Range<Date>> getAnimalStartEndDates(Project project, Date fromDate, Date toDate);
}
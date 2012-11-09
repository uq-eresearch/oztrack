package org.oztrack.data.access;

import java.util.Date;
import java.util.List;

import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.MultiPolygon;

@Service
public interface PositionFixDao {
    int getNumPositionFixes();
    Page<PositionFix> getPage(SearchQuery searchQuery, int offset, int nbrObjectsPerPage);
    List<PositionFix> getProjectPositionFixList(SearchQuery searchQuery);
    int setDeletedOnOverlappingPositionFixes(Project project, Date fromDate, Date toDate, List<Long> animalIds, MultiPolygon multiPolygon, boolean deleted);
    void renumberPositionFixes(Project project);
}
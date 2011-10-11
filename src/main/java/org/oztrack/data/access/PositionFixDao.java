package org.oztrack.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import au.edu.uq.itee.maenad.dataaccess.Page;

import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;

import com.vividsolutions.jts.geom.Polygon;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 11/08/11
 * Time: 1:10 PM
 */
public interface PositionFixDao extends Dao<PositionFix> {

       Page<PositionFix> getPage(SearchQuery searchQuery, int offset, int nbrObjectsPerPage);

       List<PositionFix> getProjectPositionFixList(SearchQuery searchQuery);
       
//       Polygon getProjectBoundingBox(Project project);
       Date getProjectFirstDetectionDate(Project project); //not used
       Date getProjectLastDetectionDate(Project project); // not used

       Date getDataFileFirstDetectionDate(DataFile dataFile);
       Date getDataFileLastDetectionDate(DataFile dataFile);
       		
}

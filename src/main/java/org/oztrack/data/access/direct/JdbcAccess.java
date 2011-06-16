package org.oztrack.data.access.direct;

import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.SearchQuery;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 9/05/11
 * Time: 12:12 PM
 */
public interface JdbcAccess {

    //public void testJDBCAccess();

    public int loadObservations(DataFile dataFile);

    //public int loadAcousticDetections(Long projectId, Long dataFileId);
    //public void truncateRawAcousticDetections();
    public void truncateRawObservations(DataFile dataFile);
    public List<AcousticDetection> queryAcousticDetections(String sql);
    public List<AcousticDetection> queryAcousticDetections2(SearchQuery searchQuery);

}
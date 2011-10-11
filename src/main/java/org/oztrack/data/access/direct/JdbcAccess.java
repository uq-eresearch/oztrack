package org.oztrack.data.access.direct;

import org.oztrack.data.model.*;

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
    //public int setProjectBoundingBox(Project project);
     public int updateProjectMetadata(Project project);


}
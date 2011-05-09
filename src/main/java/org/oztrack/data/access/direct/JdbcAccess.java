package org.oztrack.data.access.direct;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 9/05/11
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public interface JdbcAccess {

    public void testJDBCAccess();

    public int loadAcousticDetections(Long projectId, Long dataFileId);
    public void truncateRawAcousticDetections();

    }
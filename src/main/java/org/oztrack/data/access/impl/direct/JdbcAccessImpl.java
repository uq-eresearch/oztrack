package org.oztrack.data.access.impl.direct;

import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.SearchQuery;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 9/05/11
 * Time: 12:03 PM
 */
public class JdbcAccessImpl extends JdbcDaoSupport implements JdbcAccess {

    public void testJDBCAccess() {

        String test1 = "test1";
        String test2 = "test2";

        String sql = "INSERT INTO test (test1, test2)" +
                     "VALUES (?,?)";

        getJdbcTemplate().update(sql, new Object [] {test1, test2});
    }

    public int loadAcousticDetections(Long projectId, Long dataFileId) {

        String sql =
                "INSERT INTO acousticdetection (" +
                " id" +
                " ,detectiontime" +
                " ,sensor1value" +
                " ,sensor1units" +
                " ,sensor2value" +
                " ,sensor2units" +
                " ,animal_id" +
                " ,datafile_id" +
                " ,receiverdeployment_id)" +
                " SELECT rad.id" +
                " ,rad.datetime" +
                " ,rad.sensor1" +
                " ,rad.units1" +
                " ,rad.sensor2" +
                " ,rad.units2" +
                " ,ani.id" +
                " ,?" +
                " ,rdp.id" +
                " FROM rawacousticdetection rad" +
                " ,animal ani" +
                " ,receiverdeployment rdp" +
                " WHERE rad.animalid = ani.projectanimalid  "  +
                " AND  ani.project_id = ?" +
                " AND rad.receiversn=rdp.originalid" +
                " AND rdp.project_id = ?"
                ;

        return getJdbcTemplate().update(sql, new Object [] { dataFileId, projectId, projectId} );

    }

    public void truncateRawAcousticDetections() {

        getJdbcTemplate().execute("TRUNCATE TABLE rawacousticdetection");

    }

    public List<AcousticDetection> queryAcousticDetections(String sql) {

        List<AcousticDetection> acousticDetections = getJdbcTemplate().query(sql, new BeanPropertyRowMapper(AcousticDetection.class) );
        return acousticDetections;

    }

    public List<AcousticDetection> queryAcousticDetections2(SearchQuery searchQuery) {

        String sql = searchQuery.buildQuery();
        AcousticDetectionRowMapper acousticDetectionRowMapper = new AcousticDetectionRowMapper();
        List<AcousticDetection> acousticDetections = getJdbcTemplate().query(sql, acousticDetectionRowMapper);

        return acousticDetections;

    }


}

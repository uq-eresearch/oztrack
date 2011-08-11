package org.oztrack.data.access.impl.direct;

import org.oztrack.data.access.direct.JdbcQuery;
import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 19/07/11
 * Time: 9:30 AM
 */
public class JdbcQueryImpl extends JdbcDaoSupport implements JdbcQuery {

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

    public List<PositionFix> queryProjectPositionFixes(SearchQuery searchQuery) {

        Long projectId = searchQuery.getProject().getId();

        String sql = "SELECT o.animal_id " +
                     ",o.detectiontime " +
                     ",o.latitude " +
                     ",o.longitude " +
                     "from PositionFix o " +
                     ", datafile d " +
                     "where o.datafile_id=d.id " +
                     "and d.project_id = :projectId " +
                     "limit 20";

        SqlParameterSource namedParameters = new MapSqlParameterSource("projectId", Long.valueOf(projectId));

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
        PositionFixRowMapper positionFixRowMapper = new PositionFixRowMapper();
        return namedParameterJdbcTemplate.query(sql, namedParameters, positionFixRowMapper );
    }

}

package org.oztrack.data.access.impl.direct;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.direct.JdbcQuery;
import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 19/07/11
 * Time: 9:30 AM
 */
public class JdbcQueryImpl extends JdbcDaoSupport implements JdbcQuery {

     /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    public List<AcousticDetection> queryAcousticDetections(String sql) {

        @SuppressWarnings("unchecked")
        List<AcousticDetection> acousticDetections = getJdbcTemplate().query(sql, new BeanPropertyRowMapper(AcousticDetection.class) );
        return acousticDetections;

    }

    public List<AcousticDetection> queryAcousticDetections2(SearchQuery searchQuery) {

        //TODO: Fix this to use a named parameter map

        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        String select = "SELECT ad.id as acousticdetectionid "
                      + ", ad.detectionTime "
                      + ", ad.animal_id "
                      + ", ad.receiverdeployment_id "
                      + ", ad.datafile_id "
                      + ", ad.sensor1value "
                      + ", ad.sensor1units "
                      + ", a.id as animalid "
                      + ", a.projectanimalid "
                      + ", d.uploaddate as datafile_uploaddate"
                      + ", rd.originalid as receiverdeployment_originalid";

        String from = " FROM acousticdetection ad"
                    + ", animal a "
                    + ", datafile d "
                    + ", receiverdeployment rd ";

        String joinClause = " WHERE ad.animal_id=a.id"
                     + " AND ad.receiverdeployment_id=rd.id "
                     + " AND ad.datafile_id = d.id ";

        String where = "";
        String orderBy = "";

        /*if (searchQuery.getProjectAnimalId.length() != 0) {
            where = where + " AND a.projectanimalid = '"
                          + this.projectAnimalId + "'";
        } */

        if (searchQuery.getToDate() != null) {
            where = where + " AND ad.detectiontime <= to_date('"
                          + sdf.format(searchQuery.getToDate()) + "','" + dateFormat + "')";
        }

        if (searchQuery.getFromDate() != null) {
            where = where + " AND ad.detectiontime >= to_date('"
                          + sdf.format(searchQuery.getFromDate()) + "','" + dateFormat + "')";
        }

        if (searchQuery.getReceiverOriginalId().length() != 0) { //(this.receiverOriginalId.length() != 0)  {
            where = where + " AND rd.originalid = '"
                          + searchQuery.getReceiverOriginalId() + "'";
        }

        if (searchQuery.getSortField().length() != 0) {
            String fieldName = "";
            if (searchQuery.getSortField().equals("Animal")) {
                   fieldName = "a.projectanimalid";
            } else if (searchQuery.getSortField().equals("Receiver")) {
                   fieldName = "rd.originalid";
            } else if (searchQuery.getSortField().equals("Detection Time")) {
                   fieldName = "ad.detectiontime";
            }
            orderBy = orderBy + " ORDER BY " + fieldName;
        }

        String sql = select + from + joinClause + where + orderBy;

        logger.debug(sql);

        AcousticDetectionRowMapper acousticDetectionRowMapper = new AcousticDetectionRowMapper();
        @SuppressWarnings("unchecked")
        List<AcousticDetection> acousticDetections = getJdbcTemplate().query(sql, acousticDetectionRowMapper);

        return acousticDetections;

    }

    public List<PositionFix> queryProjectPositionFixes(SearchQuery searchQuery) {

        Long projectId = searchQuery.getProject().getId();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectId", projectId);

        String sql = "SELECT o.animal_id "
                   +  ",o.detectiontime "
                   +  ",o.latitude "
                   +  ",o.longitude "
                   +  "from PositionFix o "
                   +  ", datafile d "
                   +  "where o.datafile_id=d.id "
                   +  "and d.project_id = :projectId " ;
                     //"limit 20";

        if (searchQuery.getFromDate() != null) {
            sql = sql + "and o.detectionTime >= :fromDate ";
            mapSqlParameterSource.addValue("fromDate", searchQuery.getFromDate());
        }
        if (searchQuery.getToDate() != null) {
            sql = sql + "and o.detectionTime <= :toDate ";
            mapSqlParameterSource.addValue("toDate", searchQuery.getToDate());
        }
        if (searchQuery.getAnimalList() != null) {
            String animalClause = "and o.animal_id in (";
            for (int i=0; i < searchQuery.getAnimalList().size(); i++) {
                String thisAnimal = "animalId" + i;
                animalClause = animalClause + ":" + thisAnimal + ",";
                mapSqlParameterSource.addValue(thisAnimal,searchQuery.getAnimalList().get(i).getId());
            }
            animalClause = animalClause.substring(0,animalClause.length()-1) + ")";
            sql = sql + animalClause;
        }

        String orderBy = " order by o.detectionTime ";
        sql = sql + orderBy;

        logger.debug("Position fix jdbc query: " + sql);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
        PositionFixRowMapper positionFixRowMapper = new PositionFixRowMapper();
        @SuppressWarnings("unchecked")
        List<PositionFix> result = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, positionFixRowMapper );
        return result;
    }

}

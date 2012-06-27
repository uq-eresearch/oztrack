package org.oztrack.data.access.impl;

import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class JdbcAccessImpl extends JdbcDaoSupport implements JdbcAccess {
    public int loadObservations(DataFile dataFile) {
        String sql = "";
        long dataFileId = dataFile.getId();
        long projectId = dataFile.getProject().getId();
        int nbrObservations = 0;

        switch (dataFile.getProject().getProjectType()) {
            case GPS:
                sql =   "INSERT INTO positionfix (" +
                        " id" +
                        " ,detectiontime" +
                        " ,latitude" +
                        " ,longitude" +
                        " ,animal_id" +
                        " ,datafile_id" +
                        " ,locationgeometry)" +
                        " SELECT rpf.id" +
                        " ,rpf.detectiontime" +
                        " ,rpf.latitude" +
                        " ,rpf.longitude" +
                        " ,ani.id" +
                        " ,?" +
                        " ,rpf.locationgeometry " +
                        " FROM rawpositionfix rpf" +
                        " ,animal ani" ;

                String where = " WHERE rpf.animalid = ani.projectanimalid  "  +
                        " AND  ani.project_id = ?" ;

                if (dataFile.getSingleAnimalInFile()) {
                    where = " WHERE ani.id = (select max(a.id) from animal a where a.project_id = ?)";
                }
                        logger.debug(sql);
                        nbrObservations = getJdbcTemplate().update(sql+where, new Object [] { dataFileId, projectId} );
                break;
        }
        return nbrObservations;
    }
	
    private String getTableName(Project project) {
    	
    	String tableName = "foo";
    	
        switch (project.getProjectType()) {
        case GPS:
            tableName = "positionfix";

            break;
    }
    	return tableName;
    }

    public void truncateRawObservations(DataFile dataFile) {
        String tableName = "raw" + this.getTableName(dataFile.getProject());
        getJdbcTemplate().execute("TRUNCATE TABLE " + tableName);
    }

    public int updateProjectMetadata(Project project) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("projectId", project.getId());
        String tableName = this.getTableName(project);
 
        String sql = "update project "
                   + " set boundingBox = "
                     + "(select (ST_Envelope(ST_Collect(locationgeometry))) "
                     + " from " + tableName + " t "
                     + ", dataFile d "
                     + " where t.datafile_id=d.id"
                     + " and d.project_id = :projectId) "
                     + ", firstdetectiondate = "
                     + " (select min(t.detectionTime) "
                     + "  from " + tableName + " t "
                     + "  ,dataFile d "
                     + "  where t.datafile_id=d.id"
                     + "  and d.project_id = :projectId) "
                     + ", lastdetectiondate = "
                     + " (select max(t.detectionTime) "
                     + "  from " + tableName + " t "
                     + "  ,dataFile d "
                     + "  where t.datafile_id=d.id"
                     + "  and d.project_id = :projectId) "
                     + ", detectionCount = "
                     + " (select count(t.id) "
                     + "  from " + tableName + " t "
                     + "  ,dataFile d "
                     + "  where t.datafile_id=d.id"
                     + "  and d.project_id = :projectId) "
                     + "where id = :projectId";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
        return namedParameterJdbcTemplate.update(sql,mapSqlParameterSource);
    }
}
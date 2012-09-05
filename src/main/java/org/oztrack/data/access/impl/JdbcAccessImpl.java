package org.oztrack.data.access.impl;

import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class JdbcAccessImpl extends JdbcDaoSupport implements JdbcAccess {
    @Override
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

    @Override
    public void truncateRawObservations(DataFile dataFile) {
        String tableName = "raw" + this.getTableName(dataFile.getProject());
        getJdbcTemplate().execute("TRUNCATE TABLE " + tableName);
    }
}
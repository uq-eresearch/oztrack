package org.oztrack.data.access.impl;

import org.oztrack.data.access.JdbcAccess;
import org.oztrack.data.model.DataFile;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class JdbcAccessImpl extends JdbcDaoSupport implements JdbcAccess {
    @Override
    public int loadObservations(DataFile dataFile) {
        long dataFileId = dataFile.getId();
        long projectId = dataFile.getProject().getId();

        String sql =
                "INSERT INTO positionfix (" +
                " id" +
                " ,detectiontime" +
                " ,latitude" +
                " ,longitude" +
                " ,animal_id" +
                " ,datafile_id" +
                " ,locationgeometry" +
                " ,deleted" +
                " ,argosclass" +
                " ,dop)" +
                " SELECT rpf.id" +
                " ,rpf.detectiontime" +
                " ,rpf.latitude" +
                " ,rpf.longitude" +
                " ,ani.id" +
                " ,?" +
                " ,rpf.locationgeometry" +
                " ,rpf.deleted" +
                " ,rpf.argosclass" +
                " ,rpf.dop" +
                " FROM rawpositionfix rpf" +
                " ,animal ani" ;
        if (dataFile.getSingleAnimalInFile()) {
            sql += " WHERE ani.id = (select max(a.id) from animal a where a.project_id = ?)";
        }
        else {
            sql += " WHERE rpf.animalid = ani.projectanimalid AND  ani.project_id = ?" ;
        }
        logger.debug(sql);

        int nbrObservations = getJdbcTemplate().update(sql, new Object [] {dataFileId, projectId});
        return nbrObservations;
    }

    @Override
    public void truncateRawObservations(DataFile dataFile) {
        getJdbcTemplate().execute("TRUNCATE TABLE rawpositionfix");
    }
}
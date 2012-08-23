package org.oztrack.data.access.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.springframework.jdbc.core.RowMapper;

public class PositionFixRowMapper implements RowMapper<PositionFix> {
    @Override
    public PositionFix mapRow(ResultSet resultSet, int i) throws SQLException {
        PositionFix positionFix = new PositionFix();

        Animal animal = new Animal();
        animal.setId(resultSet.getLong("animal_id"));
        positionFix.setAnimal(animal);
        positionFix.setDetectionTime(resultSet.getTimestamp("detectionTime"));
        positionFix.setLatitude(resultSet.getString("latitude"));
        positionFix.setLongitude(resultSet.getString("longitude"));
        return positionFix;
    }
}

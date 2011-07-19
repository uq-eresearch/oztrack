package org.oztrack.data.access.impl.direct;

import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 19/07/11
 * Time: 12:22 PM
 */
public class PositionFixRowMapper implements RowMapper {


@Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {

    PositionFix positionFix = new PositionFix();
    Animal animal = new Animal();
    animal.setId(resultSet.getLong("animal_id"));
    positionFix.setAnimal(animal);
    positionFix.setDetectionTime(resultSet.getDate("detectionTime"));
    positionFix.setLatitude(resultSet.getString("latitude"));
    positionFix.setLongitude(resultSet.getString("longitude"));
    return positionFix;

    }

}

package org.oztrack.data.access.impl.direct;

import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.ReceiverDeployment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 25/05/11
 * Time: 12:30 PM
 */
public class AcousticDetectionRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {

        Animal animal = new Animal();
        animal.setId(resultSet.getLong("animalId"));
        animal.setProjectAnimalId(resultSet.getString("projectanimalid"));

        DataFile dataFile = new DataFile();
        dataFile.setId(resultSet.getLong("datafile_id"));
        dataFile.setUploadDate(resultSet.getDate("datafile_uploaddate"));

        ReceiverDeployment receiverDeployment = new ReceiverDeployment();
        receiverDeployment.setOriginalId(resultSet.getString("receiverdeployment_originalid"));

        AcousticDetection acousticDetection = new AcousticDetection();
        acousticDetection.setId(resultSet.getLong("acousticdetectionid"));
        acousticDetection.setDetectionTime(resultSet.getDate("detectionTime"));
        acousticDetection.setSensor1Value(resultSet.getDouble("sensor1value"));
        acousticDetection.setSensor1Units(resultSet.getString("sensor1units"));

        acousticDetection.setAnimal(animal);
        acousticDetection.setDataFile(dataFile);
        acousticDetection.setReceiverDeployment(receiverDeployment);

        return acousticDetection;
    }
}

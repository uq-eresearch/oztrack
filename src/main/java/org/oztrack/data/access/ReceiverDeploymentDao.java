package org.oztrack.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.ReceiverDeployment;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReceiverDeploymentDao extends Dao<ReceiverDeployment> {
     List<ReceiverDeployment> getReceiversByProjectId(Long projectId);
}

package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.ReceiverDeployment;

import au.edu.uq.itee.maenad.dataaccess.Dao;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReceiverDeploymentDao extends Dao<ReceiverDeployment> {
     List<ReceiverDeployment> getReceiversByProjectId(Long projectId);
     ReceiverDeployment getReceiverDeployment(String originalId, Long projectId);
}

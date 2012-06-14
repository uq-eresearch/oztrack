package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.ReceiverDeployment;
import org.springframework.stereotype.Service;

@Service
public interface ReceiverDeploymentDao {
    List<ReceiverDeployment> getReceiversByProjectId(Long projectId);
    ReceiverDeployment getReceiverDeployment(String originalId, Long projectId);
    void save(ReceiverDeployment object);
    ReceiverDeployment update(ReceiverDeployment object);
}

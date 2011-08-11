package org.oztrack.data.access.manager;

import org.oztrack.data.access.*;
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.access.direct.JdbcQuery;


import javax.persistence.EntityManager;


/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:21 AM
 */
public interface DaoManager {
    AcousticDetectionDao getAcousticDetectionDao();
    PositionFixDao getPositionFixDao();
    AnimalDao getAnimalDao();
    UserDao getUserDao();
    ProjectDao getProjectDao();
    DataFileDao getDataFileDao();
    RawAcousticDetectionDao getRawAcousticDetectionDao();
    ReceiverDeploymentDao getReceiverDeploymentDao();
    EntityManager getEntityManager();
    JdbcAccess getJdbcAccess();
    JdbcQuery getJdbcQuery();
    SightingDao getSightingDao();


}

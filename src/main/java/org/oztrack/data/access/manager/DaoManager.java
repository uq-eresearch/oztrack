package org.oztrack.data.access.manager;

import javax.persistence.EntityManager;

import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.access.SightingDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.access.direct.JdbcQuery;

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
    SettingsDao getSettingsDao();
}

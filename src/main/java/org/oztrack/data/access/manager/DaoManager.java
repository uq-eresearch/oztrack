package org.oztrack.data.access.manager;

import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.AnimalDao;


import javax.persistence.EntityManager;


/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:21 AM
 */
public interface DaoManager {
    AnimalDao getAnimalDao();
    UserDao getUserDao();
    ProjectDao getProjectDao();
    DataFileDao getDataFileDao();
    RawAcousticDetectionDao getRawAcousticDetectionDao();
    EntityManager getEntityManager();
}

package org.oztrack.data.access.manager;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.DataFileDao;


/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:21 AM
 */
public interface DaoManager {
    UserDao getUserDao();
    ProjectDao getProjectDao();
    DataFileDao getDataFileDao();
}

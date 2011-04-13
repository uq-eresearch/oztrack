package org.oztrack.data.access.manager.impl;

import org.oztrack.data.access.impl.ProjectDaoImpl;
import org.oztrack.data.access.impl.UserDaoImpl;
import org.oztrack.data.access.impl.DataFileDaoImpl;
import org.oztrack.data.access.manager.DaoManager;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:20 AM
 */
public class DaoManagerImpl implements DaoManager {

    private UserDaoImpl userDao;
    private ProjectDaoImpl projectDao;
    private DataFileDaoImpl dataFileDao;
    
    public void setUserDao(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public UserDaoImpl getUserDao() {
        return userDao;
    }
    
    public void setProjectDao(ProjectDaoImpl projectDao) {
        this.projectDao = projectDao;
    }

    public ProjectDaoImpl getProjectDao() {
        return projectDao;
    }

    public void setDataFileDao(DataFileDaoImpl dataFileDao) {
        this.dataFileDao = dataFileDao;
    }

    public DataFileDaoImpl getDataFileDao() {
        return dataFileDao;
    }

}

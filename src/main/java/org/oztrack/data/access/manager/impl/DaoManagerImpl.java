package org.oztrack.data.access.manager.impl;

import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.access.impl.ProjectDaoImpl;
import org.oztrack.data.access.impl.RawAcousticDetectionDaoImpl;
import org.oztrack.data.access.impl.UserDaoImpl;
import org.oztrack.data.access.impl.DataFileDaoImpl;
import org.oztrack.data.access.manager.DaoManager;
import org.oztrack.data.connector.JpaConnector;

import javax.persistence.EntityManager;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:20 AM
 */
public class DaoManagerImpl implements DaoManager {

    private UserDaoImpl userDao;
    private ProjectDaoImpl projectDao;
    private DataFileDaoImpl dataFileDao;
    private RawAcousticDetectionDaoImpl rawAcousticDetectionDao;
    private JpaConnector jpaConnector;

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

    public RawAcousticDetectionDaoImpl getRawAcousticDetectionDao() {
        return rawAcousticDetectionDao;
    }

    public void setRawAcousticDetectionDao(RawAcousticDetectionDaoImpl rawAcousticDetectionDao) {
        this.rawAcousticDetectionDao = rawAcousticDetectionDao;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.jpaConnector.getEntityManager();
    }

    public void setJpaConnector(JpaConnector jpaConnector) {
        this.jpaConnector = jpaConnector;
    }

}

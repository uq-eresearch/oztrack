package org.oztrack.data.access.manager.impl;

import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.RawAcousticDetectionDao;
import org.oztrack.data.access.impl.*;
import org.oztrack.data.access.manager.DaoManager;
import org.oztrack.data.connector.JpaConnector;
import org.oztrack.data.model.ReceiverDeployment;

import javax.persistence.EntityManager;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:20 AM
 */
public class DaoManagerImpl implements DaoManager {

    private AcousticDetectionDao acousticDetectionDao;
    private AnimalDaoImpl animalDao;
    private UserDaoImpl userDao;
    private ProjectDaoImpl projectDao;
    private DataFileDaoImpl dataFileDao;
    private RawAcousticDetectionDaoImpl rawAcousticDetectionDao;
    private ReceiverDeploymentDaoImpl receiverDeploymentDao;
    private JpaConnector jpaConnector;


   public AcousticDetectionDao getAcousticDetectionDao() {
        return acousticDetectionDao;
    }

    public void setAcousticDetectionDao(AcousticDetectionDao acousticDetectionDao) {
        this.acousticDetectionDao = acousticDetectionDao;
    }

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

    public AnimalDaoImpl getAnimalDao() {
        return animalDao;
    }

    public void setAnimalDao(AnimalDaoImpl animalDao) {
        this.animalDao = animalDao;
    }

    public ReceiverDeploymentDaoImpl getReceiverDeploymentDao() {
        return receiverDeploymentDao;
    }

    public void setReceiverDeploymentDao(ReceiverDeploymentDaoImpl receiverDeploymentDao) {
        this.receiverDeploymentDao = receiverDeploymentDao;
    }



}

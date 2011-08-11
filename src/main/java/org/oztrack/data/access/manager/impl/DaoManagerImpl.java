package org.oztrack.data.access.manager.impl;

import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.impl.*;
import org.oztrack.data.access.impl.direct.JdbcAccessImpl;
import org.oztrack.data.access.impl.direct.JdbcQueryImpl;
import org.oztrack.data.access.manager.DaoManager;
import org.oztrack.data.connector.JpaConnector;

import javax.persistence.EntityManager;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:20 AM
 */
public class DaoManagerImpl implements DaoManager {

    private AcousticDetectionDao acousticDetectionDao;
    private PositionFixDao positionFixDao;
    private AnimalDaoImpl animalDao;
    private UserDaoImpl userDao;
    private ProjectDaoImpl projectDao;
    private DataFileDaoImpl dataFileDao;
    private RawAcousticDetectionDaoImpl rawAcousticDetectionDao;
    private ReceiverDeploymentDaoImpl receiverDeploymentDao;
    private JpaConnector jpaConnector;
    private JdbcAccessImpl JdbcAccess;
    private JdbcQueryImpl JdbcQuery;
    private SightingDaoImpl sightingDao;

    public JdbcAccessImpl getJdbcAccess() {
        return JdbcAccess;
    }

    public void setJdbcAccess(JdbcAccessImpl JdbcAccess) {
        this.JdbcAccess = JdbcAccess;
    }

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

    public SightingDaoImpl getSightingDao() {
        return sightingDao;
    }

    public void setSightingDao(SightingDaoImpl sightingDao) {
        this.sightingDao = sightingDao;
    }

    public JdbcQueryImpl getJdbcQuery() {
        return JdbcQuery;
    }

    public void setJdbcQuery(JdbcQueryImpl jdbcQuery) {
        JdbcQuery = jdbcQuery;
    }

    public PositionFixDao getPositionFixDao() {
        return positionFixDao;
    }

    public void setPositionFixDao(PositionFixDao positionFixDao) {
        this.positionFixDao = positionFixDao;
    }

}

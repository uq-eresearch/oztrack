package org.oztrack.data.access.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.ReceiverDeployment;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 2:07 PM
 */
public class ReceiverDeploymentDaoImpl extends JpaDao<ReceiverDeployment> implements ReceiverDeploymentDao, Serializable {

    public ReceiverDeploymentDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    public List<ReceiverDeployment> getReceiversByProjectId(Long projectId) {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from ReceiverDeployment o where o.project.id = :projectId");
        query.setParameter("projectId", projectId);
        try {
            @SuppressWarnings("unchecked")
            List <ReceiverDeployment> resultList = query.getResultList();
            return resultList;
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ReceiverDeployment getReceiverDeployment(String originalId, Long projectId) {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from ReceiverDeployment o where o.originalId = :originalId and o.project.id=:projectId");
        query.setParameter("originalId",originalId);
        query.setParameter("projectId",projectId);
        try {
            return (ReceiverDeployment) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void save(ReceiverDeployment object) {
        object.setUpdateDate(new java.util.Date());
        super.save(object);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public ReceiverDeployment update(ReceiverDeployment object) {
        object.setUpdateDate(new java.util.Date());
        return super.update(object);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

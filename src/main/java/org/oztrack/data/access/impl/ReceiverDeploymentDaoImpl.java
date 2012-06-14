package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.ReceiverDeployment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceiverDeploymentDaoImpl implements ReceiverDeploymentDao {
    private EntityManager em;
    
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional(readOnly=true)
    public List<ReceiverDeployment> getReceiversByProjectId(Long projectId) {
        Query query = em.createQuery("select o from ReceiverDeployment o where o.project.id = :projectId");
        query.setParameter("projectId", projectId);
        try {
            @SuppressWarnings("unchecked")
            List <ReceiverDeployment> resultList = query.getResultList();
            return resultList;
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly=true)
    public ReceiverDeployment getReceiverDeployment(String originalId, Long projectId) {
        Query query = em.createQuery("select o from ReceiverDeployment o where o.originalId = :originalId and o.project.id=:projectId");
        query.setParameter("originalId",originalId);
        query.setParameter("projectId",projectId);
        try {
            return (ReceiverDeployment) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void save(ReceiverDeployment object) {
        object.setUpdateDate(new java.util.Date());
        em.persist(object);
    }

    @Override
    @Transactional
    public ReceiverDeployment update(ReceiverDeployment object) {
        object.setUpdateDate(new java.util.Date());
        return em.merge(object);
    }
}

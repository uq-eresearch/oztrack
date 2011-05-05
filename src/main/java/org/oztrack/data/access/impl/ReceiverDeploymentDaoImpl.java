package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.ReceiverDeploymentDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.ReceiverDeployment;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 5/05/11
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiverDeploymentDaoImpl extends JpaDao<ReceiverDeployment> implements ReceiverDeploymentDao, Serializable {

    public ReceiverDeploymentDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

        public List<ReceiverDeployment> getReceiversByProjectId(Long projectId) {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from ReceiverDeployment o where o.project.id = :projectId");
        query.setParameter("projectId", projectId);
        try {
            return (List <ReceiverDeployment>) query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }


}

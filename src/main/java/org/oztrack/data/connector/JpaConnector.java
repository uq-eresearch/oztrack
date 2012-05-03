package org.oztrack.data.connector;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.restlet.service.ConnectorService;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:10 AM
 */
public class JpaConnector extends ConnectorService implements EntityManagerSource, Serializable {

    private final EntityManagerFactory emf;
    private final ThreadLocal<EntityManager> entityManagerTL = new ThreadLocal<EntityManager>();

    public JpaConnector(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerTL.get();
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            entityManagerTL.set(entityManager);
        }
        return entityManager;
    }

}

package org.oztrack.data.connector;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;

public class JpaConnector implements EntityManagerSource, Serializable {
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
package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.access.AnimalDao;

import java.io.Serializable;
import javax.persistence.NoResultException;
import javax.persistence.Query;
/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 4/05/11
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnimalDaoImpl extends JpaDao<Animal> implements AnimalDao, Serializable {

	public AnimalDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    public Animal getAnimalByProjectId(String animalProjectId, Long projectId) {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from Animal o where o.animalProjectId = :animalProjectId and o.projectId = :projectId");
        query.setParameter("animalProjectId", animalProjectId);
        query.setParameter("projectId", projectId);
        try {
            return (Animal) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}

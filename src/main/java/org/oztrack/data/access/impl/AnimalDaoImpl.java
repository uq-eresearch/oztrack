package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Project;

import java.io.Serializable;
import java.util.List;
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

    @Override
    public List<Animal> getAnimalsByProjectId(Long projectId) {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from Animal o where o.project.id = :projectId order by o.projectAnimalId");
        query.setParameter("projectId", projectId);
        try {
            return (List <Animal>) query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }



    @Override
    public Animal getAnimal(String animalId, Long projectId) {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from Animal o where o.project.id=:projectId and o.projectAnimalId=:animalId");
        query.setParameter("projectId", projectId);
        query.setParameter("animalId", animalId);
        try {
            return (Animal) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    public Animal getAnimalById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM Animal o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (Animal) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }



    @Override
    public void save(Animal object) {
        object.setUpdateDate(new java.util.Date());
        super.save(object);    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Override
    public Animal update(Animal object) {
        object.setUpdateDate(new java.util.Date());
        return super.update(object);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

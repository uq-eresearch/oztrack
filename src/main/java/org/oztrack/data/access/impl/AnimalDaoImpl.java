package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Animal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnimalDaoImpl implements AnimalDao {
    private EntityManager em;
    
    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional(readOnly=true)
    public List<Animal> getAnimalsByProjectId(Long projectId) {
        Query query = em.createQuery("select o from Animal o where o.project.id = :projectId order by o.projectAnimalId");
        query.setParameter("projectId", projectId);
        try {
            @SuppressWarnings("unchecked")
            List <Animal> resultList = (List <Animal>) query.getResultList();
            return resultList;
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly=true)
    public Animal getAnimal(String animalId, Long projectId) {
        Query query = em.createQuery("select o from Animal o where o.project.id=:projectId and o.projectAnimalId=:animalId");
        query.setParameter("projectId", projectId);
        query.setParameter("animalId", animalId);
        try {
            return (Animal) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    @Override
    @Transactional(readOnly=true)
    public Animal getAnimalById(Long id) {
        Query query = em.createQuery("SELECT o FROM Animal o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (Animal) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    @Override
    @Transactional
    public void save(Animal object) {
        object.setUpdateDate(new java.util.Date());
        em.persist(object);
    }

    @Override
    @Transactional
    public Animal update(Animal object) {
        object.setUpdateDate(new java.util.Date());
        return em.merge(object);
    }
}

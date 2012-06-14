package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectDaoImpl implements ProjectDao {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    @Transactional(readOnly=true)
    public List<Project> getAll() {
        @SuppressWarnings("unchecked")
        List<Project> resultList = em.createQuery("from Project").getResultList();
        return resultList;
    }

    @Override
    @Transactional(readOnly=true)
    public Project getProjectById(Long id) {
        Query query = em.createQuery("SELECT o FROM Project o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (Project) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    @Transactional
    public void save(Project object) {
        object.setUpdateDate(new java.util.Date());
        em.persist(object);
    }
    
    @Override
    @Transactional
    public Project update(Project object) {
        object.setUpdateDate(new java.util.Date());
        return em.merge(object);
    }
}

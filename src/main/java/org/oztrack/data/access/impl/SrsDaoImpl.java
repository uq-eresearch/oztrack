package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.SrsDao;
import org.oztrack.data.model.Srs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SrsDaoImpl implements SrsDao {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public List<Srs> getAll() {
        @SuppressWarnings("unchecked")
        List<Srs> resultList = em.createQuery("from org.oztrack.data.model.Srs order by identifier").getResultList();
        return resultList;
    }
    
    @Override
    public List<Srs> getAllOrderedByBoundsAreaDesc() {
        @SuppressWarnings("unchecked")
        List<Srs> resultList = em.createNativeQuery("select * from srs order by ST_Area(bounds) desc", Srs.class).getResultList();
        return resultList;
    }
    
    @Override
    public Srs getById(Long id) {
        return (Srs) em
            .createQuery("from org.oztrack.data.model.Srs where id = :id")
            .setParameter("id", id)
            .getSingleResult();
    }

    @Override
    @Transactional
    public void save(Srs srs) {
        em.persist(srs);
    }

    @Override
    @Transactional
    public Srs update(Srs srs) {
        return em.merge(srs);
    }
    
    @Override
    @Transactional
    public void delete(Srs srs) {
        em.remove(srs);
    }
}

package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.CountryDao;
import org.oztrack.data.model.Country;
import org.springframework.stereotype.Service;

@Service
public class CountryDaoImpl implements CountryDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Country> getAllOrderedByTitle() {
        @SuppressWarnings("unchecked")
        List<Country> resultList = em.createQuery("from org.oztrack.data.model.Country order by title").getResultList();
        return resultList;
    }

    @Override
    public Country getById(Long id) {
        return (Country) em
            .createQuery("from org.oztrack.data.model.Country where id = :id")
            .setParameter("id", id)
            .getSingleResult();
    }
}

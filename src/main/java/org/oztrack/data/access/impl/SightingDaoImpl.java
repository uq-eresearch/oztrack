package org.oztrack.data.access.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.SightingDao;
import org.oztrack.data.model.Sighting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SightingDaoImpl implements SightingDao {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    @Transactional
    public void save(Sighting sighting) {
        em.persist(sighting);
    }
}

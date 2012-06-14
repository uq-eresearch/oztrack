package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.Page;
import org.oztrack.data.model.AcousticDetection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcousticDetectionDaoImpl implements AcousticDetectionDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly=true)
    public int getTotalCount() {
        Query query = em.createQuery("SELECT count(*) FROM AcousticDetection");
        try {
            return Integer.parseInt(query.getSingleResult().toString());
        } catch (NoResultException ex) {
            return Integer.parseInt(null);
        }
    }
    
    @Override
    @Transactional(readOnly=true)
    public Page<AcousticDetection> getPage(int offset, int limit) {
        Query query = em.createQuery("from AcousticDetection");
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        @SuppressWarnings("unchecked")
        List<AcousticDetection> result = query.getResultList();
        int count = ((Number) em.createQuery("SELECT COUNT(o) FROM AcousticDetection o").getSingleResult()).intValue();
        return new Page<AcousticDetection>(result, offset, limit, count);
    }
}
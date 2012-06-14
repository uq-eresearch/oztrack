package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.RawAcousticDetectionDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RawAcousticDetectionDaoImpl implements RawAcousticDetectionDao {
    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly=true)
    public int getNumberRawDetections() {
        Query query = em.createQuery("SELECT count(*) FROM RawAcousticDetection");
        try {
            return Integer.parseInt(query.getSingleResult().toString());
        } catch (NoResultException ex) {
            return Integer.parseInt(null);
        }
    }

    @Transactional(readOnly=true)
    public List <String> getAllAnimalIds() {
        Query query = em.createQuery("SELECT distinct animalid from RawAcousticDetection");
        try {
            @SuppressWarnings("unchecked")
            List <String> resultList = query.getResultList();
            return resultList;
        } catch (NoResultException ex) {
            return null;
        }

    }

    @Transactional(readOnly=true)
    public List<String> getAllReceiverIds() {
        Query query = em.createQuery("SELECT distinct receiversn from RawAcousticDetection");
        try {
            @SuppressWarnings("unchecked")
            List <String> resultList = query.getResultList();
            return resultList;
        } catch (NoResultException ex) {
            return null;
        }
    }
}
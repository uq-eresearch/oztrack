package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.DataLicenceDao;
import org.oztrack.data.model.DataLicence;
import org.springframework.stereotype.Service;

@Service
public class DataLicenceDaoImpl implements DataLicenceDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<DataLicence> getAll() {
        @SuppressWarnings("unchecked")
        List<DataLicence> resultList = em.createQuery("from org.oztrack.data.model.DataLicence order by id").getResultList();
        return resultList;
    }

    @Override
    public DataLicence getById(Long id) {
        return (DataLicence) em
            .createQuery("from org.oztrack.data.model.DataLicence where id = :id")
            .setParameter("id", id)
            .getSingleResult();
    }
}
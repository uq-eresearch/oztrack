package org.oztrack.data.access.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.model.Institution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstitutionDaoImpl implements InstitutionDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Institution> getAll() {
        @SuppressWarnings("unchecked")
        List<Institution> resultList = em.createQuery("from org.oztrack.data.model.Institution order by id").getResultList();
        return resultList;
    }

    @Override
    public List<Institution> getAllOrderedByTitle() {
        @SuppressWarnings("unchecked")
        List<Institution> resultList = em.createQuery("from org.oztrack.data.model.Institution order by title").getResultList();
        return resultList;
    }

    @Override
    public Institution getById(Long id) {
        return (Institution) em
            .createQuery("from org.oztrack.data.model.Institution where id = :id")
            .setParameter("id", id)
            .getSingleResult();
    }

    @Override
    public Institution getByTitle(String title) {
        return (Institution) em
            .createQuery("from org.oztrack.data.model.Institution where lower(title) = lower(:title)")
            .setParameter("title", title)
            .getSingleResult();
    }

    @Override
    @Transactional
    public void save(Institution institution) {
        em.persist(institution);
    }

    @Override
    @Transactional
    public Institution update(Institution institution) {
        return em.merge(institution);
    }

    @Override
    @Transactional
    public void delete(Institution institution) {
        em.remove(institution);
    }

    // TODO: Query for records matching setSpec
    @Override
    public List<Institution> getInstitutionsForOaiPmh(Date from, Date until, String setSpec) {
        String q = "from org.oztrack.data.model.Institution";
        if ((from != null) || (until != null)) {
            q += "\nwhere ";
        }
        if (from != null) {
            q += "(((updateDate is not null) and (:from <= updateDate)) or ((updateDate is null) and (:from <= createDate)))";
        }
        if ((from != null) && (until != null)) {
            q+= "\nand ";
        }
        if (until != null) {
            q += "(((updateDate is not null) and (updateDate <= :until)) or ((updateDate is null) and (createDate <= :until)))";
        }
        Query query = em.createQuery(q);
        if (from != null) {
            query.setParameter("from", from);
        }
        if (until != null) {
            query.setParameter("until", until);
        }
        @SuppressWarnings("unchecked")
        List<Institution> resultList = query.getResultList();
        return resultList;
    }
}

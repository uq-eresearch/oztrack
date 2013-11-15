package org.oztrack.data.access.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.PersonDao;
import org.oztrack.data.model.Person;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonDaoImpl implements PersonDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Person> getAll() {
        @SuppressWarnings("unchecked")
        List<Person> resultList = em.createQuery("from org.oztrack.data.model.Person order by id").getResultList();
        return resultList;
    }

    @Override
    public List<Person> getAllOrderedByName() {
        @SuppressWarnings("unchecked")
        List<Person> resultList = em.createQuery("from org.oztrack.data.model.Person order by lastName, firstName").getResultList();
        return resultList;
    }

    @Override
    public Person getById(Long id) {
        return (Person) em
            .createQuery("from org.oztrack.data.model.Person where id = :id")
            .setParameter("id", id)
            .getSingleResult();
    }

    @Override
    public Person getByUuid(UUID uuid) {
        return (Person) em
            .createQuery("from org.oztrack.data.model.Person where uuid = :uuid")
            .setParameter("uuid", uuid)
            .getSingleResult();
    }

    @Override
    @Transactional
    public void save(Person person) {
        em.persist(person);
    }

    @Override
    @Transactional
    public Person update(Person person) {
        return em.merge(person);
    }

    @Override
    @Transactional
    public void delete(Person person) {
        em.remove(person);
    }

    // TODO: Query for records matching setSpec
    @Override
    public List<Person> getPeopleForOaiPmh(Date from, Date until, String setSpec) {
        String q = "from org.oztrack.data.model.Person";
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
        List<Person> resultList = query.getResultList();
        return resultList;
    }
}

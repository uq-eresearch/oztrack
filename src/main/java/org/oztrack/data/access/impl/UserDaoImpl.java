package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public User getByUsername(String username) {
        Query query = em.createQuery("SELECT o FROM AppUser o WHERE o.username = :username");
        query.setParameter("username", username);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public User getByAafId(String aafId) {
        Query query = em.createQuery("SELECT o FROM AppUser o WHERE o.aafId = :aafId");
        query.setParameter("aafId", aafId);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public User getUserById(Long id) {
        Query query = em.createQuery("SELECT o FROM AppUser o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public List<User> search(String term) {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery(
                "from org.oztrack.data.model.User\n" +
                "where\n" +
                "  lower(username) like lower('%' || :term || '%') or" +
                "  lower(firstName || ' ' || lastName) like lower('%' || :term || '%')"
            )
            .setParameter("term", term)
            .setMaxResults(10)
            .getResultList();
        return resultList;
    }

    @Override
    @Transactional
    public void save(User user) {
        em.persist(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        return em.merge(user);
    }
}
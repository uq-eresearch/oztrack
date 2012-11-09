package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> getAll() {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery("from org.oztrack.data.model.User")
            .getResultList();
        return resultList;
    }

    @Override
    public User getByUsername(String username) {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery("from org.oztrack.data.model.User where lower(username) = lower(:username)")
            .setParameter("username", username)
            .getResultList();
        assert resultList.size() <= 1;
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public User getByEmail(String email) {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery("from org.oztrack.data.model.User where lower(email) = lower(:email)")
            .setParameter("email", email)
            .getResultList();
        assert resultList.size() <= 1;
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public User getByAafId(String aafId) {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery("from org.oztrack.data.model.User where lower(aafId) = lower(:aafId)")
            .setParameter("aafId", aafId)
            .getResultList();
        assert resultList.size() <= 1;
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public User getByPasswordResetToken(String token) {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery("from org.oztrack.data.model.User where passwordResetToken = :passwordResetToken")
            .setParameter("passwordResetToken", token)
            .getResultList();
        assert resultList.size() <= 1;
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public User getById(Long id) {
        @SuppressWarnings("unchecked")
        List<User> resultList = em
            .createQuery("from org.oztrack.data.model.User where id = :id")
            .setParameter("id", id)
            .getResultList();
        assert resultList.size() <= 1;
        return resultList.isEmpty() ? null : resultList.get(0);
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
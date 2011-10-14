package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:08 AM
 */
public class UserDaoImpl extends JpaDao<User> implements UserDao, Serializable {
    public UserDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public User getByUsername(String username) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM AppUser o WHERE o.username = :username");
        query.setParameter("username", username);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public User getUserById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM AppUser o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
}

package org.oztrack.data.access;

import org.oztrack.data.model.User;

import au.edu.uq.itee.maenad.dataaccess.Dao;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:17 AM
 */
public interface UserDao extends Dao<User> {
    User getByUsername(String username);
    User getUserById(Long id);
    
}

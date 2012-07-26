package org.oztrack.data.access;

import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserDao {
    User getByUsername(String username);
    User getByAafId(String aafId);
    User getUserById(Long id);
    void save(User user);
    User update(User user);
}
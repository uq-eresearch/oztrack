package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserDao {
    User getByUsername(String username);
    User getByAafId(String aafId);
    User getUserById(Long id);
    List<User> search(String term);
    void save(User user);
    User update(User user);
}
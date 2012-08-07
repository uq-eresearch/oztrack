package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserDao {
    User getByUsername(String username);
    User getByEmail(String email);
    User getByAafId(String aafId);
    User getById(Long id);
    List<User> search(String term);
    void save(User user);
    User update(User user);
}
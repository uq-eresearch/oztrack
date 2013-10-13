package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Person;
import org.springframework.stereotype.Service;

@Service
public interface PersonDao {
    List<Person> getAll();
    List<Person> getAllOrderedByName();
    Person getById(Long id);
    void save(Person institution);
    Person update(Person institution);
    void delete(Person institution);
}

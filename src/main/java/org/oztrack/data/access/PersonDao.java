package org.oztrack.data.access;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.oztrack.data.model.Person;
import org.springframework.stereotype.Service;

@Service
public interface PersonDao {
    List<Person> getAll();
    List<Person> getAllOrderedByName();
    Person getById(Long id);
    Person getByUuid(UUID uuid);
    void save(Person institution);
    Person update(Person institution);
    void delete(Person institution);
    List<Person> getPeopleForOaiPmh(Date from, Date until, String setSpec);
}

package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Institution;
import org.springframework.stereotype.Service;

@Service
public interface InstitutionDao {
    List<Institution> getAll();
    List<Institution> getAllOrderedByTitle();
    Institution getById(Long id);
    void save(Institution institution);
    Institution update(Institution institution);
    void delete(Institution institution);
}

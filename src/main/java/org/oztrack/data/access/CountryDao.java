package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Country;
import org.springframework.stereotype.Service;

@Service
public interface CountryDao {
    List<Country> getAllOrderedByTitle();
    Country getById(Long id);
}

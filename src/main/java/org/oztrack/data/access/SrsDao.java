package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.Srs;
import org.springframework.stereotype.Service;

@Service
public interface SrsDao {
    List<Srs> getAll();
    List<Srs> getAllOrderedByBoundsAreaDesc();
    Srs getById(Long id);
    void save(Srs srs);
    Srs update(Srs srs);
    void delete(Srs srs);
}
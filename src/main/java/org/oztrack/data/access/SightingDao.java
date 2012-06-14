package org.oztrack.data.access;

import org.oztrack.data.model.Sighting;
import org.springframework.stereotype.Service;

@Service
public interface SightingDao {
    void save(Sighting sighting);
}

package org.oztrack.data.access;

import org.oztrack.data.model.types.OaiPmhSet;
import org.springframework.stereotype.Service;

@Service
public interface OaiPmhSetDao {
    public OaiPmhEntityProducer<OaiPmhSet> getSets();
}

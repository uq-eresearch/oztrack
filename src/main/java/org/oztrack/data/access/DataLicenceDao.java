package org.oztrack.data.access;

import java.util.List;

import org.oztrack.data.model.DataLicence;
import org.springframework.stereotype.Service;

@Service
public interface DataLicenceDao {
    List<DataLicence> getAll();
    DataLicence getById(Long id);
    DataLicence getByIdentifier(String identifier);
}
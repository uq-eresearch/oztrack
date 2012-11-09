package org.oztrack.data.access;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.springframework.stereotype.Service;

@Service
public interface DataFileDao {
    int getNumDataFiles();
    DataFile getDataFileById(Long id);
    DataFile getNextDataFile();
    ArrayList<String> getAllAnimalIds(DataFile datafile);
    Range<Date> getDetectionDateRange(DataFile dataFile, boolean includeDeleted);
    int getDetectionCount(DataFile dataFile, boolean includeDeleted);
    List<DataFile> getDataFilesByProject(Project project);
    void save(DataFile object);
    DataFile update(DataFile object);
    void delete(DataFile dataFile);
}
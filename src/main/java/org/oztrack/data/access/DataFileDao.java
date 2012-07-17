package org.oztrack.data.access;

import java.util.ArrayList;
import java.util.List;

import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.springframework.stereotype.Service;

@Service
public interface DataFileDao {
	DataFile getDataFileById(Long id);
    DataFile getNextDataFile();
    ArrayList<String> getAllAnimalIds(DataFile datafile);
    List<DataFile> getDataFilesByProject(Project project);
    void save(DataFile object);
    DataFile update(DataFile object);
    void delete(DataFile dataFile);
}
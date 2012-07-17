package org.oztrack.data.access;

import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;

public interface JdbcAccess {
    public int loadObservations(DataFile dataFile);
    public void truncateRawObservations(DataFile dataFile);
    public int updateProjectMetadata(Project project);
}
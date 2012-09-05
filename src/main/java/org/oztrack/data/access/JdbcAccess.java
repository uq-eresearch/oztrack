package org.oztrack.data.access;

import org.oztrack.data.model.DataFile;

public interface JdbcAccess {
    public int loadObservations(DataFile dataFile);
    public void truncateRawObservations(DataFile dataFile);
}
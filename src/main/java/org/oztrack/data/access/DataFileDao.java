package org.oztrack.data.access;

import au.edu.uq.itee.maenad.dataaccess.Dao;
import org.oztrack.data.model.DataFile;

/**
 * author: uqpnewm5
 * 29/03/2011
 * 1:36:37 PM
 */


public interface DataFileDao extends Dao<DataFile> {
	DataFile getDataFileById(Long id);
    DataFile getNextDataFile();

}
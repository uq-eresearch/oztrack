package org.oztrack.data.access;

import java.util.ArrayList;
import java.util.List;

import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;

import au.edu.uq.itee.maenad.dataaccess.Dao;

/**
 * author: uqpnewm5
 * 29/03/2011
 * 1:36:37 PM
 */


public interface DataFileDao extends Dao<DataFile> {
	DataFile getDataFileById(Long id);
    DataFile getNextDataFile();
    ArrayList<String> getAllAnimalIds(DataFile datafile);
    List<String> getAllReceiverIds();
    List<DataFile> getDataFilesByProject(Project project);

}
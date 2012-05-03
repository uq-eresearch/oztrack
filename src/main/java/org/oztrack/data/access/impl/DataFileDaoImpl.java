package org.oztrack.data.access.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:08 AM
 */
public class DataFileDaoImpl extends JpaDao<DataFile> implements DataFileDao, Serializable {
    
	public DataFileDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    @Override
    public DataFile getDataFileById(Long id) {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT o FROM datafile o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (DataFile) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public DataFile getNextDataFile() {
        Query query = entityManagerSource.getEntityManager().createQuery("select o from datafile o " +
                " where o.status='NEW'" +
                " and o.createDate = (select min(d.createDate) from datafile d where d.status='NEW') " +
                " and not exists (select 1 from datafile e where e.status='PROCESSING')");
        //query.setParameter("id", id);
        try {
            return (DataFile) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ArrayList<String> getAllAnimalIds(DataFile dataFile) {

        String entityName = "RawPositionFix";

        switch (dataFile.getProject().getProjectType()) {
            case PASSIVE_ACOUSTIC:
                entityName = "RawAcousticDetection";
                break;
            case GPS:
                entityName = "RawPositionFix";
                break;
            default:
                break;
        }


        Query query = entityManagerSource.getEntityManager().createQuery("SELECT animalId from " + entityName);
        try {
        	
        	List<String> animalIdList = (List<String>) query.getResultList();
        	ArrayList<String> finalList = new ArrayList<String>();
            
        	// implement distinct here because hibernate won't play nice 
        	HashSet<String> hashSet = new HashSet<String>(10000);
        	for (String animalId : animalIdList) {
        		if (!hashSet.contains(animalId)) {
        			hashSet.add(animalId);
        		}
        	}
        	
        	for (String animalId : hashSet) {
        		finalList.add(animalId);
        	}
        	
        	return finalList;
        	
        } catch (NoResultException ex) {
            return null;
        }

    }

    public List<String> getAllReceiverIds() {
        Query query = entityManagerSource.getEntityManager().createQuery("SELECT distinct receiversn from RawAcousticDetection");
        try {
            return (List <String>) query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    public List<DataFile> getDataFilesByProject(Project project) {
    	Query query = entityManagerSource.getEntityManager().createQuery("SELECT o from datafile o where o.project = :project order by o.createDate");
    	query.setParameter("project", project);
    	//query.setParameter("status", DataFileStatus.COMPLETE);
    	try {
    		return (List <DataFile>) query.getResultList();
    	}catch (NoResultException ex) {
    		return null;
    	}
    }


    @Override
    public void save(DataFile object) {
        object.setUpdateDate(new java.util.Date());
        super.save(object);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public DataFile update(DataFile object) {
        object.setUpdateDate(new java.util.Date());
        return super.update(object);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

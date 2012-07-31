package org.oztrack.data.access.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DataFileDaoImpl implements DataFileDao {
    private EntityManager em;
    
    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public DataFile getDataFileById(Long id) {
        Query query = em.createQuery("SELECT o FROM datafile o WHERE o.id = :id");
        query.setParameter("id", id);
        try {
            return (DataFile) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public DataFile getNextDataFile() {
        Query query = em.createQuery("select o from datafile o " +
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

    @Override
    public ArrayList<String> getAllAnimalIds(DataFile dataFile) {

        String entityName = "RawPositionFix";

        switch (dataFile.getProject().getProjectType()) {
            case GPS:
                entityName = "RawPositionFix";
                break;
            default:
                break;
        }


        Query query = em.createQuery("SELECT animalId from " + entityName);
        try {
        	
        	@SuppressWarnings("unchecked")
            List<String> animalIdList = query.getResultList();
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
    
    @Override
    public List<DataFile> getDataFilesByProject(Project project) {
    	Query query = em.createQuery("SELECT o from datafile o where o.project = :project order by o.createDate");
    	query.setParameter("project", project);
    	//query.setParameter("status", DataFileStatus.COMPLETE);
    	try {
    		@SuppressWarnings("unchecked")
            List <DataFile> resultList = (List <DataFile>) query.getResultList();
            return resultList;
    	}catch (NoResultException ex) {
    		return null;
    	}
    }


    @Override
    @Transactional
    public void save(DataFile object) {
        object.setUpdateDate(new java.util.Date());
        em.persist(object);
    }

    @Override
    @Transactional
    public DataFile update(DataFile object) {
        object.setUpdateDate(new java.util.Date());
        return em.merge(object);
    }
    
    @Override
    @Transactional
    public void delete(DataFile dataFile) {
        em.remove(dataFile);
    }
}

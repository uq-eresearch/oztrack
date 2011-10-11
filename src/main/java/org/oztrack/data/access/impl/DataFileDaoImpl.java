package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.DataFileStatus;
import org.oztrack.data.model.types.ProjectType;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

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

    public List<String> getAllAnimalIds(DataFile dataFile) {

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


        Query query = entityManagerSource.getEntityManager().createQuery("SELECT distinct animalid from " + entityName);
        try {
            return (List <String>) query.getResultList();
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
    	Query query = entityManagerSource.getEntityManager().createQuery("SELECT o from datafile o where o.status = :status and o.project = :project order by o.createDate");
    	query.setParameter("project", project);
    	query.setParameter("status", DataFileStatus.COMPLETE);
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

package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;

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
                " and o.uploadDate = (select min(d.uploadDate) from datafile d where d.status='NEW') " +
                " and not exists (select 1 from datafile e where e.status='PROCESSING')");
        //query.setParameter("id", id);
        try {
            return (DataFile) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}

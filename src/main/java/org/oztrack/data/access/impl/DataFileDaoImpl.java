package org.oztrack.data.access.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.Range;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.Animal;
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
    public int getNumDataFiles() {
        Query query = em.createQuery("select count(o) from org.oztrack.data.model.DataFile o");
        return ((Number) query.getSingleResult()).intValue();
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
        try {
            return (DataFile) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<String> getRawProjectAnimalIds(DataFile dataFile) {
        Query query = em.createQuery("SELECT animalId from RawPositionFix");
        @SuppressWarnings("unchecked")
        List<String> animalIdList = query.getResultList();
        // implement distinct here because hibernate won't play nice
        HashSet<String> hashSet = new HashSet<String>();
        for (String animalId : animalIdList) {
            hashSet.add(animalId);
        }
        ArrayList<String> finalList = new ArrayList<String>();
        for (String animalId : hashSet) {
            finalList.add(animalId);
        }
        return finalList;
    }

    @Override
    public List<Animal> getAnimals(DataFile dataFile) {
        @SuppressWarnings("unchecked")
        List<Animal> resultList = em
            .createQuery(
                "select distinct animal\n" +
                "from Animal as animal\n" +
                "inner join animal.positionFixes as positionFix\n" +
                "where positionFix.dataFile = :dataFile"
            )
            .setParameter("dataFile", dataFile)
            .getResultList();
        return resultList;
    }

    @Override
    public Range<Date> getDetectionDateRange(DataFile dataFile, boolean includeDeleted) {
        String sql =
            "select min(o.detectionTime)\n" +
            "from PositionFix o\n" +
            "where o.dataFile = :dataFile\n" +
            "and ((o.deleted = false) or (:includeDeleted = true))";
        Query query = em.createQuery(sql);
        query.setParameter("dataFile", dataFile);
        query.setParameter("includeDeleted", includeDeleted);
        Object[] result = (Object[]) query.getSingleResult();
        Date fromDate = (Date) result[0];
        Date toDate = (Date) result[1];
        return ((fromDate == null) || (toDate == null)) ? null : Range.between(fromDate, toDate);
    }

    @Override
    public int getDetectionCount(DataFile dataFile, boolean includeDeleted) {
        String sql =
            "select count(*)\n" +
            "from PositionFix o\n" +
            "where o.dataFile = :dataFile\n" +
            "and ((o.deleted = false) or (:includeDeleted = true))";
        Query query = em.createQuery(sql);
        query.setParameter("dataFile", dataFile);
        query.setParameter("includeDeleted", includeDeleted);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<DataFile> getDataFilesByProject(Project project) {
        Query query = em.createQuery("SELECT o from datafile o where o.project = :project order by o.createDate");
        query.setParameter("project", project);
        //query.setParameter("status", DataFileStatus.COMPLETE);
        try {
            @SuppressWarnings("unchecked")
            List <DataFile> resultList = query.getResultList();
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

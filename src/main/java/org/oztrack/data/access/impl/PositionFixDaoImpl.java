package org.oztrack.data.access.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;

import au.edu.uq.itee.maenad.dataaccess.Page;
import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 11/08/11
 * Time: 1:10 PM
 */
public class PositionFixDaoImpl extends JpaDao<PositionFix> implements PositionFixDao, Serializable {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    public PositionFixDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }

    public Page<PositionFix> getPage(SearchQuery searchQuery, int offset, int nbrObjectsPerPage) {

       try {
            Query query = buildQuery(searchQuery, false);
            logger.debug(query.toString());
            query.setFirstResult(offset);
            query.setMaxResults(nbrObjectsPerPage);
            @SuppressWarnings("unchecked")
            List<PositionFix> positionFixList = query.getResultList();
            Query countQuery = buildQuery(searchQuery, true);
            int count = Integer.parseInt(countQuery.getSingleResult().toString());
            return new Page<PositionFix>(positionFixList,offset,nbrObjectsPerPage, count);
       } catch (NoResultException ex) {
           entityManagerSource.getEntityManager().getTransaction().rollback();
           return null;
       }
    }

    // count : indicates whether the query returns a count of rows
    public Query buildQuery(SearchQuery searchQuery, boolean count) {

        String select = (count ? "count(o) " : "o ");
        String orderBy = (count ? "" : " order by o.detectionTime ");

        String sql = "select " + select
                   + "from PositionFix o "
                   + "where o.dataFile in "
                   + "(select d from datafile d where d.project.id = :projectId) ";


        if (searchQuery.getFromDate() != null) {
            sql = sql + "and o.detectionTime >= :fromDate ";
        }
        if (searchQuery.getToDate() != null) {
            sql = sql + "and o.detectionTime <= :toDate ";
        }
        if (searchQuery.getAnimalList() != null) {
            String animalClause = "and o.animal in (";
            for (int i=0; i < searchQuery.getAnimalList().size(); i++) {
                animalClause = animalClause + ":animal" + i + ",";
            }
            animalClause = animalClause.substring(0,animalClause.length()-1) + ")";
            sql = sql + animalClause;
        }


        sql = sql + orderBy;
        Query query = entityManagerSource.getEntityManager().createQuery(sql);
        query.setParameter("projectId", searchQuery.getProject().getId());


        if (searchQuery.getFromDate() != null) {
            query.setParameter("fromDate", searchQuery.getFromDate());
        }
        if (searchQuery.getToDate() != null) {
            query.setParameter("toDate", searchQuery.getToDate());
        }
        if (searchQuery.getAnimalList() != null) {
            for (int i=0; i < searchQuery.getAnimalList().size(); i++) {
                String paramName = "animal" + i;
                query.setParameter(paramName, searchQuery.getAnimalList().get(i));
            }
        }

        return query;
    }

    public List<PositionFix> getProjectPositionFixList(SearchQuery searchQuery) {
        Query query = buildQuery(searchQuery, false);
        @SuppressWarnings("unchecked")
        List<PositionFix> resultList = (List<PositionFix>) query.getResultList();
        return resultList;
    }

    /*
    public Polygon getProjectBoundingBox(Project project) {
    	
    	String projectId =  project.getId().toString();
    	String sql  = "(select (ST_Envelope(ST_Collect(t.locationGeometry))) "
                + " from PositionFix t "
                + ", dataFile d "
                + " where t.datafile_id=d.id"
                + " and d.project_id = :projectId) ";
    	
    	Query query = entityManagerSource.getEntityManager().createNativeQuery(sql);
    	query.setParameter("projectId", projectId);
    	Type polygonType = new CustomType(Polygon.class, null);
    	
    	Polygon boundingBox = (Polygon)query.getSingleResult();
    	return boundingBox;
    }
    
    */
    
    public Date getProjectFirstDetectionDate(Project project) {
    	
    	String projectId =  project.getId().toString();
        String sql = "select min(o.detectionTime)" 
        + "from PositionFix o "
        + "where o.dataFile in "
        + "(select d from datafile d where d.project.id = :projectId) ";
    	
    	Query query = entityManagerSource.getEntityManager().createQuery(sql);
    	query.setParameter("projectId", projectId);
    	Date firstDate = (Date)query.getSingleResult();
    	return firstDate;
    }
    
    public Date getProjectLastDetectionDate(Project project) {
    	
    	String projectId =  project.getId().toString();
        String sql = "select max(o.detectionTime)" 
            + "from PositionFix o "
            + "where o.dataFile in "
            + "(select d from datafile d where d.project.id = :projectId) ";
    	
    	Query query = entityManagerSource.getEntityManager().createQuery(sql);
    	query.setParameter("projectId", projectId);
    	Date lastDate = (Date)query.getSingleResult();
    	return lastDate;
    }
    
    public Date getDataFileFirstDetectionDate(DataFile dataFile) {
    	
        String sql = "select min(o.detectionTime)" 
        + "from PositionFix o "
        + "where o.dataFile = :dataFile";
    	
    	Query query = entityManagerSource.getEntityManager().createQuery(sql);
    	query.setParameter("dataFile", dataFile);
    	Date firstDate = (Date) query.getSingleResult();
    	return firstDate;
    }

    public Date getDataFileLastDetectionDate(DataFile dataFile) {
    	
        String sql = "select max(o.detectionTime)" 
        + "from PositionFix o "
        + "where o.dataFile = :dataFile";
    	
    	Query query = entityManagerSource.getEntityManager().createQuery(sql);
    	query.setParameter("dataFile", dataFile);
    	Date lastDate = (Date) query.getSingleResult();
    	return lastDate;
    }
    



}

package org.oztrack.data.access.impl;

import au.edu.uq.itee.maenad.dataaccess.Page;
import au.edu.uq.itee.maenad.dataaccess.jpa.EntityManagerSource;
import au.edu.uq.itee.maenad.dataaccess.jpa.JpaDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 11/08/11
 * Time: 1:10 PM
 */
public class PositionFixDaoImpl extends JpaDao<PositionFix> implements PositionFixDao, Serializable {

    public PositionFixDaoImpl(EntityManagerSource entityManagerSource) {
        super(entityManagerSource);
    }


   //Page<AcousticDetection> acousticDetectionsPage = acousticDetectionDao.getPage(offset,nbrObjectsPerPage);

    public Page<PositionFix> getPage(SearchQuery searchQuery, int offset, int nbrObjectsPerPage) {

       try {
            //query.setParameter("projectId", projectId);
            Query query = buildQuery(searchQuery, false);
            query.setFirstResult(offset);
            query.setMaxResults(nbrObjectsPerPage);
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

        String sql = "select " + (count ? "count(o) " : "o ")
                   + "from PositionFix o "
                   + ", datafile d "
                   + "where o.datafile_id=d.id "
                   + "and o.project.id = :projectId "
                   + "order by o.detectionTime";

        Query query = entityManagerSource.getEntityManager().createQuery(sql);
        query.setParameter("projectId", searchQuery.getProject().getId().toString());
        return query;
    }



}

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

        String select = (count ? "count(o) " : "o ");
        String orderBy = (count ? "" : " order by o.detectionTime");

        String sql = "select " + select
                   + "from PositionFix o "
                   + "where o.dataFile in"
                   + "(select d from datafile d where d.project.id = :projectId) "
                   + orderBy;

        Query query = entityManagerSource.getEntityManager().createQuery(sql);
        query.setParameter("projectId", searchQuery.getProject().getId());
        return query;
    }



}

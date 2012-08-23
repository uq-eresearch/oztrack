package org.oztrack.data.access.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.Page;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.DataFile;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTWriter;

@Service
public class PositionFixDaoImpl implements PositionFixDao {
    protected final Log logger = LogFactory.getLog(getClass());

    private EntityManager em;

    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Override
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
           em.getTransaction().rollback();
           return null;
       }
    }

    public Query buildQuery(SearchQuery searchQuery, boolean count) {

        String select = (count ? "count(o) " : "o ");
        String orderBy = (count ? "" : " order by o.animal.id, o.detectionTime ");

        String sql = "select " + select
                   + "from PositionFix o "
                   + "where o.dataFile in "
                   + "(select d from datafile d where d.project.id = :projectId) ";

        if ((searchQuery.getIncludeDeleted() == null) || !searchQuery.getIncludeDeleted()) {
            sql = sql + "and o.deleted = false ";
        }
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
        Query query = em.createQuery(sql);
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

    @Override
    public List<PositionFix> getProjectPositionFixList(SearchQuery searchQuery) {
        Query query = buildQuery(searchQuery, false);
        @SuppressWarnings("unchecked")
        List<PositionFix> resultList = (List<PositionFix>) query.getResultList();
        return resultList;
    }

    @Override
    public Date getProjectFirstDetectionDate(Project project) {

        String projectId =  project.getId().toString();
        String sql = "select min(o.detectionTime)"
        + "from PositionFix o "
        + "where o.deleted = false "
        + "and o.dataFile in "
        + "(select d from datafile d where d.project.id = :projectId) ";

        Query query = em.createQuery(sql);
        query.setParameter("projectId", projectId);
        Date firstDate = (Date)query.getSingleResult();
        return firstDate;
    }

    @Override
    public Date getProjectLastDetectionDate(Project project) {

        String projectId =  project.getId().toString();
        String sql = "select max(o.detectionTime)"
            + "from PositionFix o "
            + "where o.deleted = false "
            + "and o.dataFile in "
            + "(select d from datafile d where d.project.id = :projectId) ";

        Query query = em.createQuery(sql);
        query.setParameter("projectId", projectId);
        Date lastDate = (Date)query.getSingleResult();
        return lastDate;
    }

    @Override
    public Date getDataFileFirstDetectionDate(DataFile dataFile) {

        String sql = "select min(o.detectionTime)"
        + "from PositionFix o "
        + "where o.deleted = false "
        + "and o.dataFile = :dataFile";

        Query query = em.createQuery(sql);
        query.setParameter("dataFile", dataFile);
        Date firstDate = (Date) query.getSingleResult();
        return firstDate;
    }

    @Override
    public Date getDataFileLastDetectionDate(DataFile dataFile) {

        String sql = "select max(o.detectionTime)"
        + "from PositionFix o "
        + "where o.deleted = false "
        + "and o.dataFile = :dataFile";

        Query query = em.createQuery(sql);
        query.setParameter("dataFile", dataFile);
        Date lastDate = (Date) query.getSingleResult();
        return lastDate;
    }

    @Override
    @Transactional
    public int setDeletedOnOverlappingPositionFixes(Project project, List<Long> animalIds, MultiPolygon multiPolygon, boolean deleted) {
        String queryString =
            "update positionfix\n" +
            "set deleted = :deleted\n" +
            "where\n" +
            "    deleted = not(:deleted)\n" +
            "    and datafile_id in (select id from datafile where project_id = :projectId)\n" +
            "    and animal_id in (:animalIds)\n" +
            "    and ST_Within(locationgeometry, ST_GeomFromText(:wkt, 4326));";
        return em.createNativeQuery(queryString)
            .setParameter("projectId", project.getId())
            .setParameter("animalIds", animalIds)
            .setParameter("deleted", deleted)
            .setParameter("wkt", new WKTWriter().write(multiPolygon))
            .executeUpdate();
    }

    @Override
    @Transactional
    public int setDeletedOnAllPositionFixes(Project project, List<Long> animalIds, boolean deleted) {
        String queryString =
            "update positionfix\n" +
            "set deleted = :deleted\n" +
            "where\n" +
            "    deleted = not(:deleted)\n" +
            "    and datafile_id in (select id from datafile where project_id = :projectId)\n" +
            "    and animal_id in (:animalIds);";
        return em.createNativeQuery(queryString)
            .setParameter("projectId", project.getId())
            .setParameter("animalIds", animalIds)
            .setParameter("deleted", deleted)
            .executeUpdate();
    }
}
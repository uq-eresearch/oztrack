package org.oztrack.data.access.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.Range;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.Page;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.ArgosClass;
import org.oztrack.data.model.types.PositionFixStats;
import org.oztrack.data.model.types.TrajectoryStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTWriter;

@Service
public class PositionFixDaoImpl implements PositionFixDao {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private EntityManager em;

    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public PositionFix update(PositionFix object) {
        return em.merge(object);
    }

    @Override
    public int getNumPositionFixes() {
        Query query = em.createQuery("select count(o) from org.oztrack.data.model.PositionFix o");
        return ((Number) query.getSingleResult()).intValue();
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
        if (searchQuery.getAnimalIds() != null) {
            String animalClause = "and o.animal.id in (";
            for (int i = 0; i < searchQuery.getAnimalIds().size(); i++) {
                animalClause = animalClause + ":animal" + i + ",";
            }
            animalClause = animalClause.substring(0, animalClause.length() - 1) + ")";
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
        if (searchQuery.getAnimalIds() != null) {
            for (int i=0; i < searchQuery.getAnimalIds().size(); i++) {
                String paramName = "animal" + i;
                query.setParameter(paramName, searchQuery.getAnimalIds().get(i));
            }
        }

        return query;
    }

    @Override
    public List<PositionFix> getProjectPositionFixList(SearchQuery searchQuery) {
        Query query = buildQuery(searchQuery, false);
        @SuppressWarnings("unchecked")
        List<PositionFix> resultList = query.getResultList();
        return resultList;
    }

    @Override
    @Transactional
    public int setDeleted(
        Project project,
        Date fromDate,
        Date toDate,
        List<Long> animalIds,
        MultiPolygon multiPolygon,
        Set<PositionFix> speedFilterPositionFixes,
        ArgosClass minArgosClass,
        boolean deleted
    ) {
        String queryString =
            "update positionfix\n" +
            "set deleted = :deleted\n" +
            "where\n" +
            "    deleted = not(:deleted)\n" +
            "    and datafile_id in (select id from datafile where project_id = :projectId)\n" +
            "    and animal_id in (:animalIds)\n";
        if (fromDate != null) {
            queryString += "    and detectionTime >= :fromDate\n";
        }
        if (toDate != null) {
            queryString += "    and detectionTime <= :toDate\n";
        }
        if (speedFilterPositionFixes != null) {
            queryString += "    and id not in (:speedFilterPositionFixes)\n";
        }
        if (multiPolygon != null) {
            String unshiftedPointExpr = "locationgeometry";
            String unshiftedMultiPolygonExpr = "ST_GeomFromText(:wkt, 4326)";
            String pointExpr = project.getCrosses180() ? "ST_Shift_Longitude(" + unshiftedPointExpr + ")" : unshiftedPointExpr;
            String multiPolygonExpr = project.getCrosses180() ? "ST_Shift_Longitude(" + unshiftedMultiPolygonExpr + ")" : unshiftedMultiPolygonExpr;
            queryString += "    and ST_Within(" + pointExpr + ", " + multiPolygonExpr + ")\n";
        }
        if (minArgosClass != null) {
            queryString += "    and argosclass < :minArgosClass\n";
        }
        queryString += ";";
        Query query = em.createNativeQuery(queryString);
        query.setParameter("projectId", project.getId());
        query.setParameter("animalIds", animalIds);
        query.setParameter("deleted", deleted);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        };
        if (speedFilterPositionFixes != null) {
            query.setParameter("speedFilterPositionFixes", speedFilterPositionFixes);
        }
        if (multiPolygon != null) {
            query.setParameter("wkt", new WKTWriter().write(multiPolygon));
        }
        if (minArgosClass != null) {
            query.setParameter("minArgosClass", minArgosClass.ordinal());
        }
        return query.executeUpdate();
    }

    @Override
    @Transactional
    public void renumberPositionFixes(Project project) {
        em.createNativeQuery(
                "delete from positionfixlayer\n" +
                "where project_id = :projectId"
            )
            .setParameter("projectId", project.getId())
            .executeUpdate();

        String unshiftedPointExpr = "positionfix.locationgeometry";
        String pointExpr = project.getCrosses180() ? "ST_Shift_Longitude(" + unshiftedPointExpr + ")" : unshiftedPointExpr;
        em.createNativeQuery(
                "insert into positionfixlayer(\n" +
                "    id,\n" +
                "    project_id,\n" +
                "    animal_id,\n" +
                "    detectiontime,\n" +
                "    locationgeometry,\n" +
                "    deleted,\n" +
                "    colour\n" +
                ")\n" +
                "select\n" +
                "    positionfix.id as id,\n" +
                "    project.id as project_id,\n" +
                "    positionfix.animal_id as animal_id,\n" +
                "    positionfix.detectiontime as detectiontime,\n" +
                "    " + pointExpr + " as locationgeometry,\n" +
                "    positionfix.deleted as deleted,\n" +
                "    animal.colour as colour\n" +
                "from\n" +
                "    positionfix,\n" +
                "    animal,\n" +
                "    project\n" +
                "where\n" +
                "    positionfix.animal_id = animal.id and\n" +
                "    animal.project_id = project.id and\n" +
                "    project.id = :projectId"
            )
            .setParameter("projectId", project.getId())
            .executeUpdate();

        em.createNativeQuery(
                "delete from positionfixnumbered\n" +
                "where project_id = :projectId"
            )
            .setParameter("projectId", project.getId())
            .executeUpdate();

        em.createNativeQuery(
                "insert into positionfixnumbered(\n" +
                "    id,\n" +
                "    project_id,\n" +
                "    animal_id,\n" +
                "    detectiontime,\n" +
                "    locationgeometry,\n" +
                "    colour,\n" +
                "    row_number\n" +
                ")\n" +
                "select\n" +
                "    positionfix.id as id,\n" +
                "    project.id as project_id,\n" +
                "    positionfix.animal_id as animal_id,\n" +
                "    positionfix.detectiontime as detectiontime,\n" +
                "    positionfix.locationgeometry as locationgeometry,\n" +
                "    animal.colour as colour,\n" +
                "    row_number() over (partition by project.id, positionfix.animal_id order by positionfix.detectiontime) as row_number\n" +
                "from\n" +
                "    positionfix positionfix\n" +
                "    inner join animal on positionfix.animal_id = animal.id\n" +
                "    inner join project on animal.project_id = project.id\n" +
                "where\n" +
                "    project.id = :projectId and\n" +
                "    not(positionfix.deleted)"
            )
            .setParameter("projectId", project.getId())
            .executeUpdate();

        em.createNativeQuery(
                "delete from trajectorylayer\n" +
                "where project_id = :projectId"
            )
            .setParameter("projectId", project.getId())
            .executeUpdate();

        String unshiftedLineExpr = "ST_MakeLine(positionfix1.locationgeometry, positionfix2.locationgeometry)";
        String lineExpr = project.getCrosses180() ? "ST_Shift_Longitude(" + unshiftedLineExpr + ")" : unshiftedLineExpr;
        em.createNativeQuery(
                "insert into trajectorylayer(\n" +
                "    id,\n" +
                "    project_id,\n" +
                "    animal_id,\n" +
                "    startdetectiontime,\n" +
                "    enddetectiontime,\n" +
                "    colour,\n" +
                "    trajectorygeometry\n" +
                ")\n" +
                "select\n" +
                "    positionfix1.id as id,\n" +
                "    positionfix1.project_id as project_id,\n" +
                "    positionfix1.animal_id as animal_id,\n" +
                "    positionfix1.detectiontime as startdetectiontime,\n" +
                "    positionfix2.detectiontime as enddetectiontime,\n" +
                "    positionfix1.colour as colour,\n" +
                "    " + lineExpr + " as trajectorygeometry\n" +
                "from\n" +
                "    positionfixnumbered positionfix1\n" +
                "    inner join positionfixnumbered positionfix2 on\n" +
                "        positionfix1.project_id = positionfix2.project_id and\n" +
                "        positionfix1.animal_id = positionfix2.animal_id and\n" +
                "        positionfix1.row_number + 1 = positionfix2.row_number\n" +
                "where\n" +
                "    positionfix1.project_id = :projectId"
            )
            .setParameter("projectId", project.getId())
            .executeUpdate();
    }

    @Override
    public Map<Long, PositionFixStats> getAnimalPositionFixStats(Project project, Date fromDate, Date toDate) {
        String totalQueryString =
            "select\n" +
            "    animal_id,\n" +
            "    min(detectiontime),\n" +
            "    max(detectiontime),\n" +
            "    count(animal_id),\n" +
            "    count(animal_id) / (extract(epoch from max(detectiontime) - min(detectiontime)) / (60 * 60 * 24))\n" +
            "from positionfixlayer\n" +
            "where project_id = :projectId\n";
        if (fromDate != null) {
            totalQueryString += " and detectiontime >= DATE '" + isoDateFormat.format(fromDate) + "'\n";
        }
        if (toDate != null) {
            totalQueryString += " and detectiontime <= DATE '" + isoDateFormat.format(toDate) + "'\n";
        }
        totalQueryString += "group by animal_id";
        @SuppressWarnings("unchecked")
        List<Object[]> totalResultList = em.createNativeQuery(totalQueryString)
            .setParameter("projectId", project.getId())
            .getResultList();
        HashMap<Long, PositionFixStats> map = new HashMap<Long, PositionFixStats>();
        for (Object[] totalResult : totalResultList) {
            PositionFixStats stats = new PositionFixStats();
            long animalId = ((Number) totalResult[0]).longValue();
            stats.setAnimalId(animalId);
            stats.setStartDate(new Date(((Timestamp) totalResult[1]).getTime()));
            stats.setEndDate(new Date(((Timestamp) totalResult[2]).getTime()));
            stats.setCount(((Number) totalResult[3]).longValue());
            stats.setDailyMean(((Number) totalResult[4]).doubleValue());
            map.put(animalId, stats);
        }

        String dailyQueryString =
            "select distinct\n" +
            "    animal_id,\n" +
            "    max(count(date_trunc('day', detectiontime))) over (partition by animal_id)\n" +
            "from positionfixlayer\n" +
            "where project_id = :projectId\n";
        if (fromDate != null) {
            dailyQueryString += " and detectiontime >= DATE '" + isoDateFormat.format(fromDate) + "'\n";
        }
        if (toDate != null) {
            dailyQueryString += " and detectiontime <= DATE '" + isoDateFormat.format(toDate) + "'\n";
        }
        dailyQueryString += "group by animal_id, date_trunc('day', detectiontime)";
        @SuppressWarnings("unchecked")
        List<Object[]> dailyResultList = em.createNativeQuery(dailyQueryString)
            .setParameter("projectId", project.getId())
            .getResultList();
        for (Object[] dailyResult : dailyResultList) {
            Long animalId = ((Number) dailyResult[0]).longValue();
            map.get(animalId).setDailyMax(((Number) dailyResult[1]).longValue());
        }

        return map;
    }

    @Override
    public Map<Long, TrajectoryStats> getAnimalTrajectoryStats(Project project, Date fromDate, Date toDate) {
        String queryString =
            "select\n" +
            "    animal_id,\n" +
            "    min(startdetectiontime),\n" +
            "    max(enddetectiontime),\n" +
            "    sum(ST_Length_Spheroid(trajectorygeometry, 'SPHEROID[\"WGS 84\", 6378137, 298.257223563]')),\n" +
            "    avg(ST_Length_Spheroid(trajectorygeometry, 'SPHEROID[\"WGS 84\", 6378137, 298.257223563]')),\n" +
            "    (\n" +
            "        sum(ST_Length_Spheroid(trajectorygeometry, 'SPHEROID[\"WGS 84\", 6378137, 298.257223563]')) /\n" +
            "        sum(extract(epoch from (enddetectiontime - startdetectiontime)))" +
            "    )\n" +
            "from trajectorylayer\n" +
            "where\n" +
            "    project_id = :projectId and\n" +
            "    enddetectiontime > startdetectiontime\n";
        if (fromDate != null) {
            queryString += " and startdetectiontime >= DATE '" + isoDateFormat.format(fromDate) + "'\n";
        }
        if (toDate != null) {
            queryString += " and enddetectiontime <= DATE '" + isoDateFormat.format(toDate) + "'\n";
        }
        queryString += "group by animal_id";
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = em.createNativeQuery(queryString)
            .setParameter("projectId", project.getId())
            .getResultList();
        HashMap<Long, TrajectoryStats> map = new HashMap<Long, TrajectoryStats>();
        for (Object[] result : resultList) {
            Long animalId = ((Number) result[0]).longValue();
            TrajectoryStats stats = new TrajectoryStats();
            stats.setAnimalId(animalId);
            stats.setStartDate(new Date(((Timestamp) result[1]).getTime()));
            stats.setEndDate(new Date(((Timestamp) result[2]).getTime()));
            stats.setDistance(((Number) result[3]).doubleValue());
            stats.setMeanStepDistance(((Number) result[4]).doubleValue());
            stats.setMeanStepSpeed(((Number) result[5]).doubleValue());
            map.put(animalId, stats);
        }
        return map;
    }

    @Override
    public Map<Long, Range<Date>> getAnimalStartEndDates(Project project, Date fromDate, Date toDate) {
        String queryString =
            "select animal_id, min(startdetectiontime), max(enddetectiontime)\n" +
            "from trajectorylayer\n" +
            "where project_id = :projectId\n";
        if (fromDate != null) {
            queryString += " and startdetectiontime >= DATE '" + isoDateFormat.format(fromDate) + "'\n";
        }
        if (toDate != null) {
            queryString += " and enddetectiontime <= DATE '" + isoDateFormat.format(toDate) + "'\n";
        }
        queryString += "group by animal_id";
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = em.createNativeQuery(queryString)
            .setParameter("projectId", project.getId())
            .getResultList();
        HashMap<Long, Range<Date>> animalStartEndDates = new HashMap<Long, Range<Date>>();
        for (Object[] result : resultList) {
            Long animalId = ((Number) result[0]).longValue();
            Date startDate = (Date) result[1];
            Date endDate = (Date) result[2];
            animalStartEndDates.put(animalId, Range.between(startDate, endDate));
        }
        return animalStartEndDates;
    }
}
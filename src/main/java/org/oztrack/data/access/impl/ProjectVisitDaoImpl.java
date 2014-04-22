package org.oztrack.data.access.impl;

import java.util.Date;
import java.util.EnumMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.ProjectVisitDao;
import org.oztrack.data.model.ProjectVisit;
import org.oztrack.data.model.types.ProjectVisitSummary;
import org.oztrack.data.model.types.ProjectVisitType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectVisitDaoImpl implements ProjectVisitDao {
    private EntityManager em;

    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void save(ProjectVisit visit) {
        em.persist(visit);
    }

    @Override
    public EnumMap<ProjectVisitType, ProjectVisitSummary> getVisitSummaries() {
        EnumMap<ProjectVisitType, ProjectVisitSummary> visitCounts =
            new EnumMap<ProjectVisitType, ProjectVisitSummary>(ProjectVisitType.class);
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = em
            .createQuery(
                "select visit.visitType, count(visit), min(visitDate)\n" +
                "from ProjectVisit visit\n" +
                "group by visit.visitType"
            )
            .getResultList();
        for (Object[] result : resultList) {
            ProjectVisitType visitType = (ProjectVisitType) result[0];
            long numVisits = ((Number) result[1]).longValue();
            Date earliestDate = new Date(((Date) result[2]).getTime());
            ProjectVisitSummary visitSummary = new ProjectVisitSummary(numVisits, earliestDate);
            visitCounts.put(visitType, visitSummary);
        }
        return visitCounts;
    }
}

package org.oztrack.data.access.impl;

import java.util.EnumMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.ProjectVisitDao;
import org.oztrack.data.model.ProjectVisit;
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
    public EnumMap<ProjectVisitType, Long> getVisitCounts() {
        EnumMap<ProjectVisitType, Long> visitCounts = new EnumMap<ProjectVisitType, Long>(ProjectVisitType.class);
        for (ProjectVisitType visitType : ProjectVisitType.values()) {
            visitCounts.put(visitType, 0L);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = em
            .createQuery(
                "select visit.visitType, count(visit)\n" +
                "from ProjectVisit visit\n" +
                "group by visit.visitType"
            )
            .getResultList();
        for (Object[] result : resultList) {
            visitCounts.put((ProjectVisitType) result[0], ((Number) result[1]).longValue());
        }
        return visitCounts;
    }
}

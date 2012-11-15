package org.oztrack.data.access.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalysisDaoImpl implements AnalysisDao {
    private EntityManager em;

    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Override
    public Analysis getAnalysisById(Long id) {
        @SuppressWarnings("unchecked")
        List<Analysis> resultList = em.createQuery("SELECT o FROM Analysis o WHERE o.id = :id")
            .setParameter("id", id)
            .getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    @Transactional
    public void save(Analysis analysis) {
        analysis.setUpdateDate(new java.util.Date());
        em.persist(analysis);
    }

    @Override
    public List<Analysis> getPreviousAnalyses(User createUser, String createSession) {
        @SuppressWarnings("unchecked")
        List<Analysis> resultList = em
            .createQuery(
                "from org.oztrack.data.model.Analysis\n" +
                "where createUser = :createUser or createSession = :createSession\n" +
                "order by createDate")
            .setParameter("createUser", createUser)
            .setParameter("createSession", createSession)
            .setMaxResults(20)
            .getResultList();
        return resultList;
    }
}
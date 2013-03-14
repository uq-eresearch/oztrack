package org.oztrack.data.access.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.Project;
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
    public List<Analysis> getSavedAnalyses(Project project) {
        @SuppressWarnings("unchecked")
        List<Analysis> resultList = em
            .createQuery(
                "from org.oztrack.data.model.Analysis\n" +
                "where\n" +
                "    project = :project and\n" +
                "    saved = true\n" +
                "order by createDate desc")
            .setParameter("project", project)
            .setMaxResults(20)
            .getResultList();
        // We order by date descending in the query so we can limit to N most recent analyses.
        // Now reverse the list of those N analyses so order is by date ascending.
        Collections.reverse(resultList);
        return resultList;
    }

    @Override
    public List<Analysis> getPreviousAnalyses(Project project, User currentUser, String currentSessionId) {
        @SuppressWarnings("unchecked")
        List<Analysis> resultList = em
            .createQuery(
                "from org.oztrack.data.model.Analysis\n" +
                "where\n" +
                "    project = :project and\n" +
                "    saved = false and\n" +
                "    (\n" +
                "        :currentUserIsAdmin = true or\n" +
                "        createUser = :currentUser or\n" +
                "        createSession = :currentSession\n" +
                "    )\n" +
                "order by createDate desc")
            .setParameter("project", project)
            .setParameter("currentUserIsAdmin", (currentUser != null) && (currentUser.getAdmin() != null) && currentUser.getAdmin())
            .setParameter("currentUser", currentUser)
            .setParameter("currentSession", currentSessionId)
            .setMaxResults(20)
            .getResultList();
        // We order by date descending in the query so we can limit to N most recent analyses.
        // Now reverse the list of those N analyses so order is by date ascending.
        Collections.reverse(resultList);
        return resultList;
    }
}
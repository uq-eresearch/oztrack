package org.oztrack.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.oztrack.data.access.impl.AnalysisDaoImpl;
import org.oztrack.data.access.impl.PositionFixDaoImpl;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.types.AnalysisStatus;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AnalysisRunner {
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectPool<RConnection> rserveConnectionPool;

    public AnalysisRunner() {
    }

    @Async
    public void run(Long analysisId) {
        logger.info("Running analysis " + analysisId);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        AnalysisDaoImpl analysisDao = new AnalysisDaoImpl();
        analysisDao.setEntityManger(entityManager);
        PositionFixDaoImpl positionFixDao = new PositionFixDaoImpl();
        positionFixDao.setEntityManger(entityManager);
        positionFixDao.setDataSource(dataSource);
        try {
            entityManager.getTransaction().begin();
            Analysis analysis = analysisDao.getAnalysisById(analysisId);
            analysis.setStatus(AnalysisStatus.PROCESSING);
            analysisDao.save(analysis);
            entityManager.getTransaction().commit();

            entityManager.getTransaction().begin();
            analysis.setResultFilePath("analysis-" + analysis.getId().toString() + ".kml");
            List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(analysis.toSearchQuery());
            RserveInterface rserveInterface = new RserveInterface(rserveConnectionPool);
            rserveInterface.createKml(analysis, positionFixList);
            analysis.setStatus(AnalysisStatus.COMPLETE);
            analysisDao.save(analysis);
            entityManager.getTransaction().commit();
        }
        catch (Exception e) {
            logger.error("Error running analysis", e);
            try {
                entityManager.getTransaction().rollback();
            }
            catch (Exception e2) {
            }
            entityManager.clear();
            entityManager.getTransaction().begin();
            Analysis analysis = analysisDao.getAnalysisById(analysisId);
            analysis.setStatus(AnalysisStatus.FAILED);
            analysis.setMessage(e.getMessage());
            analysisDao.save(analysis);
            entityManager.getTransaction().commit();
        }
    }
}
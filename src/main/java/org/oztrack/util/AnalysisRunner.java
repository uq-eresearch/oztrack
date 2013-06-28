package org.oztrack.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.oztrack.data.access.impl.AnalysisDaoImpl;
import org.oztrack.data.access.impl.PositionFixDaoImpl;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisResultAttribute;
import org.oztrack.data.model.AnalysisResultFeature;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.types.AnalysisResultAttributeType;
import org.oztrack.data.model.types.AnalysisStatus;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.MultiPolygon;

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
            if (analysis.getAnalysisType().getReturnsAnimalFeatures()) {
                readResultFile(analysis);
            }
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

    @SuppressWarnings("unchecked")
    private void readResultFile(Analysis analysis) throws Exception {
        Parser parser = new Parser(new KMLConfiguration());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(analysis.getAbsoluteResultFilePath());
            SimpleFeature document = (SimpleFeature) parser.parse(inputStream);
            List<SimpleFeature> documentFeatures = (List<SimpleFeature>) document.getAttribute("Feature");
            if (documentFeatures.isEmpty()) {
                return;
            }

            // Get list of Placemark features: under either /Document/Folder or just /Document.
            List<SimpleFeature> placemarks =
                documentFeatures.get(0).getFeatureType().getName().getLocalPart().equals("folder")
                ? (List<SimpleFeature>) documentFeatures.get(0).getAttribute("Feature")
                : documentFeatures;

            analysis.getResultFeatures().clear();
            for (SimpleFeature placemark : placemarks) {
                Long animalId = Long.valueOf((String) placemark.getAttribute("id"));
                AnalysisResultFeature resultFeature = new AnalysisResultFeature();
                resultFeature.setAnalysis(analysis);
                for (Animal animal : analysis.getAnimals()) {
                    if (animal.getId().equals(animalId)) {
                        resultFeature.setAnimal(animal);
                        break;
                    }
                }
                resultFeature.setGeometry((MultiPolygon) placemark.getDefaultGeometry());
                HashSet<AnalysisResultAttribute> resultAttributes = new HashSet<AnalysisResultAttribute>();
                for (AnalysisResultAttributeType resultAttributeType : analysis.getAnalysisType().getResultAttributeTypes()) {
                    Object value = placemark.getAttribute(resultAttributeType.getIdentifier());
                    if (value != null) {
                        AnalysisResultAttribute resultAttribute = new AnalysisResultAttribute();
                        resultAttribute.setFeature(resultFeature);
                        resultAttribute.setName(resultAttributeType.getIdentifier());
                        resultAttribute.setValue((value != null) ? String.valueOf(value) : null);
                        resultAttributes.add(resultAttribute);
                    }
                }
                resultFeature.setAttributes(resultAttributes);
                analysis.getResultFeatures().add(resultFeature);
            }
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
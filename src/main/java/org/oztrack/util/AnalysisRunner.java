package org.oztrack.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.oztrack.data.access.impl.AnalysisDaoImpl;
import org.oztrack.data.access.impl.PositionFixDaoImpl;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisResultAttribute;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.FilterResultFeature;
import org.oztrack.data.model.HomeRangeResultFeature;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.types.AnalysisResultAttributeType;
import org.oztrack.data.model.types.AnalysisResultType;
import org.oztrack.data.model.types.AnalysisStatus;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;
import de.micromata.opengis.kml.v_2_2_0.TimeStamp;

@Service
public class AnalysisRunner {
    protected final Log logger = LogFactory.getLog(getClass());

    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    
    private final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

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
            if (analysis.getAnalysisType().getResultType() == AnalysisResultType.HOME_RANGE) {
                readHomeRangeResult(analysis);
            }
            else if (analysis.getAnalysisType().getResultType() == AnalysisResultType.FILTER) {
                readFilterResult(analysis);
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
    private void readHomeRangeResult(Analysis analysis) throws Exception {
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
                HomeRangeResultFeature resultFeature = new HomeRangeResultFeature();
                resultFeature.setAnalysis(analysis);
                for (Animal animal : analysis.getAnimals()) {
                    if (animal.getId().equals(animalId)) {
                        resultFeature.setAnimal(animal);
                        break;
                    }
                }
                resultFeature.setGeometry((MultiPolygon) placemark.getDefaultGeometry());
                HashSet<AnalysisResultAttribute> resultAttributes = new HashSet<AnalysisResultAttribute>();
                for (AnalysisResultAttributeType attributeType : analysis.getAnalysisType().getFeatureResultAttributeTypes()) {
                    Object value = placemark.getAttribute(attributeType.getIdentifier());
                    if (value != null) {
                        AnalysisResultAttribute resultAttribute = new AnalysisResultAttribute();
                        resultAttribute.setFeature(resultFeature);
                        resultAttribute.setName(attributeType.getIdentifier());
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

    private void readFilterResult(Analysis analysis) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(analysis.getAbsoluteResultFilePath());
            Kml kml = Kml.unmarshal(inputStream);
            Document document = (Document) kml.getFeature();
            List<Feature> features = document.getFeature();
            if (features.isEmpty()) {
                return;
            }
            analysis.getResultAttributes().clear();
            SchemaData documentSchemaData = document.getExtendedData().getSchemaData().get(0);
            for (AnalysisResultAttributeType attributeType : analysis.getAnalysisType().getOverallResultAttributeTypes()) {
                String value = null;
                for (SimpleData simpleData : documentSchemaData.getSimpleData()) {
                    if (simpleData.getName().equals(attributeType.getIdentifier())) {
                        value = simpleData.getValue();
                        break;
                    }
                }
                if (value != null) {
                    AnalysisResultAttribute overallResultAttribute = new AnalysisResultAttribute();
                    overallResultAttribute.setAnalysis(analysis);
                    overallResultAttribute.setName(attributeType.getIdentifier());
                    overallResultAttribute.setValue(value);
                    analysis.getResultAttributes().add(overallResultAttribute);
                }
            }
            analysis.getResultFeatures().clear();
            for (Feature feature : features) {
                Placemark placemark = (Placemark) feature;
                FilterResultFeature resultFeature = new FilterResultFeature();
                resultFeature.setAnalysis(analysis);
                resultFeature.setAnimal(analysis.getAnimals().iterator().next());
                TimeStamp timeStamp = (TimeStamp) placemark.getTimePrimitive();
                resultFeature.setDateTime(isoDateTimeFormat.parse(timeStamp.getWhen()));
                Point point = (Point) placemark.getGeometry();
                resultFeature.setGeometry(geometryFactory.createPoint(new Coordinate(
                    point.getCoordinates().get(0).getLongitude(),
                    point.getCoordinates().get(0).getLatitude()
                )));
                HashSet<AnalysisResultAttribute> featureResultAttributes = new HashSet<AnalysisResultAttribute>();
                SchemaData placemarkSchemaData = placemark.getExtendedData().getSchemaData().get(0);
                for (AnalysisResultAttributeType attributeType : analysis.getAnalysisType().getFeatureResultAttributeTypes()) {
                    String value = null;
                    for (SimpleData simpleData : placemarkSchemaData.getSimpleData()) {
                        if (simpleData.getName().equals(attributeType.getIdentifier())) {
                            value = simpleData.getValue();
                            break;
                        }
                    }
                    if (value != null) {
                        AnalysisResultAttribute featureResultAttribute = new AnalysisResultAttribute();
                        featureResultAttribute.setFeature(resultFeature);
                        featureResultAttribute.setName(attributeType.getIdentifier());
                        featureResultAttribute.setValue(value);
                        featureResultAttributes.add(featureResultAttribute);
                    }
                }
                resultFeature.setAttributes(featureResultAttributes);
                analysis.getResultFeatures().add(resultFeature);
            }
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
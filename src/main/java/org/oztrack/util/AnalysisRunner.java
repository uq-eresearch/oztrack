package org.oztrack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.GeometryFactory;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;
import de.micromata.opengis.kml.v_2_2_0.TimeStamp;

@Service
public class AnalysisRunner {
    private final Logger logger = Logger.getLogger(getClass());

    private final GeometryFactory jtsGeometryFactory = JTSFactoryFinder.getGeometryFactory(null);

    private final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RserveConnectionPool rserveConnectionPool;

    @Autowired
    private ProjectAnimalsMutexExecutor renumberPositionFixesExecutor;

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
        positionFixDao.setRenumberPositionFixesExecutor(renumberPositionFixesExecutor);
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
            rserveInterface.runAnalysis(analysis, positionFixList);
            if (!new File(analysis.getAbsoluteResultFilePath()).canRead()) {
                throw new Exception("Analysis result file not found.");
            }
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

    private void readHomeRangeResult(Analysis analysis) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(analysis.getAbsoluteResultFilePath());
            Kml kml = Kml.unmarshal(inputStream);
            Document document = (Document) kml.getFeature();
            List<Feature> documentFeatures = document.getFeature();
            if (documentFeatures.isEmpty()) {
                return;
            }

            // Get list of Placemark features: under either /Document/Folder or just /Document.
            List<Feature> features =
                (documentFeatures.get(0) instanceof Folder)
                ? ((Folder) documentFeatures.get(0)).getFeature()
                : documentFeatures;

            analysis.getResultFeatures().clear();
            for (Feature feature : features) {
                Placemark placemark = (Placemark) feature;
                SchemaData placemarkSchemaData = placemark.getExtendedData().getSchemaData().get(0);
                Long animalId = null;
                for (SimpleData simpleData : placemarkSchemaData.getSimpleData()) {
                    if (simpleData.getName().equals("id")) {
                        animalId = Long.valueOf(simpleData.getValue());
                        break;
                    }
                }
                if (animalId == null) {
                    continue;
                }
                HomeRangeResultFeature resultFeature = new HomeRangeResultFeature();
                resultFeature.setAnalysis(analysis);
                for (Animal animal : analysis.getAnimals()) {
                    if (animal.getId().equals(animalId)) {
                        resultFeature.setAnimal(animal);
                        break;
                    }
                }
                MultiGeometry kmlMultiGeometry = (MultiGeometry) placemark.getGeometry();
                resultFeature.setGeometry(convertKmlMultiGeometryToJtsMultiPolygon(kmlMultiGeometry));
                HashSet<AnalysisResultAttribute> resultAttributes = new HashSet<AnalysisResultAttribute>();
                for (AnalysisResultAttributeType attributeType : analysis.getAnalysisType().getFeatureResultAttributeTypes()) {
                    String value = null;
                    for (SimpleData simpleData : placemarkSchemaData.getSimpleData()) {
                        if (simpleData.getName().equals(attributeType.getIdentifier())) {
                            value = simpleData.getValue();
                            break;
                        }
                    }
                    if (value != null) {
                        AnalysisResultAttribute resultAttribute = new AnalysisResultAttribute();
                        resultAttribute.setFeature(resultFeature);
                        resultAttribute.setName(attributeType.getIdentifier());
                        resultAttribute.setValue(value);
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

    private com.vividsolutions.jts.geom.MultiPolygon convertKmlMultiGeometryToJtsMultiPolygon(MultiGeometry kmlMultiGeometry) {
        ArrayList<com.vividsolutions.jts.geom.Polygon> jtsPolygons = new ArrayList<com.vividsolutions.jts.geom.Polygon>();
        for (Geometry kmlGeometry: kmlMultiGeometry.getGeometry()) {
            jtsPolygons.add(convertKmlPolygonToJtsPolygon((Polygon) kmlGeometry));
        }
        return jtsGeometryFactory.createMultiPolygon(jtsPolygons.toArray(new com.vividsolutions.jts.geom.Polygon[] {}));
    }

    private com.vividsolutions.jts.geom.Polygon convertKmlPolygonToJtsPolygon(Polygon kmlPolygon) {
        com.vividsolutions.jts.geom.LinearRing jtsShell = convertKmlBoundaryToJtsLinearRing(kmlPolygon.getOuterBoundaryIs());
        ArrayList<com.vividsolutions.jts.geom.LinearRing> jtsHoles = new ArrayList<com.vividsolutions.jts.geom.LinearRing>();
        for (Boundary jtsInnerBoundary : kmlPolygon.getInnerBoundaryIs()) {
            jtsHoles.add(convertKmlBoundaryToJtsLinearRing(jtsInnerBoundary));
        }
        return jtsGeometryFactory.createPolygon(jtsShell, jtsHoles.toArray(new com.vividsolutions.jts.geom.LinearRing[] {}));
    }

    private com.vividsolutions.jts.geom.LinearRing convertKmlBoundaryToJtsLinearRing(Boundary jtsBoundary) {
        ArrayList<com.vividsolutions.jts.geom.Coordinate> jtsCoordinates = new ArrayList<com.vividsolutions.jts.geom.Coordinate>();
        for (Coordinate kmlCoordinate : jtsBoundary.getLinearRing().getCoordinates()) {
            jtsCoordinates.add(new com.vividsolutions.jts.geom.Coordinate(
                kmlCoordinate.getLongitude(),
                kmlCoordinate.getLatitude()
            ));
        }
        return jtsGeometryFactory.createLinearRing(jtsCoordinates.toArray(new com.vividsolutions.jts.geom.Coordinate[] {}));
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
                double longitude = point.getCoordinates().get(0).getLongitude();
                if (analysis.getProject().getCrosses180() && (longitude > 180d)) {
                    longitude -= 360d;
                }
                com.vividsolutions.jts.geom.Point geometry = jtsGeometryFactory.createPoint(new com.vividsolutions.jts.geom.Coordinate(
                    longitude,
                    point.getCoordinates().get(0).getLatitude()
                ));
                geometry.setSRID(4326);
                resultFeature.setGeometry(geometry);
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
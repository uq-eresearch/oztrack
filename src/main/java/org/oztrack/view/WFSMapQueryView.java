package org.oztrack.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.wfs.v1_1.WFSConfiguration;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.springframework.web.servlet.view.AbstractView;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public class WFSMapQueryView extends AbstractView {
    private static final String namespacePrefix = "oztrack";
    private static final String namespaceURI = "http://oztrack.org/xmlns#";
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    // TODO: DAO should not appear in this layer.
    private ProjectDao projectDao;

    // TODO: DAO should not appear in this layer.
    private PositionFixDao positionFixDao;
    
    public WFSMapQueryView(ProjectDao projectDao, PositionFixDao positionFixDao) {
        this.projectDao = projectDao;
        this.positionFixDao = positionFixDao;
    }

    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        SearchQuery searchQuery;

        if (model != null) {
            logger.debug("Resolving ajax request view ");
            searchQuery = (SearchQuery) model.get("searchQuery");

            if (searchQuery.getMapQueryType() != null) {
                SimpleFeatureCollection collection = FeatureCollections.newCollection();
                WFSConfiguration wfsConfiguration = new org.geotools.wfs.v1_1.WFSConfiguration();
                @SuppressWarnings("unchecked")
                Set<QName> wfsConfigurationProperties = wfsConfiguration.getProperties();
                wfsConfigurationProperties.add(GMLConfiguration.NO_FEATURE_BOUNDS);
                Encoder e = new Encoder(wfsConfiguration);
                e.setIndenting(true);
                FeatureCollectionType featureCollectionType = WfsFactory.eINSTANCE.createFeatureCollectionType();

                @SuppressWarnings("unchecked")
                EList<SimpleFeatureCollection> feature = featureCollectionType.getFeature();
                switch (searchQuery.getMapQueryType()) {
                    case PROJECTS:
                		collection = this.buildAllProjectsFeatureCollection();
                		e.getNamespaces().declarePrefix(namespacePrefix, namespaceURI);
                		feature.add(collection);
                		break;
                    case POINTS:
                    case LINES:
                    case START_END:
                        collection = this.buildFeatureCollection(searchQuery);
                        e.getNamespaces().declarePrefix(namespacePrefix, namespaceURI);
                        feature.add(collection);
                        break;
                    default:
                        throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
                }

                response.setContentType("text/xml");
                response.setHeader("Content-Encoding", "gzip");
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
                try {
                	e.encode(featureCollectionType, org.geotools.wfs.WFS.FeatureCollection, gzipOutputStream);
                }
                catch (IOException ex) {
                	logger.error(ex.getMessage());
                }
                gzipOutputStream.close();
            }
        }
    }

    private static class AnimalTrack {
        private Animal animal;
        private Date fromDate;
        private Date toDate;
        private List<Coordinate> coordinates;
        private Coordinate startPoint;
        private Coordinate endPoint;
    }
    
    private SimpleFeatureCollection buildFeatureCollection(SearchQuery searchQuery) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        Integer srid = positionFixList.isEmpty() ? null : positionFixList.get(0).getLocationGeometry().getSRID();
        
        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Track");
        simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
        simpleFeatureTypeBuilder.add("identifier", String.class);
        simpleFeatureTypeBuilder.add("animalId", String.class);
        simpleFeatureTypeBuilder.add("fromDate", Date.class);
        simpleFeatureTypeBuilder.add("toDate", Date.class);
        Class<?> trackClass = null;
        switch (searchQuery.getMapQueryType()) {
            case POINTS:
                trackClass = MultiPoint.class;
                break;
            case LINES:
                trackClass = LineString.class;
                break;
            case START_END:
                trackClass = MultiPoint.class;
                break;
            default:
                throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
        }
        simpleFeatureTypeBuilder.add("track", trackClass, srid);

        HashMap<Long, AnimalTrack> tracks = new HashMap<Long, AnimalTrack>();
        for (PositionFix positionFix : positionFixList) {
            AnimalTrack thisTrack = tracks.get(positionFix.getAnimal().getId());
            Coordinate thisCoordinate = positionFix.getLocationGeometry().getCoordinate();
            if (thisTrack == null) {
                thisTrack = new AnimalTrack();
                thisTrack.animal = positionFix.getAnimal();
                thisTrack.fromDate = null;
                thisTrack.toDate = null;
                thisTrack.coordinates = new ArrayList<Coordinate>();
                thisTrack.startPoint = null;
                thisTrack.endPoint = null;
                tracks.put(positionFix.getAnimal().getId(), thisTrack);
            }
            thisTrack.coordinates.add(thisCoordinate);
            if ((thisTrack.fromDate == null) || positionFix.getDetectionTime().before(thisTrack.fromDate)) {
                thisTrack.fromDate = positionFix.getDetectionTime();
                thisTrack.startPoint = thisCoordinate;
            }
            if ((thisTrack.toDate == null) || positionFix.getDetectionTime().after(thisTrack.toDate)) {
                thisTrack.toDate = positionFix.getDetectionTime();
                thisTrack.endPoint = thisCoordinate;
            }
        }

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (AnimalTrack animalTrack: tracks.values()) {
            switch (searchQuery.getMapQueryType()) {
            case POINTS:
                MultiPoint points = geometryFactory.createMultiPoint(animalTrack.coordinates.toArray(new Coordinate[] {}));
                featureCollection.add(buildAnimalTrackFeature(featureBuilder, animalTrack, "detections", points));
                break;
            case LINES:
                LineString lines = geometryFactory.createLineString(animalTrack.coordinates.toArray(new Coordinate[] {}));
                featureCollection.add(buildAnimalTrackFeature(featureBuilder, animalTrack, "trajectory", lines));
                break;
            case START_END:
                MultiPoint startPoint = geometryFactory.createMultiPoint(new Coordinate[] {animalTrack.startPoint});
                MultiPoint endPoint = geometryFactory.createMultiPoint(new Coordinate[] {animalTrack.endPoint});
                featureCollection.add(buildAnimalTrackFeature(featureBuilder, animalTrack, "start", startPoint));
                featureCollection.add(buildAnimalTrackFeature(featureBuilder, animalTrack, "end", endPoint));
                break;
            default:
                throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
            }
        }
        return featureCollection;
    }
    
    private SimpleFeature buildAnimalTrackFeature(SimpleFeatureBuilder featureBuilder, AnimalTrack animalTrack, String identifier, Object trackObject) {
        featureBuilder.set("identifier", identifier);
        featureBuilder.set("animalId", animalTrack.animal.getId());
        featureBuilder.set("fromDate", animalTrack.fromDate);
        featureBuilder.set("toDate", animalTrack.toDate);
        featureBuilder.set("track", trackObject);
        return featureBuilder.buildFeature(animalTrack.animal.getId().toString() + "-" + identifier);
    }

    private SimpleFeatureCollection buildAllProjectsFeatureCollection() {
        List<Project> projectList = projectDao.getAll();
        Integer srid = 4326;

        SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        featureTypeBuilder.setName("Project");
        featureTypeBuilder.setNamespaceURI(namespaceURI);
        featureTypeBuilder.add("projectCentroid", Point.class, srid);
        featureTypeBuilder.add("projectId", String.class);
        featureTypeBuilder.add("projectTitle", String.class);
        featureTypeBuilder.add("projectDescription", String.class);
        featureTypeBuilder.add("firstDetectionDate",String.class);
        featureTypeBuilder.add("lastDetectionDate",String.class);
        featureTypeBuilder.add("spatialCoverageDescr", String.class);
        featureTypeBuilder.add("speciesCommonName", String.class);
        featureTypeBuilder.add("global", Boolean.class);
        SimpleFeatureType featureType = featureTypeBuilder.buildFeatureType();

        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        for (Project project : projectList) {
        	if (project.getBoundingBox() != null) {
        	    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        		featureBuilder.set("projectCentroid", project.getBoundingBox().getCentroid());
        		featureBuilder.set("projectId", project.getId().toString());
        		featureBuilder.set("projectTitle", project.getTitle());
	        	featureBuilder.set("projectDescription", project.getDescription());
	        	featureBuilder.set("firstDetectionDate", sdf.format(project.getFirstDetectionDate()));
	        	featureBuilder.set("lastDetectionDate", sdf.format(project.getLastDetectionDate()));
	        	featureBuilder.set("spatialCoverageDescr", project.getSpatialCoverageDescr());
	        	featureBuilder.set("speciesCommonName", project.getSpeciesCommonName());
	        	featureBuilder.set("global", project.isGlobal());

	        	SimpleFeature feature = featureBuilder.buildFeature(project.getId().toString());
	            featureCollection.add(feature);
        	}
        	else {
        		logger.error("No bounding box for project " + project.getId() + " (" + project.getTitle() + ")");
        	}
        }

        return featureCollection;
    }   
}
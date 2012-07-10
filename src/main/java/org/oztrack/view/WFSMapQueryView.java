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
import org.oztrack.app.OzTrackApplication;
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
        String namespaceURI = OzTrackApplication.getApplicationContext().getUriPrefix();

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
                    case ALL_PROJECTS:
                		collection = this.buildAllProjectsFeatureCollection();
                		e.getNamespaces().declarePrefix("Project", namespaceURI);
                		feature.add(collection);
                		break;
                    case ALL_POINTS:
                    case POINTS:
                        collection = this.buildFeatureCollection(searchQuery,"points");
                        //**encoder.setNamespace("PositionFix", namespaceURI);
                        e.getNamespaces().declarePrefix("Track", namespaceURI);
                        feature.add(collection);
                        break;
                    case ALL_LINES:
                    case LINES:
                        collection = this.buildFeatureCollection(searchQuery,"lines");
                        //**encoder.setNamespace("Track", namespaceURI);
                        e.getNamespaces().declarePrefix("Track", namespaceURI);
                        feature.add(collection);
                        break;
                    default:
                        break;
                }

                response.setContentType("text/xml");
                response.setHeader("Content-Encoding", "gzip");
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
                //encoder.encode(gzipOutputStream, collection);
                try {
                	e.encode(featureCollectionType, org.geotools.wfs.WFS.FeatureCollection, gzipOutputStream);
                } catch (IOException ex) {
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
    
    private SimpleFeatureCollection buildFeatureCollection(SearchQuery searchQuery, String collectionType) {

        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);

        String namespaceURI = OzTrackApplication.getApplicationContext().getUriPrefix();
        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Track");
        simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
        Integer srid = positionFixList.isEmpty() ? null : positionFixList.get(0).getLocationGeometry().getSRID();
        
        if (collectionType == "points") {
            simpleFeatureTypeBuilder.add("track", MultiPoint.class, srid);
        } else if (collectionType == "lines") {
            simpleFeatureTypeBuilder.add("track", LineString.class, srid);
        }
        simpleFeatureTypeBuilder.add("fromDate", Date.class);
        simpleFeatureTypeBuilder.add("toDate", Date.class);
        simpleFeatureTypeBuilder.add("animalId",String.class);
        simpleFeatureTypeBuilder.add("projectAnimalId",String.class);
        simpleFeatureTypeBuilder.add("animalName",String.class);
        simpleFeatureTypeBuilder.add("startPoint", Point.class, srid);
        simpleFeatureTypeBuilder.add("endPoint", Point.class, srid);

        SimpleFeatureType simpleFeatureType = simpleFeatureTypeBuilder.buildFeatureType();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(simpleFeatureType);
        HashMap<Long, AnimalTrack> tracks = new HashMap<Long, AnimalTrack>();
        
        for (PositionFix positionFix : positionFixList) {

            AnimalTrack thisTrack = tracks.get(positionFix.getAnimal().getId());
            Coordinate thisCoordinate = positionFix.getLocationGeometry().getCoordinate();
            
            if (thisTrack == null) {

                thisTrack = new AnimalTrack();
                thisTrack.animal = positionFix.getAnimal();
                thisTrack.fromDate = positionFix.getDetectionTime();
                thisTrack.toDate = positionFix.getDetectionTime();
                thisTrack.coordinates = new ArrayList<Coordinate>();
                thisTrack.coordinates.add(thisCoordinate);
                thisTrack.startPoint = thisCoordinate;
                thisTrack.endPoint = thisCoordinate;
                tracks.put(positionFix.getAnimal().getId(),thisTrack);

            } else {

                if (positionFix.getDetectionTime().before(thisTrack.fromDate)) {
                    thisTrack.fromDate = positionFix.getDetectionTime();
                    thisTrack.startPoint = thisCoordinate;
                }
                if (positionFix.getDetectionTime().after(thisTrack.toDate)) {
                    thisTrack.toDate = positionFix.getDetectionTime();
                    thisTrack.endPoint = thisCoordinate;
                }
                thisTrack.coordinates.add(thisCoordinate);

            }
        }

        for (AnimalTrack animalTrack: tracks.values()) {

            MultiPoint multiPoint;
            LineString lineString; 
            
        	if (collectionType == "points") {
            	multiPoint = geometryFactory.createMultiPoint(animalTrack.coordinates.toArray(new Coordinate[] {}));
                featureBuilder.set("track",multiPoint);
            } else if (collectionType == "lines") {
            	lineString = geometryFactory.createLineString(animalTrack.coordinates.toArray(new Coordinate[] {}));
                featureBuilder.set("track",lineString);
            }
            featureBuilder.set("fromDate",animalTrack.fromDate);
            featureBuilder.set("toDate",animalTrack.toDate);
            featureBuilder.set("animalId", animalTrack.animal.getId());
            featureBuilder.set("projectAnimalId", animalTrack.animal.getProjectAnimalId());
            featureBuilder.set("animalName",animalTrack.animal.getAnimalName());
            featureBuilder.set("startPoint", geometryFactory.createPoint(animalTrack.startPoint));
            featureBuilder.set("endPoint", geometryFactory.createPoint(animalTrack.endPoint));
            SimpleFeature simpleFeature = featureBuilder.buildFeature(animalTrack.animal.getId().toString());
            collection.add(simpleFeature);
        }
        return collection;
    }


    
    private  SimpleFeatureCollection buildAllProjectsFeatureCollection() {
        List<Project> projectList = projectDao.getPublishedProjects();

        String namespaceURI = OzTrackApplication.getApplicationContext().getUriPrefix();
        SimpleFeatureCollection collection = FeatureCollections.newCollection();

        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Project");
        simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
        int srid = 4326;// = projectList.get(0).getBoundingBox().getSRID();
        
        for (Project project : projectList) {
        	if (project.getBoundingBox() != null) {
        		srid = project.getBoundingBox().getSRID();
        	}
        }
        
        simpleFeatureTypeBuilder.add("projectCentroid", Point.class, srid);
        simpleFeatureTypeBuilder.add("projectId", String.class);
        simpleFeatureTypeBuilder.add("projectTitle", String.class);
        simpleFeatureTypeBuilder.add("projectDescription", String.class);
        simpleFeatureTypeBuilder.add("firstDetectionDate",String.class);
        simpleFeatureTypeBuilder.add("lastDetectionDate",String.class);
        simpleFeatureTypeBuilder.add("spatialCoverageDescr", String.class);
        simpleFeatureTypeBuilder.add("speciesCommonName", String.class);

        SimpleFeatureType simpleFeatureType = simpleFeatureTypeBuilder.buildFeatureType();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(simpleFeatureType);
        
        for (Project project : projectList) {
        	if (project.getBoundingBox() != null) {
        		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        	
        		featureBuilder.set("projectCentroid",project.getBoundingBox().getCentroid());
        		featureBuilder.set("projectId", project.getId().toString());
        		featureBuilder.set("projectTitle", project.getTitle());
	        	featureBuilder.set("projectDescription", project.getDescription());
	        	featureBuilder.set("firstDetectionDate", sdf.format(project.getFirstDetectionDate()));
	        	featureBuilder.set("lastDetectionDate", sdf.format(project.getLastDetectionDate()));
	        	featureBuilder.set("spatialCoverageDescr", project.getSpatialCoverageDescr());
	        	featureBuilder.set("speciesCommonName", project.getSpeciesCommonName());
	        	
	        	 SimpleFeature simpleFeature = featureBuilder.buildFeature(project.getId().toString());
	             collection.add(simpleFeature);
    	
        	}
        	else {
        		logger.error("no bounding box in project: " + project.getId() + " " + project.getTitle());
        	}
        }

        return collection;
    }   
}
package org.oztrack.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;

public class WFSPositionFixSearchQueryView extends WFSSearchQueryView {
    private static class AnimalTrack {
        private Animal animal;
        private Date fromDate;
        private Date toDate;
        private List<Coordinate> coordinates;
    }
    
    // TODO: DAO should not appear in this layer.
    private PositionFixDao positionFixDao;
    
    private String featureTypeName = null;
    private String geometryAttributeName = null;
    private Class<?> geometryAttributeClass = null;
    
    public WFSPositionFixSearchQueryView(SearchQuery searchQuery, PositionFixDao positionFixDao) {
        super(searchQuery);
        this.positionFixDao = positionFixDao;
        switch (searchQuery.getMapQueryType()) {
        case POINTS:
            featureTypeName = "Detections";
            geometryAttributeName = "multiPoint";
            geometryAttributeClass = MultiPoint.class;
            break;
        case LINES:
            featureTypeName = "Trajectory";
            geometryAttributeName = "lineString";
            geometryAttributeClass = LineString.class;
            break;
        default:
            throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
        }
    }
    
    @Override
    protected SimpleFeatureCollection buildFeatureCollection() {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        Integer srid = positionFixList.isEmpty() ? null : positionFixList.get(0).getLocationGeometry().getSRID();
        SimpleFeatureType featureType = buildFeatureType(srid);
        HashMap<Long, AnimalTrack> trackByAnimal = buildAnimalTracks(positionFixList);
        return buildFeatureCollection(featureType, trackByAnimal);
    }

    public HashMap<Long, AnimalTrack> buildAnimalTracks(List<PositionFix> positionFixList) {
        HashMap<Long, AnimalTrack> trackByAnimal = new HashMap<Long, AnimalTrack>();
        for (PositionFix positionFix : positionFixList) {
            AnimalTrack track = trackByAnimal.get(positionFix.getAnimal().getId());
            Coordinate thisCoordinate = positionFix.getLocationGeometry().getCoordinate();
            if (track == null) {
                track = new AnimalTrack();
                track.animal = positionFix.getAnimal();
                track.fromDate = null;
                track.toDate = null;
                track.coordinates = new ArrayList<Coordinate>();
                trackByAnimal.put(positionFix.getAnimal().getId(), track);
            }
            track.coordinates.add(thisCoordinate);
            if ((track.fromDate == null) || positionFix.getDetectionTime().before(track.fromDate)) {
                track.fromDate = positionFix.getDetectionTime();
            }
            if ((track.toDate == null) || positionFix.getDetectionTime().after(track.toDate)) {
                track.toDate = positionFix.getDetectionTime();
            }
        }
        return trackByAnimal;
    }

    public SimpleFeatureType buildFeatureType(Integer srid) {
        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName(featureTypeName);
        simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
        simpleFeatureTypeBuilder.add("identifier", String.class);
        simpleFeatureTypeBuilder.add("animalId", String.class);
        simpleFeatureTypeBuilder.add("fromDate", Date.class);
        simpleFeatureTypeBuilder.add("toDate", Date.class);
        simpleFeatureTypeBuilder.add(geometryAttributeName, geometryAttributeClass, srid);
        SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
        return featureType;
    }
    
    private SimpleFeature buildFeature(SimpleFeatureType featureType, AnimalTrack track, String identifier, Object trackObject) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("identifier", identifier);
        featureBuilder.set("animalId", track.animal.getId());
        featureBuilder.set("fromDate", track.fromDate);
        featureBuilder.set("toDate", track.toDate);
        featureBuilder.set(geometryAttributeName, trackObject);
        String featureId = track.animal.getId().toString();
        if (identifier != null) {
            featureId += "-" + identifier;
        }
        return featureBuilder.buildFeature(featureId);
    }

    public SimpleFeatureCollection buildFeatureCollection(SimpleFeatureType featureType, HashMap<Long, AnimalTrack> trackByAnimal) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (AnimalTrack track: trackByAnimal.values()) {
            switch (searchQuery.getMapQueryType()) {
            case POINTS:
                MultiPoint points = geometryFactory.createMultiPoint(track.coordinates.toArray(new Coordinate[] {}));
                featureCollection.add(buildFeature(featureType, track, null, points));
                break;
            case LINES:
                LineString lines = geometryFactory.createLineString(track.coordinates.toArray(new Coordinate[] {}));
                featureCollection.add(buildFeature(featureType, track, null, lines));
                break;
            default:
                throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
            }
        }
        return featureCollection;
    }
}
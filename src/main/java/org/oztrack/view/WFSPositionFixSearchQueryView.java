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
import org.oztrack.data.model.types.MapQueryType;

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
        private Coordinate startPoint;
        private Coordinate endPoint;
    }
    
    // TODO: DAO should not appear in this layer.
    private PositionFixDao positionFixDao;
    
    public WFSPositionFixSearchQueryView(PositionFixDao positionFixDao) {
        this.positionFixDao = positionFixDao;
    }
    
    protected SimpleFeatureCollection buildFeatureCollection(SearchQuery searchQuery) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        Integer srid = positionFixList.isEmpty() ? null : positionFixList.get(0).getLocationGeometry().getSRID();
        SimpleFeatureType featureType = buildFeatureType(searchQuery.getMapQueryType(), srid);
        HashMap<Long, AnimalTrack> tracks = buildAnimalTracks(positionFixList);
        return buildFeatureCollection(featureType, searchQuery.getMapQueryType(), tracks);
    }

    public HashMap<Long, AnimalTrack> buildAnimalTracks(List<PositionFix> positionFixList) {
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
        return tracks;
    }

    public SimpleFeatureType buildFeatureType(MapQueryType mapQueryType, Integer srid) {
        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Track");
        simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
        simpleFeatureTypeBuilder.add("identifier", String.class);
        simpleFeatureTypeBuilder.add("animalId", String.class);
        simpleFeatureTypeBuilder.add("fromDate", Date.class);
        simpleFeatureTypeBuilder.add("toDate", Date.class);
        Class<?> trackClass = null;
        switch (mapQueryType) {
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
                throw new RuntimeException("Unsupported map query type: " + mapQueryType);
        }
        simpleFeatureTypeBuilder.add("track", trackClass, srid);
        SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
        return featureType;
    }
    
    private SimpleFeature buildFeature(SimpleFeatureType featureType, AnimalTrack animalTrack, String identifier, Object trackObject) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("identifier", identifier);
        featureBuilder.set("animalId", animalTrack.animal.getId());
        featureBuilder.set("fromDate", animalTrack.fromDate);
        featureBuilder.set("toDate", animalTrack.toDate);
        featureBuilder.set("track", trackObject);
        String featureId = animalTrack.animal.getId().toString();
        if (identifier != null) {
            featureId += "-" + identifier;
        }
        return featureBuilder.buildFeature(featureId);
    }

    public SimpleFeatureCollection buildFeatureCollection(SimpleFeatureType featureType, MapQueryType mapQueryType, HashMap<Long, AnimalTrack> tracks) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (AnimalTrack animalTrack: tracks.values()) {
            switch (mapQueryType) {
            case POINTS:
                MultiPoint points = geometryFactory.createMultiPoint(animalTrack.coordinates.toArray(new Coordinate[] {}));
                featureCollection.add(buildFeature(featureType, animalTrack, null, points));
                break;
            case LINES:
                LineString lines = geometryFactory.createLineString(animalTrack.coordinates.toArray(new Coordinate[] {}));
                featureCollection.add(buildFeature(featureType, animalTrack, null, lines));
                break;
            case START_END:
                MultiPoint startPoint = geometryFactory.createMultiPoint(new Coordinate[] {animalTrack.startPoint});
                MultiPoint endPoint = geometryFactory.createMultiPoint(new Coordinate[] {animalTrack.endPoint});
                featureCollection.add(buildFeature(featureType, animalTrack, "start", startPoint));
                featureCollection.add(buildFeature(featureType, animalTrack, "end", endPoint));
                break;
            default:
                throw new RuntimeException("Unsupported map query type: " + mapQueryType);
            }
        }
        return featureCollection;
    }
}
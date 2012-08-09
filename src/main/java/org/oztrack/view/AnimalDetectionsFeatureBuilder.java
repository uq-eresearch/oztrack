package org.oztrack.view;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.Constants;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;

public class AnimalDetectionsFeatureBuilder {
    private static class AnimalDetections {
        private Animal animal;
        private List<Coordinate> coordinates;
    }
    
    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    private final List<PositionFix> positionFixList;
    
    public AnimalDetectionsFeatureBuilder(List<PositionFix> positionFixList) {
        this.positionFixList = positionFixList;
    }
    
    public SimpleFeatureCollection buildFeatureCollection() {
        List<AnimalDetections> animalDetectionsList = buildAnimalDetectionsList(positionFixList);
        SimpleFeatureType featureType = buildFeatureType(4326);
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (AnimalDetections animalDetections : animalDetectionsList) {
            featureCollection.add(buildFeature(featureType, animalDetections));
        }
        return featureCollection;
    }

    private static List<AnimalDetections> buildAnimalDetectionsList(List<PositionFix> positionFixList) {
        List<AnimalDetections> animalDetectionsList = new ArrayList<AnimalDetections>();
        AnimalDetections animalDetections = null;
        for (PositionFix positionFix : positionFixList) {
            if ((animalDetections == null) || (animalDetections.animal.getId() != positionFix.getAnimal().getId())) {
                animalDetections = new AnimalDetections();
                animalDetections.animal = positionFix.getAnimal();
                animalDetections.coordinates = new ArrayList<Coordinate>();
                animalDetectionsList.add(animalDetections);
            }
            animalDetections.coordinates.add(positionFix.getLocationGeometry().getCoordinate());
        }
        return animalDetectionsList;
    }

    private SimpleFeatureType buildFeatureType(Integer srid) {
        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Detections");
        simpleFeatureTypeBuilder.setNamespaceURI(Constants.namespaceURI);
        simpleFeatureTypeBuilder.add("animalId", String.class);
        simpleFeatureTypeBuilder.add("multiPoint", MultiPoint.class, srid);
        SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
        return featureType;
    }
    
    private SimpleFeature buildFeature(SimpleFeatureType featureType, AnimalDetections animalDetections) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("animalId", animalDetections.animal.getId());
        featureBuilder.set("multiPoint", geometryFactory.createMultiPoint(animalDetections.coordinates.toArray(new Coordinate[] {})));
        return featureBuilder.buildFeature(animalDetections.animal.getId().toString());
    }
}
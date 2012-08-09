package org.oztrack.view;

import java.util.ArrayList;
import java.util.Date;
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
import com.vividsolutions.jts.geom.LineString;

public class AnimalTrajectoryFeatureBuilder {
    private static class AnimalTrajectory {
        private Animal animal;
        private Date fromDate;
        private Date toDate;
        private List<Coordinate> coordinates;
    }
    
    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    private final List<PositionFix> positionFixList;
    
    public AnimalTrajectoryFeatureBuilder(List<PositionFix> positionFixList) {
        this.positionFixList = positionFixList;
    }
    
    public SimpleFeatureCollection buildFeatureCollection() {
        List<AnimalTrajectory> animalTrajectories = buildAnimalTrajectories(positionFixList);
        SimpleFeatureType featureType = buildFeatureType(4326);
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (AnimalTrajectory animalTrajectory : animalTrajectories) {
            featureCollection.add(buildFeature(featureType, animalTrajectory));
        }
        return featureCollection;
    }

    private List<AnimalTrajectory> buildAnimalTrajectories(List<PositionFix> positionFixList) {
        List<AnimalTrajectory> animalTrajectories = new ArrayList<AnimalTrajectory>();
        AnimalTrajectory animalTrajectory = null;
        for (PositionFix positionFix : positionFixList) {
            if ((animalTrajectory == null) || (animalTrajectory.animal.getId() != positionFix.getAnimal().getId())) {
                animalTrajectory = new AnimalTrajectory();
                animalTrajectory.animal = positionFix.getAnimal();
                animalTrajectory.fromDate = positionFix.getDetectionTime();
                animalTrajectory.toDate = null;
                animalTrajectory.coordinates = new ArrayList<Coordinate>();
                animalTrajectories.add(animalTrajectory);
            }
            animalTrajectory.coordinates.add(positionFix.getLocationGeometry().getCoordinate());
            animalTrajectory.toDate = positionFix.getDetectionTime();
        }
        return animalTrajectories;
    }

    private SimpleFeatureType buildFeatureType(Integer srid) {
        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Trajectory");
        simpleFeatureTypeBuilder.setNamespaceURI(Constants.namespaceURI);
        simpleFeatureTypeBuilder.add("animalId", String.class);
        simpleFeatureTypeBuilder.add("fromDate", Date.class);
        simpleFeatureTypeBuilder.add("toDate", Date.class);
        simpleFeatureTypeBuilder.add("lineString", LineString.class, srid);
        SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
        return featureType;
    }
    
    private SimpleFeature buildFeature(SimpleFeatureType featureType, AnimalTrajectory animalTrajectory) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("animalId", animalTrajectory.animal.getId());
        featureBuilder.set("fromDate", animalTrajectory.fromDate);
        featureBuilder.set("toDate", animalTrajectory.toDate);
        LineString lineString = null;
        if (animalTrajectory.coordinates.size() >= 2) {
            lineString = geometryFactory.createLineString(animalTrajectory.coordinates.toArray(new Coordinate[] {}));
        }
        featureBuilder.set("lineString", lineString);
        return featureBuilder.buildFeature(animalTrajectory.animal.getId().toString());
    }
}
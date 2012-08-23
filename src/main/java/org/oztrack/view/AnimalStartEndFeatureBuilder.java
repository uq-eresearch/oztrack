package org.oztrack.view;

import java.util.HashMap;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.Constants;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;

import com.vividsolutions.jts.geom.Point;

public class AnimalStartEndFeatureBuilder {
    private static class AnimalStartEnd {
        private Animal animal;
        private PositionFix startPositionFix;
        private PositionFix endPositionFix;
    }

    private final List<PositionFix> positionFixList;

    public AnimalStartEndFeatureBuilder(List<PositionFix> positionFixList) {
        this.positionFixList = positionFixList;
    }

    public SimpleFeatureCollection buildFeatureCollection() {
        Integer srid = positionFixList.isEmpty() ? null : positionFixList.get(0).getLocationGeometry().getSRID();
        SimpleFeatureType featureType = buildFeatureType(srid);
        HashMap<Long, AnimalStartEnd> startEndByAnimal = buildAnimalStartEnds(positionFixList);
        return buildFeatureCollection(featureType, startEndByAnimal);
    }

    private HashMap<Long, AnimalStartEnd> buildAnimalStartEnds(List<PositionFix> positionFixList) {
        HashMap<Long, AnimalStartEnd> startEndByAnimal = new HashMap<Long, AnimalStartEnd>();
        for (PositionFix positionFix : positionFixList) {
            AnimalStartEnd startEnd = startEndByAnimal.get(positionFix.getAnimal().getId());
            if (startEnd == null) {
                startEnd = new AnimalStartEnd();
                startEnd.animal = positionFix.getAnimal();
                startEnd.startPositionFix = null;
                startEnd.endPositionFix = null;
                startEndByAnimal.put(positionFix.getAnimal().getId(), startEnd);
            }
            if ((startEnd.startPositionFix == null) || positionFix.getDetectionTime().before(startEnd.startPositionFix.getDetectionTime())) {
                startEnd.startPositionFix = positionFix;
            }
            if ((startEnd.endPositionFix == null) || positionFix.getDetectionTime().after(startEnd.endPositionFix.getDetectionTime())) {
                startEnd.endPositionFix = positionFix;
            }
        }
        return startEndByAnimal;
    }

    private SimpleFeatureType buildFeatureType(Integer srid) {
        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("StartEnd");
        simpleFeatureTypeBuilder.setNamespaceURI(Constants.namespaceURI);
        simpleFeatureTypeBuilder.add("animalId", Long.class);
        simpleFeatureTypeBuilder.add("identifier", String.class);
        simpleFeatureTypeBuilder.add("point", Point.class, srid);
        SimpleFeatureType featureType = simpleFeatureTypeBuilder.buildFeatureType();
        return featureType;
    }

    private SimpleFeature buildFeature(SimpleFeatureType featureType, Animal animal, String identifier, Point point) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("animalId", animal.getId());
        featureBuilder.set("identifier", identifier);
        featureBuilder.set("point", point);
        String featureId = animal.getId().toString();
        if (identifier != null) {
            featureId += "-" + identifier;
        }
        return featureBuilder.buildFeature(featureId);
    }

    private SimpleFeatureCollection buildFeatureCollection(SimpleFeatureType featureType, HashMap<Long, AnimalStartEnd> startEndByAnimal) {
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (AnimalStartEnd startEnd: startEndByAnimal.values()) {
            featureCollection.add(buildFeature(featureType, startEnd.animal, "start", startEnd.startPositionFix.getLocationGeometry()));
            featureCollection.add(buildFeature(featureType, startEnd.animal, "end", startEnd.endPositionFix.getLocationGeometry()));
        }
        return featureCollection;
    }
}
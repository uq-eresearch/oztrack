package org.oztrack.view;

import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.Constants;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisResultFeature;
import org.oztrack.data.model.types.AnalysisResultAttributeType;

import com.vividsolutions.jts.geom.MultiPolygon;

public class AnalysisResultFeatureBuilder {
    private final Analysis analysis;

    public AnalysisResultFeatureBuilder(Analysis analysis) {
        this.analysis = analysis;
    }

    public SimpleFeatureCollection buildFeatureCollection() {
        Set<AnalysisResultFeature> resultFeatures = analysis.getResultFeatures();
        SimpleFeatureType featureType = buildFeatureType(4326);
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        for (AnalysisResultFeature resultFeature : resultFeatures) {
            featureCollection.add(buildFeature(featureType, resultFeature));
        }
        return featureCollection;
    }

    private SimpleFeatureType buildFeatureType(Integer srid) {
        SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        featureTypeBuilder.setName(analysis.getAnalysisType().name());
        featureTypeBuilder.setNamespaceURI(Constants.namespaceURI);
        featureTypeBuilder.add("animalId", Long.class);
        featureTypeBuilder.add("animalName", String.class);
        for (AnalysisResultAttributeType resultAttributeType : analysis.getAnalysisType().getResultAttributeTypes()) {
            featureTypeBuilder.add(resultAttributeType.getIdentifier(), resultAttributeType.getDataTypeClass());
        }
        featureTypeBuilder.add("geometry", MultiPolygon.class, srid);
        SimpleFeatureType featureType = featureTypeBuilder.buildFeatureType();
        return featureType;
    }

    private SimpleFeature buildFeature(SimpleFeatureType featureType, AnalysisResultFeature resultFeature) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("animalId", resultFeature.getAnimal().getId());
        featureBuilder.set("animalName", resultFeature.getAnimal().getAnimalName());
        for (AnalysisResultAttributeType resultAttributeType : analysis.getAnalysisType().getResultAttributeTypes()) {
            Object value = resultFeature.getAttribute(resultAttributeType.getIdentifier()).getValue();
            featureBuilder.set(resultAttributeType.getIdentifier(), value);
        }
        featureBuilder.set("geometry", resultFeature.getGeometry());
        String featureId = resultFeature.getAnimal().getId().toString();
        return featureBuilder.buildFeature(featureId);
    }
}

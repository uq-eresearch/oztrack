package org.oztrack.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.Constants;
import org.oztrack.data.model.Project;

import com.vividsolutions.jts.geom.Point;

public class ProjectsFeatureBuilder {
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

    private final List<Project> projectList;
    private final HashMap<Long, Range<Date>> projectDetectionDateRangeMap;
    private final HashMap<Long, Point> projectCentroidMap;

    public ProjectsFeatureBuilder(
        List<Project> projectList,
        HashMap<Long, Range<Date>> projectDetectionDateRangeMap,
        HashMap<Long, Point> projectCentroidMap
    ) {
        this.projectList = projectList;
        this.projectDetectionDateRangeMap = projectDetectionDateRangeMap;
        this.projectCentroidMap = projectCentroidMap;
    }

    public SimpleFeatureCollection buildFeatureCollection() {
        SimpleFeatureType featureType = buildFeatureType();
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (Project project : projectList) {
            if (projectCentroidMap.get(project.getId()) == null) {
                continue;
            }
            SimpleFeature feature = buildFeature(featureType, project);
            featureCollection.add(feature);
        }
        return featureCollection;
    }

    private SimpleFeatureType buildFeatureType() {
        SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
        featureTypeBuilder.setName("Project");
        featureTypeBuilder.setNamespaceURI(Constants.namespaceURI);
        featureTypeBuilder.add("projectCentroid", Point.class, 4326);
        featureTypeBuilder.add("crosses180", Boolean.class);
        featureTypeBuilder.add("projectId", String.class);
        featureTypeBuilder.add("projectTitle", String.class);
        featureTypeBuilder.add("projectDescription", String.class);
        featureTypeBuilder.add("firstDetectionDate",String.class);
        featureTypeBuilder.add("lastDetectionDate",String.class);
        featureTypeBuilder.add("spatialCoverageDescr", String.class);
        featureTypeBuilder.add("speciesScientificName", String.class);
        featureTypeBuilder.add("speciesCommonName", String.class);
        featureTypeBuilder.add("access", String.class);
        return featureTypeBuilder.buildFeatureType();
    }

    private SimpleFeature buildFeature(SimpleFeatureType featureType, Project project) {
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        featureBuilder.set("projectCentroid", projectCentroidMap.get(project.getId()));
        featureBuilder.set("crosses180", project.getCrosses180());
        featureBuilder.set("projectId", project.getId().toString());
        featureBuilder.set("projectTitle", project.getTitle());
        featureBuilder.set("projectDescription", project.getDescription());
        Range<Date> projectDetectionDateRange = projectDetectionDateRangeMap.get(project.getId());
        featureBuilder.set("firstDetectionDate", sdf.format(projectDetectionDateRange.getMinimum()));
        featureBuilder.set("lastDetectionDate", sdf.format(projectDetectionDateRange.getMaximum()));
        featureBuilder.set("spatialCoverageDescr", project.getSpatialCoverageDescr());
        featureBuilder.set("speciesScientificName", project.getSpeciesScientificName());
        featureBuilder.set("speciesCommonName", project.getSpeciesCommonName());
        featureBuilder.set("access", project.getAccess().name());
        return featureBuilder.buildFeature(project.getId().toString());
    }
}

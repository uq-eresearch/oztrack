package org.oztrack.view;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final Log logger = LogFactory.getLog(getClass());
    private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

    private final List<Project> projectList;

    public ProjectsFeatureBuilder(List<Project> projectList) {
        this.projectList = projectList;
    }

    public SimpleFeatureCollection buildFeatureCollection() {
        SimpleFeatureType featureType = buildFeatureType();
        SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
        for (Project project : projectList) {
            if (project.getBoundingBox() == null) {
                logger.error("No bounding box for project " + project.getId() + " (" + project.getTitle() + ")");
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
        featureTypeBuilder.add("projectId", String.class);
        featureTypeBuilder.add("projectTitle", String.class);
        featureTypeBuilder.add("projectDescription", String.class);
        featureTypeBuilder.add("firstDetectionDate",String.class);
        featureTypeBuilder.add("lastDetectionDate",String.class);
        featureTypeBuilder.add("spatialCoverageDescr", String.class);
        featureTypeBuilder.add("speciesCommonName", String.class);
        featureTypeBuilder.add("global", Boolean.class);
        return featureTypeBuilder.buildFeatureType();
    }

    private SimpleFeature buildFeature(SimpleFeatureType featureType, Project project) {
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
        return featureBuilder.buildFeature(project.getId().toString());
    }
}

package org.oztrack.view;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.xml.Encoder;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.view.AbstractView;

import com.vividsolutions.jts.geom.Point;

public class KMLExportView extends AbstractView{
    private final Project project;
    private final List<PositionFix> positionFixList;

    public KMLExportView(Project project, List<PositionFix> positionFixList) {
        this.project = project;
        this.positionFixList = positionFixList;
    }

    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        Encoder encoder = new Encoder(new KMLConfiguration());
        encoder.setIndenting(true);

        String fileName = "animal.kml";
        response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");

        if (project != null) {
            collection = this.buildPointsFeatureCollection();
            encoder.encode(collection,KML.kml, response.getOutputStream());
        }
    }

    private SimpleFeatureCollection buildPointsFeatureCollection() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:m:s");
        int count = 1;

        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("PositionFix");
        sftb.add("name",String.class);
        sftb.add("description",String.class);
        sftb.add("location", Point.class);
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(sftb.buildFeatureType());

        for(PositionFix positionFix : positionFixList) {
            String nameField = sdf.format(positionFix.getDetectionTime());
            String descriptionField = "Animal Id: "
                                    + positionFix.getAnimal().getProjectAnimalId()
                                    + "<br> Name: "
                                    + positionFix.getAnimal().getAnimalName()
                                    + "<br> Timestamp: "
                                    + sdf.format(positionFix.getDetectionTime());

            featureBuilder.add(nameField);
            featureBuilder.add(descriptionField);
            featureBuilder.add(positionFix.getLocationGeometry());
            collection.add(featureBuilder.buildFeature(Integer.toString(count)));
            count++;
        }
        return collection;
    }
}
package org.oztrack.view;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.GML;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.util.RServeInterface;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 3/08/11
 * Time: 2:36 PM
 */
public class WFSMapQueryView extends AbstractView {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        SearchQuery searchQuery;
        File kmlFile = null;

        if (model != null) {
            logger.debug("Resolving ajax request view ");
            searchQuery = (SearchQuery) model.get("searchQuery");

            if (searchQuery.getProject() != null) {

                List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao().getProjectPositionFixList(searchQuery);

                if (!positionFixList.isEmpty()) {
                    String namespaceURI = OzTrackApplication.getApplicationContext().getUriPrefix();

                    SimpleFeatureCollection collection = FeatureCollections.newCollection();
                    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

                    SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
                    simpleFeatureTypeBuilder.setName("PositionFix");
                    simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
                    int srid = positionFixList.get(0).getLocationGeometry().getSRID();
                    simpleFeatureTypeBuilder.add("location", Point.class, srid);
                    simpleFeatureTypeBuilder.add("detectionTime", Date.class);
                    simpleFeatureTypeBuilder.add("animalId",String.class);
                    simpleFeatureTypeBuilder.add("animalName",String.class);
                    SimpleFeatureType simpleFeatureType = simpleFeatureTypeBuilder.buildFeatureType();

                    for(PositionFix positionFix : positionFixList) {
                        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(simpleFeatureType);
                        featureBuilder.add(positionFix.getLocationGeometry());
                        featureBuilder.set("location", positionFix.getLocationGeometry());
                        featureBuilder.set("detectionTime",positionFix.getDetectionTime());
                        featureBuilder.set("animalId",positionFix.getAnimal().getProjectAnimalId());
                        featureBuilder.set("animalName",positionFix.getAnimal().getAnimalName());
                        SimpleFeature simpleFeature = featureBuilder.buildFeature(positionFix.getId().toString());
                        collection.add(simpleFeature);
                    }

                    response.setContentType("text/xml");
                    GML encoder = new GML(GML.Version.WFS1_0);
                    encoder.setNamespace("PositionFix", namespaceURI);
                    encoder.encode(response.getOutputStream(), collection);
                }
            }
        }

    }
}

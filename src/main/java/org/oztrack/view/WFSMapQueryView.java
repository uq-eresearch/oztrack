package org.oztrack.view;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.GML;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.wfs.v1_1.WFSConfiguration;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.util.RServeInterface;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

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
        String namespaceURI = OzTrackApplication.getApplicationContext().getUriPrefix();

        if (model != null) {

            logger.debug("Resolving ajax request view ");
            searchQuery = (SearchQuery) model.get("searchQuery");

            if (searchQuery.getProject() != null) {

                //List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao().getProjectPositionFixList(searchQuery);

                //if (!positionFixList.isEmpty()) {

                SimpleFeatureCollection collection = FeatureCollections.newCollection();

                    // Would like to supply GMLConfiguration.NO_FEATURE_BOUNDS to the encoder;
                    // but not possible with the GML class - would need use Encoder directly
                    //**GML encoder = new GML(GML.Version.WFS1_0);


                WFSConfiguration wfsConfiguration = new org.geotools.wfs.v1_1.WFSConfiguration();
                wfsConfiguration.getProperties().add(GMLConfiguration.NO_FEATURE_BOUNDS);

                Encoder e = new Encoder(wfsConfiguration);

                e.setIndenting(true);
                FeatureCollectionType featureCollectionType = WfsFactory.eINSTANCE
                        .createFeatureCollectionType();


                    switch (searchQuery.getMapQueryType()) {
                        case ALL_POINTS:
                            //collection = this.buildPointsFeatureCollection(searchQuery);
                            //**encoder.setNamespace("PositionFix", namespaceURI);


                            e.getNamespaces().declarePrefix("PositionFix", namespaceURI);
                            featureCollectionType.getFeature().add(collection);


                            break;
                        case ALL_LINES:
                        case LINES:
                            collection = this.buildLinesFeatureCollection(searchQuery);
                            //**encoder.setNamespace("Track", namespaceURI);

                            e.getNamespaces().declarePrefix("Track", namespaceURI);
                            featureCollectionType.getFeature().add(collection);

                            break;
                        default:
                            break;
                    }

                    response.setContentType("text/xml");
                    response.setHeader("Content-Encoding", "gzip");
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
                    //encoder.encode(gzipOutputStream, collection);
                    e.encode(featureCollectionType, org.geotools.wfs.WFS.FeatureCollection, gzipOutputStream);

                gzipOutputStream.close();

            }
        }

    }

/*
    private SimpleFeatureCollection buildPointsFeatureCollection(SearchQuery searchQuery) {

        List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao().getProjectPositionFixList(searchQuery);
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

        return collection;

    }
*/
    private static class AnimalTrack {
            private Animal animal;
            private Date fromDate;
            private Date toDate;
            private List<Coordinate> coordinates;
            private Coordinate startPoint;
            private Coordinate endPoint;
    }

    private SimpleFeatureCollection buildLinesFeatureCollection(SearchQuery searchQuery) {

        List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao().getProjectPositionFixList(searchQuery);

        String namespaceURI = OzTrackApplication.getApplicationContext().getUriPrefix();

        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("Track");
        simpleFeatureTypeBuilder.setNamespaceURI(namespaceURI);
        int srid = positionFixList.get(0).getLocationGeometry().getSRID();
        
        simpleFeatureTypeBuilder.add("track", LineString.class, srid);
        simpleFeatureTypeBuilder.add("fromDate", Date.class);
        simpleFeatureTypeBuilder.add("toDate", Date.class);
        simpleFeatureTypeBuilder.add("animalId",String.class);
        simpleFeatureTypeBuilder.add("animalName",String.class);
        simpleFeatureTypeBuilder.add("startPoint", Point.class, srid);
        simpleFeatureTypeBuilder.add("endPoint", Point.class, srid);

        SimpleFeatureType simpleFeatureType = simpleFeatureTypeBuilder.buildFeatureType();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(simpleFeatureType);
        HashMap<Long, AnimalTrack> tracks = new HashMap<Long, AnimalTrack>();
        
        for (PositionFix positionFix : positionFixList) {

            AnimalTrack thisTrack = tracks.get(positionFix.getAnimal().getId());
            Coordinate thisCoordinate = positionFix.getLocationGeometry().getCoordinate();
            
            if (thisTrack == null) {

                thisTrack = new AnimalTrack();
                thisTrack.animal = positionFix.getAnimal();
                thisTrack.fromDate = positionFix.getDetectionTime();
                thisTrack.toDate = positionFix.getDetectionTime();
                thisTrack.coordinates = new ArrayList<Coordinate>();
                thisTrack.coordinates.add(thisCoordinate);
                thisTrack.startPoint = thisCoordinate;
                thisTrack.endPoint = thisCoordinate;
                tracks.put(positionFix.getAnimal().getId(),thisTrack);

            } else {

                if (positionFix.getDetectionTime().before(thisTrack.fromDate)) {
                    thisTrack.fromDate = positionFix.getDetectionTime();
                    thisTrack.startPoint = thisCoordinate;
                }
                if (positionFix.getDetectionTime().after(thisTrack.toDate)) {
                    thisTrack.toDate = positionFix.getDetectionTime();
                    thisTrack.endPoint = thisCoordinate;
                }
                thisTrack.coordinates.add(thisCoordinate);

            }
        }

        for (AnimalTrack animalTrack: tracks.values()) {

            LineString lineString = geometryFactory.createLineString(animalTrack.coordinates.toArray(new Coordinate[] {}));
            featureBuilder.set("track",lineString);
            featureBuilder.set("fromDate",animalTrack.fromDate);
            featureBuilder.set("toDate",animalTrack.toDate);
            featureBuilder.set("animalId", animalTrack.animal.getProjectAnimalId());
            featureBuilder.set("animalName",animalTrack.animal.getAnimalName());
            featureBuilder.set("startPoint", geometryFactory.createPoint(animalTrack.startPoint));
            featureBuilder.set("endPoint", geometryFactory.createPoint(animalTrack.endPoint));
            SimpleFeature simpleFeature = featureBuilder.buildFeature(animalTrack.animal.getId().toString());
            collection.add(simpleFeature);
        }
        return collection;

    }


}

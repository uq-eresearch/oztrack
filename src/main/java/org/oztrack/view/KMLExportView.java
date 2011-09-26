package org.oztrack.view;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.springframework.web.servlet.view.AbstractView;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class KMLExportView extends AbstractView{

	
	@Override
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
        
		SearchQuery searchQuery;
        SimpleFeatureCollection collection = FeatureCollections.newCollection();
        Encoder encoder = new Encoder(new KMLConfiguration());
        encoder.setIndenting(true);
        
		String fileName = "animal.kml";
		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
				
        if (model != null) {
            searchQuery = (SearchQuery) model.get("searchQuery");
            if (searchQuery.getProject() != null) {

            	collection = this.buildPointsFeatureCollection(searchQuery);
            	encoder.encode(collection,KML.kml, response.getOutputStream());
            	
            }    
        }	
	}
	
	
	
    private SimpleFeatureCollection buildPointsFeatureCollection(SearchQuery searchQuery) {

        List<PositionFix> positionFixList = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao().getProjectPositionFixList(searchQuery);
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

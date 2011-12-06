var map;
var projection900913;
var projection4326;
var projectBBoxOnStyle;
var projectBBoxOffStyle; 
var projectBBoxStyleMap;
var projectPointsOnStyle;
var projectPointsOffStyle; 
var projectPointsStyleMap;

var colours = [
               '#8DD3C7',
               '#FFFFB3',
               '#BEBADA',
               '#FB8072',
               '#80B1D3',
               '#FDB462',
               '#B3DE69',
               '#FCCDE5',
               '#D9D9D9',
               '#BC80BD',
               '#CCEBC5',
               '#FFED6F'
           ];


function initializeHomeMap() {

    var projection900913 = new OpenLayers.Projection('EPSG:900913');
    var projection4326 =  new OpenLayers.Projection("EPSG:4326");
    var mapOptions = {
       maxExtent: new OpenLayers.Bounds(
            -128 * 156543.0339,
            -128 * 156543.0339,
             128 * 156543.0339,
             128 * 156543.0339),
       maxResolution: 156543.0339,
       units: 'm',
       projection: projection900913,
       displayProjection: projection4326
    };
    map = new OpenLayers.Map('homeMap',mapOptions);
    var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
   // layerSwitcher.div = OpenLayers.Util.getElement('homeMapOptions');
    //layerSwitcher.roundedCorner = false;
    map.addControl(layerSwitcher);

    var gphy = new OpenLayers.Layer.Google(
                "Google Physical",
                {type: google.maps.MapTypeId.TERRAIN}
    );

    var gsat = new OpenLayers.Layer.Google(
                "Google Satellite",
                {type: google.maps.MapTypeId.SATELLITE}
    );
    
    initStyles();
    allProjectsLayer = new OpenLayers.Layer.Vector(
            "All Projects",{
            projection: projection4326,
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
               url:  "mapQueryWFS?queryType=ALL_PROJECTS",
               featureType: "Project",
               featureNS: "http://localhost:8080/",
               geometryName: "hello"
               }),
            styleMap: projectBBoxStyleMap,
            strategies: [new OpenLayers.Strategy.Fixed()],
            eventListeners: {
                loadend: function (e) {
            		//map.zoomToExtent(allProjectsLayer.getDataExtent(),false);
            		initProjectLayer(allProjectsLayer);
            	}
             }
            });
        

    map.addLayers([gsat,gphy, allProjectsLayer]);
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326,projection900913), 4);

}

function initProjectLayer(allProjectsLayer) {
	
	projectPointsLayer = new OpenLayers.Layer.Vector(
    		"Project Icons",{ 
    		  projection: projection4326
   			 ,styleMap: pointsStyleMap	
    		 });
	
	
	// add features
	for (var i in allProjectsLayer.features) {
        var feature = allProjectsLayer.features[i];
        if (feature.attributes.projectId) {

        	// create a centroid point for each bbox feature with the same colour.
        	var projectPoint = new OpenLayers.Feature.Vector(feature.geometry.getCentroid());
        	projectPoint.attributes = feature.attributes;
        	projectPointsLayer.addFeatures([projectPoint]);
        	
        }
	}
	map.addLayer(projectPointsLayer);

	for (var i in allProjectsLayer.features) {
        var feature = allProjectsLayer.features[i];
        if (feature.attributes.projectId) {
        	
        	
        	var projectClickControl = new OpenLayers.Control.SelectFeature([projectPointsLayer],{
				clickout: true,
				eventListeners: {
					featurehighlighted: function(e) {
						//alert(e.feature.attributes.projectTitle);
						map.addPopup(buildPopup(e));       	
					},
					featureunhighlighted: function(e) {
						e.feature.renderIntent="default";
						
					}
				}
			});
        	
        	map.addControl(projectClickControl);
            projectClickControl.activate();
        }	
	}
}

function buildPopup(e) {

	var f = e.feature;
	var layer = f.layer;
	var firstDate = f.attributes.firstDetectionDate;//.split(" ")[0];
	var lastDate = f.attributes.lastDetectionDate;//.split(" ")[0];
	var projectId = f.attributes.projectId;
	var popupHtml = "<div class='homeMapPopup'>" 
				  + "<h3>" + f.attributes.projectTitle + "</h3>"
				  + "<p><b>Species: </b><br>" + f.attributes.speciesCommonName + "</p>"
				  + "<p><b>Coverage: </b><br>" + f.attributes.spatialCoverageDescr + "</p>"
				  + "<p><b>Date Range: </b><br>" + firstDate + " to " + lastDate + "</p>"
				  + "<p><a href='projectdetailext?project_id=" + projectId + "'>more</a> ...</p>"
				  + "</div>";

	var popup = new OpenLayers.Popup.AnchoredBubble(
            projectId, 
            f.geometry.getBounds().getCenterLonLat(),
            null,
            popupHtml,
            null,
            true
        );
	popup.autoSize = true;
	popup.setBackgroundColor("#FBFEE9");
	popup.setOpacity("0.9");
	popup.closeOnMove = true;
	//popup.addCloseBox("closeBoxCb()");
	
	return popup;
}


function initStyles() {
	
    var wfsStyleContext = {
            getColour: function(feature) {
                var c = feature.attributes.projectId%colours.length;
                return colours[c];
            } };
    
    //------------------
	
    var projectBBoxTemplate = {
			strokeColor: "${getColour}",
			strokeWidth: 1,
			strokeOpacity: 1.0,
			fillColor: "${getColour}",
			fillOpacity: 0.5
			};

	projectBBoxOnStyle = new OpenLayers.Style(projectBBoxTemplate, {context: wfsStyleContext});
	projectBBoxOffStyle = {
			strokeOpacity: 0.0,
			fillOpacity: 0.0
	};
	
	projectBBoxStyleMap = new OpenLayers.StyleMap({
		"default":projectBBoxOffStyle,
		"temporary":projectBBoxOnStyle
	});
	
	//------------------

	var pointsDefaultTemplate = {
			pointRadius: 5,
			strokeColor: "#000000",
			strokeWidth: 1.5,
			strokeOpacity: 0.6,
			fillColor: "${getColour}",
			fillOpacity: 0.6
	};
	pointsDefaultStyle = new OpenLayers.Style(pointsDefaultTemplate, {context: wfsStyleContext});

	var pointsSelectTemplate = {
			pointRadius: 8,
			strokeColor: "#000000",
			strokeWidth: 2,
			strokeOpacity: 0.9,
			fillColor: "${getColour}",
			fillOpacity: 1.0
	};
	pointsSelectStyle = new OpenLayers.Style(pointsSelectTemplate, {context: wfsStyleContext});
	
	
	pointsTempStyle = {
			strokeOpacity: 0.0
	};
	
	pointsStyleMap = new OpenLayers.StyleMap({
		"default":pointsDefaultStyle,
		"temporary":pointsTempStyle,
		"select":pointsSelectStyle 
	});

	
}


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
    var map = new OpenLayers.Map('homeMap',mapOptions);
    var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
    layerSwitcher.div = OpenLayers.Util.getElement('homeMapOptions');
    layerSwitcher.roundedCorner = false;
    map.addControl(layerSwitcher);

    var gphy = new OpenLayers.Layer.Google(
                "Google Physical",
                {type: google.maps.MapTypeId.TERRAIN}
    );

    var gsat = new OpenLayers.Layer.Google(
                "Google Satellite",
                {type: google.maps.MapTypeId.SATELLITE}
    );

    map.addLayers([gsat,gphy]);
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326,projection900913), 4);

}

var map;
var allAnimalTracksLayer;
var allAnimalTracksLayerStyle;
var pointsLayer;
var startPointOnStyle;
var endPointOnStyle;
var pointsOffStyle;

var selectControl;
var projection900913 = new OpenLayers.Projection('EPSG:900913');
var projection4326 =  new OpenLayers.Projection("EPSG:4326");
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


function initializeProjectMap() {

    var projectId = $('#projectId').val();
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

    map = new OpenLayers.Map('projectMap',mapOptions);
    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.Scale());
    map.addControl(new OpenLayers.Control.ScaleLine());
    map.addControl(new OpenLayers.Control.NavToolbar());
    var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
    layerSwitcher.div = OpenLayers.Util.getElement('customLayerSwitcher');
    layerSwitcher.roundedCorner = false;
    map.addControl(layerSwitcher);

    var gphy = new OpenLayers.Layer.Google(
                "Google Physical",
                {type: google.maps.MapTypeId.TERRAIN}
    );
    var gsat = new OpenLayers.Layer.Google(
                "Google Satellite",
                {type: google.maps.MapTypeId.SATELLITE}
    );

    allAnimalTracksLayer = new OpenLayers.Layer.Vector(
        "All Animal Tracks",
        {strategies: [new OpenLayers.Strategy.BBOX()],
         eventListeners: {
            loadend: function (e) {
            	map.zoomToExtent(allAnimalTracksLayer.getDataExtent(),false);
            	updateAnimalStyles(this);
            	if (!pointsLayer) {createPointsLayer();}
            	createSelectControl();
        	}
         },
         projection: projection4326,
         protocol: new OpenLayers.Protocol.WFS.v1_1_0({
            url:  "mapQueryWFS?projectId=" + projectId + "&queryType=ALL_LINES",
            featureType: "Track",
            featureNS: "http://localhost:8080/",
            geometryName: "startPoint"
            })
        });

    map.addLayers([gsat,gphy,allAnimalTracksLayer]);
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326,projection900913), 4);
}

function updateAnimalStyles(linesLayer) {
    

//	var vectorLayers = getVectorLayers();

//    for (var l in vectorLayers) {
    	
//    	var layer = vectorLayers[l];
//    	var layerName = layer.name;
//	    if (layerName.indexOf("Animal Track") != -1 ) {	
	        
	        for (var key in linesLayer.features) {
		        var feature = linesLayer.features[key];
		        if (feature.attributes && feature.attributes.animalId) {
		                var colour = colours[feature.attributes.animalId % colours.length];
		                feature.style = {
		                        strokeColor: colour,
		                        strokeWidth: 1.5,
		                        label: feature.attributes.animalName,
		                        labelAlign: "rt",
		                        fontColor: "#ffffff",
		                        fontOpacity: 0.9,
		                        fontFamily: "Arial",
		                        fontSize: 12
		                }
		                $('#legend-colour-' + feature.attributes.animalId).attr('style', 'background-color: ' + colour + ';');
		                $('input[id=select-animal-' + feature.attributes.animalId + ']').attr('checked','checked');
		                var txt = "<br><b> Animal Track: </b>"
		                	 	+ "<br> Date From: " + feature.attributes.fromDate
			    		  		+ "<br> Date To: " + feature.attributes.toDate;
		    	    	var distance = feature.geometry.getGeodesicLength(map.projection);
		    	    	txt = txt +  "<br> Minimum Distance: " + Math.round(distance*1000)/1000 + "m";
	 
		                $('#animalInfo-'+ feature.attributes.animalId).append(txt);
		        }
	        }
	  //  }
	linesLayer.redraw();
    //}
}    

function createPointsLayer() {
	
	pointsLayer = new OpenLayers.Layer.Vector(
        		"Start and End Points",
        		{projection: projection4326});
		
	startPointOnStyle = {
		pointRadius: 2,
		fillColor: "#00CD00",
		strokeColor:"#00CD00",	
		fillOpacity: 0,
		strokeOpacity: 1,
		strokeWidth: 1.2,
	};
	
	endPointOnStyle = {
		pointRadius: 2,
		fillColor: "#CD0000",
		strokeColor:"#CD0000",	
		fillOpacity: 0,
		strokeOpacity: 1,
		strokeWidth: 1.2,
	};
	
	pointsOffStyle = {
		strokeOpacity: 0,
		fillOpacity: 0
	};
	
		
	for (var key in allAnimalTracksLayer.features) {
         var feature = allAnimalTracksLayer.features[key];
         if (feature.attributes) {
        	// add features
	         var startPoint = new OpenLayers.Feature.Vector(feature.geometry.components[0]);
	            startPoint.attributes = {animalId : feature.attributes.animalId,
					            		animalName : feature.attributes.animalName, 
					            		fromDate: feature.attributes.fromDate,
					            		pointName:	"start"};
	            startPoint.style = startPointOnStyle;
            var endPoint = new OpenLayers.Feature.Vector(feature.geometry.components[feature.geometry.components.length-1]);
	            endPoint.attributes = {animalId : feature.attributes.animalId,
					            		animalName : feature.attributes.animalName, 
					            		toDate: feature.attributes.toDate,
					            		pointName:	"end"};
	            endPoint.style = endPointOnStyle;
            pointsLayer.addFeatures([startPoint,endPoint]);
	         
         }
    }
	map.addLayer(pointsLayer);
}

function createPointsLayer(linesLayer) {
	
	
	pointsLayer = new OpenLayers.Layer.Vector(
        		"Start and End Points",
        		{projection: new OpenLayers.Projection("EPSG:4326")});
		
	startPointOnStyle = {
		pointRadius: 2,
		fillColor: "#00CD00",
		strokeColor:"#00CD00",	
		fillOpacity: 0,
		strokeOpacity: 1,
		strokeWidth: 1.2,
	};
	
	endPointOnStyle = {
		pointRadius: 2,
		fillColor: "#CD0000",
		strokeColor:"#CD0000",	
		fillOpacity: 0,
		strokeOpacity: 1,
		strokeWidth: 1.2,
	};
	
	pointsOffStyle = {
		strokeOpacity: 0,
		fillOpacity: 0
	};
	
		
	for (var key in allAnimalTracksLayer.features) {
         var feature = allAnimalTracksLayer.features[key];
         if (feature.attributes) {
        	// add features
	         var startPoint = new OpenLayers.Feature.Vector(feature.geometry.components[0]);
	            startPoint.attributes = {animalId : feature.attributes.animalId,
					            		animalName : feature.attributes.animalName, 
					            		fromDate: feature.attributes.fromDate,
					            		pointName:	"start"};
	            startPoint.style = startPointOnStyle;
            var endPoint = new OpenLayers.Feature.Vector(feature.geometry.components[feature.geometry.components.length-1]);
	            endPoint.attributes = {animalId : feature.attributes.animalId,
					            		animalName : feature.attributes.animalName, 
					            		toDate: feature.attributes.toDate,
					            		pointName:	"end"};
	            endPoint.style = endPointOnStyle;
            pointsLayer.addFeatures([startPoint,endPoint]);
	         
         }
    }
	map.addLayer(pointsLayer);
}

function createSelectControl() {

	//get vector layers from layerswitcher 
	var vectorLayers = new Array();
	for (var c in map.controls) {
		var control = map.controls[c];
		if (control.id.indexOf("LayerSwitcher") != -1) {
			for (var i=0; i < control.dataLayers.length; i++) {
				vectorLayers.push(control.dataLayers[i].layer);
			}
		}
	}
	
	var controlOptions = {
	        clickout: true,
	        eventListeners: {
	            featurehighlighted: function(e) {
					writeSelectControlText(e);
	            	},
	            featureunhighlighted: function(e) {
	            }
	        }
	};

	if (selectControl) {
		selectControl.initLayer(vectorLayers);
	} else {
		selectControl = new OpenLayers.Control.SelectFeature(vectorLayers, controlOptions);
	    map.addControl(selectControl);
	    selectControl.activate();
	}
}

function writeSelectControlText(e) {
	
	if ((e.feature.layer === allAnimalTracksLayer) || (e.feature.layer === pointsLayer)) {

		var txt="<b>Selected Feature: </b><br> Animal: " + e.feature.attributes.animalName
	    + "<br> Layer name: " + e.feature.layer.name;
		
	    if (e.feature.layer === allAnimalTracksLayer) {

	    	txt = txt + "<br> Date From: " + e.feature.attributes.fromDate
		    		  + "<br> Date To: " + e.feature.attributes.toDate;
	    	var distance = e.feature.geometry.getGeodesicLength(map.projection);
	    	txt = txt +  "<br> Minimum Distance: " + Math.round(distance*1000)/1000 + "m";
	    
	    } 
	    if (e.feature.layer === pointsLayer) {
	    
	    	if (typeof e.feature.attributes.fromDate != "undefined") {
	    		txt = txt + "<br> Start Point: " + e.feature.attributes.fromDate;
	    	}
	    	if (typeof e.feature.attributes.toDate != "undefined") {
	    		txt = txt + "<br> End Point: " + e.feature.attributes.toDate;
	    	}
	    }
	}
    
	$('#mapDescription').html(txt);
	
}

function zoomToTrack(animalId) {

	for (var key in allAnimalTracksLayer.features) {
	         var feature = allAnimalTracksLayer.features[key];
	         if (feature.attributes && animalId == feature.attributes.animalId) {
	        	map.zoomToExtent(feature.geometry.getBounds(),false);
	         }
	    }
}

function getVectorLayers() {
	//get vector layers from layerswitcher 
	var vectorLayers = new Array();
	for (var c in map.controls) {
		var control = map.controls[c];
		if (control.id.indexOf("LayerSwitcher") != -1) {
			for (var i=0; i < control.dataLayers.length; i++) {
				vectorLayers.push(control.dataLayers[i].layer);
			}
		}
	}
	return vectorLayers;
}


function toggleAnimalFeature(animalId, setVisible) {

    //line
	var lineStyle = {
            strokeColor: "#ffffff",
            strokeWidth: 1.5,
            label: "",
            labelAlign: "rt",
            fontColor: "#ffffff",
            fontOpacity: 0.9,
            fontFamily: "Arial",
            fontSize: 12,
            fillOpacity:1.0,
            strokeOpacity:1.0
    };
	
	var polygonStyle = {
		strokeColor: "FF0000",
		strokeWidth: 2,
		strokeOpacity: 1.0,
		fillOpacity: 0.0
	};
	
	var vectorLayers = getVectorLayers();

    for (var l in vectorLayers) {
    	var layer = vectorLayers[l];
    	var layerName = layer.name;
	        if (layerName.indexOf("Animal Track") != -1 ) {
        		for (var f in layer.features) {
	                var feature = layer.features[f];
	                if (feature.attributes.animalId == animalId) {
	                    if (setVisible) {
	                    	lineStyle.strokeColor = colours[feature.attributes.animalId % colours.length];
	                    	lineStyle.label = feature.attributes.animalName;
	                    	lineStyle.strokeOpacity = 1.0;
	                    } else {
	                    	lineStyle.strokeOpacity = 0.0;
	                    }
	                    feature.style = lineStyle;
	                    layer.redraw();
	                }
	            }
            } else if (layerName.indexOf("Start and End Points") != -1) {   
	            for (var f in layer.features) {
	                var feature = layer.features[f];
	                if (feature.attributes.animalId == animalId) {
	                    if (setVisible) {
	                    	if (feature.attributes.fromDate) {
	                    		feature.style = startPointOnStyle;
	                    	} else if (feature.attributes.toDate) {
	                    		feature.style = endPointOnStyle;
	                    	}
	                    } else {
	                    	feature.style = pointsOffStyle;
	                    }
	                    layer.redraw();
	                }
	            }
	        } else {
	        	//kml layer
	        	for (var f in layer.features) {
	        		var feature = layer.features[f];
	        		if (feature.attributes.id.value == animalId) {
	        			if (setVisible) {
	        				polygonStyle.strokeOpacity = 1.0;
	        			}else {
	        				polygonStyle.strokeOpacity = 0.0;
	        			}
	        			feature.style = polygonStyle;
	        			layer.redraw();
	        		}
	        	}
	        }   
     }

}

function addProjectMapLayer() {

    var projectId = $('#projectId').val();
    var dateFrom = $('input[id=fromDatepicker]').val();
    var dateTo=$('input[id=toDatepicker]').val();
    var queryType =$('input[name=mapQueryTypeSelect]:checked');
    var queryTypeDescription =  queryType.parent().next().text();
    
     
/*
    var data = '&dateFrom=' + dateFrom
     + '&dateTo=' + dateTo
     + '&queryType=' + queryType.val()
     + '&mapQueryTypeDescription=' + queryTypeDescription;
    
     alert("data : " + data);
*/
    if (queryType.val() != null) {
    
        var params = {projectId: projectId
	                 ,queryType: queryType.val()};
	    var layerName = queryTypeDescription;
	
	    if (dateFrom.length == 10) {
	        layerName = layerName + " from " + dateFrom;
	        params.dateFrom = dateFrom;
	    }
	
	    if (dateTo.length == 10) {
	        layerName = layerName + " to " + dateTo;
	        params.dateTo = dateTo;
	    }
	
	    if (queryType.val() == "LINES") {
	        addWFSLayer(layerName, params);
	    } else {
	        addKMLLayer(layerName,params);
	    }
	    
    } else {
	    alert("Please set a Layer Type.");
    }
        
}

function addKMLLayer(layerName, params) {

        var queryOverlay = new OpenLayers.Layer.Vector(
                layerName,
                {strategies: [new OpenLayers.Strategy.Fixed()],
                 eventListeners: {
	                 loadend: function (e) {
	                	updateAnimalInfoFromKML(layerName, e);
	            	 }
                 },
                	protocol: new OpenLayers.Protocol.HTTP(
                    {url: "mapQueryKML",
                     params: params,
                     format: new OpenLayers.Format.KML(
                        {extractStyles: true,
                         extractAttributes: true,
                         maxDepth: 2,
                         internalProjection: projection900913,
                         externalProjection: projection4326,
                         kmlns:"http://localhost:8080/"
                     })
                  })
        });


        var querySelectControl = new OpenLayers.Control.SelectFeature(
            [queryOverlay],
            {
                clickout: true,
                eventListeners: {
                    featurehighlighted: function(e) {
                        var txt="<b>Selected Feature: </b> "
                        + "<br> " + e.feature.layer.name
                        + "<br> Animal Id: " + e.feature.attributes.id.value
                        + "<br> Area: " + e.feature.attributes.area.value;
                        $('#mapDescription').html(txt);
                        //alert(e.feature.attributes.animalId );//+ " at " + e.feature.attributes.detectionTime);
                    },
                    featureunhighlighted: function(e) {
                    }
                }
            }
        );
        map.addLayer(queryOverlay);
 //       map.addControl(querySelectControl);
 //       querySelectControl.activate();
 //       queryOverlay.refresh();
}
                
function updateAnimalInfoFromKML(layerName, e) {
	
	var txt = "<br><b>" + layerName + "</b>" ;

	for (var f in e.object.features) {
		var feature = e.object.features[f];
		var area = feature.attributes.area.value;
		var txt = "<br><b>" + layerName + "</b>" 
				+ "<br> Area: " + 		Math.round(area*1000)/1000;
		$('#animalInfo-'+ feature.attributes.id.value).append(txt);
	}	
}


function addWFSLayer(layerName, params) {

        newWFSOverlay = new OpenLayers.Layer.Vector(
            layerName,
            {strategies: [new OpenLayers.Strategy.BBOX()],
             eventListeners: {
                loadend: function (e) {
                	map.zoomToExtent(newWFSOverlay.getDataExtent(),false);
                	var whatsThis = this;
                	updateAnimalStyles(this);
            	}
             },
             projection: projection4326,
             protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                url:  "mapQueryWFS",
                params:params,
                featureType: "Track",
                featureNS: "http://localhost:8080/"
                })
            });


        var newSelectControl = new OpenLayers.Control.SelectFeature(
            [newWFSOverlay],
            {
                clickout: true,
                eventListeners: {
                    featurehighlighted: function(e) {
                        var txt="<b>Selected Feature: </b><br> Animal: " + e.feature.attributes.animalName
                        + "<br> Date From: " + e.feature.attributes.fromDate
                        + "<br> Date To: " + e.feature.attributes.toDate;
                        $('#mapDescription').html(txt);
                        //alert(e.feature.attributes.animalId );//+ " at " + e.feature.attributes.detectionTime);
                    },
                    featureunhighlighted: function(e) {
                    }
                }
            }
        );

        map.addLayer(newWFSOverlay);
//        map.addControl(newSelectControl);
//        newSelectControl.activate();
//        newWFSOverlay.refresh();
        
}



function initializeSightingMap() {

    var australia = new google.maps.LatLng(-32,134);
    var centralAustralia = new google.maps.LatLng(-24.01, 135.01);
    var myOptions = {
                    zoom: 3,
                    center: australia,
                    mapTypeId: google.maps.MapTypeId.SATELLITE,
                    streetViewControl: false,
                    mapTypeControl: true,
                    mapTypeControlOptions: {
                            style: google.maps.MapTypeControlStyle.DEFAULT
                            },
                    zoomControl:true,
                    zoomControlOptions: {
                            style: google.maps.ZoomControlStyle.SMALL
                            },
                    animation: google.maps.Animation.BOUNCE

                    };

    var sightingMap = new google.maps.Map(document.getElementById("sightingMap"), myOptions);
    sightingMap.enableKeyDragZoom();

    var marker = new google.maps.Marker({
               position: centralAustralia,
               map:sightingMap,
               draggable: true
               //title:site[0],
               //zIndex:site[3],
               //html:site[4],
               //icon:image
    });

    $('#sightingLatitude').val(centralAustralia.lat());
    $('#sightingLongitude').val(centralAustralia.lng());

    google.maps.event.addListener(marker, 'drag', function() {
        var point = marker.getPosition();
 		sightingMap.setCenter(point);
 		$('#sightingLatitude').val(point.lat());
        $('#sightingLongitude').val(point.lng());
    });

    google.maps.event.addListener(sightingMap, 'dragend', function() {
        var point = sightingMap.getCenter();
        marker.setPosition(point);
 		$('#sightingLatitude').val(point.lat());
        $('#sightingLongitude').val(point.lng());
    });

    google.maps.event.addListener(sightingMap, 'zoom_changed', function() {
        var point = sightingMap.getCenter();
        marker.setPosition(point);
 		$('#sightingLatitude').val(point.lat());
        $('#sightingLongitude').val(point.lng());
    });



 }


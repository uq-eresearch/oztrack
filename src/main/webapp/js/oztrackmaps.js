
function initializeHomeMap() {


    var googleProjection = new OpenLayers.Projection('EPSG:900913');
    var kmlProjection =  new OpenLayers.Projection("EPSG:4326");
    var mapOptions = {
       maxExtent: new OpenLayers.Bounds(
            -128 * 156543.0339,
            -128 * 156543.0339,
             128 * 156543.0339,
             128 * 156543.0339),
       maxResolution: 156543.0339,
       units: 'm',
       projection: googleProjection,
       displayProjection: kmlProjection
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
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(kmlProjection,googleProjection), 4);

}

var map;
var linesLayer;
var linesLayerStyle;
var pointsLayer;
var startPointOnStyle;
var endPointOnStyle;
var pointsOffStyle;

var selectControl;
var googleProjection = new OpenLayers.Projection('EPSG:900913');
var kmlProjection =  new OpenLayers.Projection("EPSG:4326");
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
       projection: googleProjection,
       displayProjection: kmlProjection
    };

    map = new OpenLayers.Map('projectMap',mapOptions);
    map.addControl(new OpenLayers.Control.MousePosition());
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

    linesLayer = new OpenLayers.Layer.Vector(
        "Animal Tracks",
        {strategies: [new OpenLayers.Strategy.BBOX()],
         eventListeners: {
            loadend: function (e) {
            	map.zoomToExtent(linesLayer.getDataExtent(),false);
            	updateAnimalStyles();
            	if (!pointsLayer) {createPointsLayer();}
            	createSelectControl();
        	}
         },
         projection: new OpenLayers.Projection("EPSG:4326"),
         protocol: new OpenLayers.Protocol.WFS.v1_1_0({
            url:  "mapQueryWFS?projectId=" + projectId + "&queryType=ALL_LINES",
            featureType: "Track",
            featureNS: "http://localhost:8080/",
            geometryName: "startPoint"
            })
        });

    map.addLayers([gsat,gphy,linesLayer]);
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(kmlProjection,googleProjection), 4);
}

function updateAnimalStyles() {
    
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
	        }
    }
    linesLayer.redraw();
}

function createPointsLayer() {
	
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
	
		
	for (var key in linesLayer.features) {
         var feature = linesLayer.features[key];
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
					var distance = e.feature.geometry.getGeodesicLength(map.projection);
	                var txt="<b>Selected Feature: </b><br> Animal: " + e.feature.attributes.animalName
	                + "<br> Date From: " + e.feature.attributes.fromDate
	                + "<br> Date To: " + e.feature.attributes.toDate
	                + "<br> Minimum Distance: " + Math.round(distance*1000)/1000 + "m";
	                $('#mapDescription').html(txt);
	                //alert(e.feature.attributes.animalId );//+ " at " + e.feature.attributes.detectionTime);
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

function zoomToTrack(animalId) {

	for (var key in linesLayer.features) {
	         var feature = linesLayer.features[key];
	         if (feature.attributes && animalId == feature.attributes.animalId) {
	        	map.zoomToExtent(feature.geometry.getBounds(),false);
	         }
	    }
}


function toggleAnimalFeature(animalId, setVisible) {

    //line
	var style = {
            strokeColor: "#ffffff",
            strokeWidth: 2,
            label: "",
            labelAlign: "rt",
            fontColor: "#ffffff",
            fontOpacity: 0.9,
            fontFamily: "Arial",
            fontSize: 12,
            fillOpacity:1.0,
            strokeOpacity:1.0
    };

    for (var l in map.layers) {
    	var layer = map.layers[l];
        if (!layer.isBaseLayer) {
            if (layer === linesLayer) { //(layer.name == "Animal Tracks") {
	            for (var f in layer.features) {
	                var feature = layer.features[f];
	                if (feature.attributes.animalId == animalId) {
	                    if (setVisible) {
	                       	style.strokeColor = colours[feature.attributes.animalId % colours.length];
	                        style.label = feature.attributes.animalName;
	                        style.strokeOpacity = 1.0;
	                    } else {
	                       style.strokeOpacity = 0.0;
	                    }
	                    feature.style = style;
	                    layer.redraw();
	                }
	            }
            }   
	         if (layer === pointsLayer){
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
     + '&dateTo=' + dateTo.val()
     + '&queryType=' + queryType.val()
     + '&mapQueryTypeDescription=' + queryTypeDescription;
    alert("data : " + data);
    */

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

}

function addKMLLayer(layerName, params) {

        var queryOverlay = new OpenLayers.Layer.Vector(
                layerName,
                {strategies: [new OpenLayers.Strategy.Fixed()],
                 protocol: new OpenLayers.Protocol.HTTP(
                    {url: "mapQueryKML",
                     params: params,
                     format: new OpenLayers.Format.KML(
                        {extractStyles: true,
                         extractAttributes: true,
                         maxDepth: 2,
                         internalProjection: googleProjection,
                         externalProjection: kmlProjection,
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
        map.addControl(querySelectControl);
        querySelectControl.activate();
        queryOverlay.refresh();
}


function addWFSLayer(layerName, params) {

        newWFSOverlay = new OpenLayers.Layer.Vector(
            layerName,
            {strategies: [new OpenLayers.Strategy.BBOX()],
             eventListeners: {
                loadend: function (e) {
                	map.zoomToExtent(newWFSOverlay.getDataExtent(),false);
                	updateAnimalStyles();
            	}
             },
             projection: new OpenLayers.Projection("EPSG:4326"),
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
        map.addControl(newSelectControl);
        newSelectControl.activate();
        newWFSOverlay.refresh();
        
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


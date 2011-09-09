
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
var linesWFSOverlay;
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

    linesWFSOverlay = new OpenLayers.Layer.Vector(
        "LinesWFS",
        {strategies: [new OpenLayers.Strategy.BBOX()],
         eventListeners: {
            loadend: function (e) {
            	map.zoomToExtent(linesWFSOverlay.getDataExtent(),false);
            	updateAnimalStyles();
        	}
         },
         projection: new OpenLayers.Projection("EPSG:4326"),
         protocol: new OpenLayers.Protocol.WFS.v1_1_0({
            url:  "mapQueryWFS?projectId=" + projectId + "&queryType=ALL_LINES",
            featureType: "Track",
            featureNS: "http://localhost:8080/"
            })
        });


    var lineSelectControl = new OpenLayers.Control.SelectFeature(
        [linesWFSOverlay],
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

    /*
    var pointsWFSOverlay = new OpenLayers.Layer.Vector(
        "PointsWFS",
        {strategies: [new OpenLayers.Strategy.BBOX()],
         projection: new OpenLayers.Projection("EPSG:4326"),
         protocol: new OpenLayers.Protocol.WFS.v1_1_0(
            {url:  "mapQueryWFS?projectId=" + projectId + "&queryType=ALL_POINTS",
            featureType: "PositionFix",
            featureNS: "http://localhost:8080/",
            geometryName: "location"
            })
        });

    var pointHoverControl = new OpenLayers.Control.SelectFeature(
        [pointsWFSOverlay],
        {
            clickout: true,
            eventListeners: {
                featurehighlighted: function(e) {
                    alert(e.feature.attributes.animalId + " at " + e.feature.attributes.detectionTime);
                },
                featureunhighlighted: function(e) {
                }
            }
        }
    );
    */

    map.addLayers([gsat,gphy]);
    map.addLayer(linesWFSOverlay);
    map.addControl(lineSelectControl);
    lineSelectControl.activate();
    //map.addLayer(pointsWFSOverlay);
    //map.addControl(pointHoverControl);
    //pointHoverControl.activate();

    map.setCenter(new OpenLayers.LonLat(133,-28).transform(kmlProjection,googleProjection), 4);
}



function updateAnimalStyles() {

    for (var key in linesWFSOverlay.features) {
	        var feature = linesWFSOverlay.features[key];
	        if (feature.attributes && feature.attributes.animalId) {
	                var colour = colours[feature.attributes.animalId % colours.length];
	                feature.style = {
	                        strokeColor: colour,
	                        strokeWidth: 2,
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
	linesWFSOverlay.redraw();
}


function zoomToTrack(animalId) {

    for (var key in linesWFSOverlay.features) {
         var feature = linesWFSOverlay.features[key];
         if (feature.attributes && animalId == feature.attributes.animalId) {
            map.zoomToExtent(feature.geometry.getBounds(),false);
         }
    }
}


function toggleAnimalFeature(animalId, setVisible) {

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
            for (var f in layer.features) {
                var feature = layer.features[f];
                if (feature.attributes.animalId == animalId) {
                    if (setVisible) {
                       style.strokeColor = colours[feature.attributes.animalId % colours.length];
                       style.label =  feature.attributes.animalName;
                       style.strokeOpacity = 1.0;
                    } else {
                       style.strokeOpacity = 0.0;
                    }
                    feature.style = style;
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
}


function addWFSLayer(layerName, params) {

        linesWFSOverlay = new OpenLayers.Layer.Vector(
            layerName,
            {strategies: [new OpenLayers.Strategy.BBOX()],
             eventListeners: {
                loadend: function (e) {
                	map.zoomToExtent(linesWFSOverlay.getDataExtent(),false);
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


        var lineSelectControl = new OpenLayers.Control.SelectFeature(
            [linesWFSOverlay],
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

        map.addLayer(linesWFSOverlay);
        map.addControl(lineSelectControl);
        lineSelectControl.activate();
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


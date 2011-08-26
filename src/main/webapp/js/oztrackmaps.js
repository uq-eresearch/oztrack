
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

function initializeProjectMap() {

    var projectId = $('#projectId').val();
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


    map = new OpenLayers.Map('projectMap',mapOptions);
    var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
    layerSwitcher.div = OpenLayers.Util.getElement('layerSwitcherDiv')
    map.addControl(layerSwitcher);
    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.ZoomBox());

    var gphy = new OpenLayers.Layer.Google(
                "Google Physical",
                {type: google.maps.MapTypeId.TERRAIN}
    );
    var gsat = new OpenLayers.Layer.Google(
                "Google Satellite",
                {type: google.maps.MapTypeId.SATELLITE}
    );

    var kmlOverlay = new OpenLayers.Layer.Vector(
                "Points",
                {strategies: [new OpenLayers.Strategy.Fixed()],
                 protocol: new OpenLayers.Protocol.HTTP(
                    {url: "mapQueryKML",
                     params: {projectId:projectId, queryType:"ALL_POINTS"},
                     format: new OpenLayers.Format.KML(
                        {extractStyles: true,
                         extractAttributes: true,
                         maxDepth: 2,
                         internalProjection: googleProjection,
                         externalProjection: kmlProjection
                     })
                  })
                });

    var pointsWFSOverlay = new OpenLayers.Layer.Vector("PointsWFS", {
        strategies: [new OpenLayers.Strategy.BBOX()],
        projection: new OpenLayers.Projection("EPSG:4326"),
        protocol: new OpenLayers.Protocol.WFS({
            url:  "mapQueryWFS?projectId=" + projectId + "&queryType=ALL_POINTS",
            featureType: "PositionFix",
            featureNS: "http://localhost:8080/"
        })
    });


    var lineStyle = new OpenLayers.Style();
    var lineRule = new OpenLayers.Rule({
    symbolizer: {
    strokeColor: "#ffffff",
    strokeWidth: 1,
    label: "${animalName}",
    labelAlign: "rt",
    fontColor: "#ffffff",
    fontOpacity: 0.9,
    fontFamily: "Arial",
    fontSize: 12}
    });
    lineStyle.addRules([lineRule]);

    linesWFSOverlay = new OpenLayers.Layer.Vector("LinesWFS", {
        strategies: [new OpenLayers.Strategy.BBOX()],
        styleMap: lineStyle,
        eventListeners: {
            loadend: function (e){ map.zoomToExtent(linesWFSOverlay.getDataExtent(),false); }
        },
        projection: new OpenLayers.Projection("EPSG:4326"),
        protocol: new OpenLayers.Protocol.WFS({
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
                    var txt="<b>Selected: </b><br> Animal: " + e.feature.attributes.animalName
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


    var lineHoverControl = new OpenLayers.Control.SelectFeature(
        [linesWFSOverlay],
        {
            hover:true,
            highlightOnly: true,
            renderIntent: "temporary",
            eventListeners: {
                featurehighlighted: function(e) {

                }
            }
        }
    );


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

    map.addLayers([gsat,gphy]);
    //map.addLayer(pointsWFSOverlay);
    map.addLayer(linesWFSOverlay);
    map.addControl(pointHoverControl);
    map.addControl(lineSelectControl);
    pointHoverControl.activate();
    lineSelectControl.activate();
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(kmlProjection,googleProjection), 4);
}

function zoomToTrack(animalId) {

    for (var key in linesWFSOverlay.features) {
         var feature = linesWFSOverlay.features[key];
         if (feature.attributes && animalId == feature.attributes.animalId) {
            map.zoomToExtent(feature.geometry.getBounds(),false);
         }
    }
}

function updateProjectMap() {

    alert("updateProjectMap() called.");

    var dateFrom = $('input[id=fromDatepicker]');
    var dateTo=$('input[id=toDatepicker]');
    var queryType =$('select[id=mapQueryTypeSelect]');

    var data= 'projectId=' + $('#projectId').val() +'&dateFrom=' + dateFrom.val() + '&dateTo=' + dateTo.val() + '&queryType=' + queryType.val();
    alert("data : " + data);
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

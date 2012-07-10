var polygonFeatures = new Array();

function createCleanseMap(projectId) {
    var projection900913 = new OpenLayers.Projection("EPSG:900913");
    var projection4326 = new OpenLayers.Projection("EPSG:4326");
    var map = new OpenLayers.Map('projectMap', {
        units: 'm',
        projection: projection900913,
        displayProjection: projection4326
    });
    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.ScaleLine());
    map.addControl(new OpenLayers.Control.NavToolbar());
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.LoadingPanel());
    
    var gphy = new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN});
    var gsat = new OpenLayers.Layer.Google("Google Satellite", {type: google.maps.MapTypeId.SATELLITE});
    var allDetectionsLayer = createAllDetectionsLayer(projectId);

    var polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");
    var polygonControl = new OpenLayers.Control.DrawFeature(polygonLayer, OpenLayers.Handler.Polygon);
    polygonControl.events.register('featureadded', ' ', polygonAdded);
    function polygonAdded(data) {
        var wkt = new OpenLayers.Format.WKT();
        wkt.write(data.feature);
        polygonFeatures.push(data.feature);
        jQuery('#cleanseList').append(
            '<li id="item-' + data.feature.id + '">Polygon ' + polygonFeatures.length + ' ' +
            '(<a href="javascript:void(0)" onclick="deleteCleanseItem(\'' + data.feature.id + '\');">delete</a>)</li>'
        );
    }
    map.addControl(polygonControl);
    polygonControl.activate();

    map.addLayers([gsat, gphy, allDetectionsLayer, polygonLayer]);

    map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326, projection900913), 4);
    
    return map;
}

function deleteCleanseItem(featureId) {
    jQuery('*[id=\'item-' + featureId + '\']').remove();
    for (var i = 0; i < polygonFeatures.length; i++) {
        if (polygonFeatures[i].id == featureId) {
            polygonFeatures[i].destroy();
            polygonFeatures.splice(i, 1);
            break;
        }
    }
}

function createAllDetectionsLayer(projectId) {
    return new OpenLayers.Layer.Vector(
        "All Detections",
        {
            projection: new OpenLayers.Projection("EPSG:4326"),
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                url:  "/mapQueryWFS?projectId=" + projectId + "&queryType=ALL_POINTS",
                featureType: "Track",
                featureNS: "http://localhost:8080/"
            }),
            strategies: [new OpenLayers.Strategy.Fixed()],
            styleMap: createPointStyleMap(), 
            eventListeners: {
                loadend: function (e) {
                    map.zoomToExtent(e.object.getDataExtent(), false);
                }
            }
        }
    );
}

function createPointStyleMap() {
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
    var styleContext = {
        getColour: function(feature) {
            return colours[feature.attributes.animalId % colours.length];
        }
    };
    var pointsOnStyle = new OpenLayers.Style(
        {
            fillColor: "${getColour}",
            strokeColor:"${getColour}",    
            //fillOpacity: 0.5,
            strokeOpacity: 0.8,
            strokeWidth: 0.2,
            graphicName: "cross",
            pointRadius:4
        },
        {
            context: styleContext
        }
    );
    var pointStyleMap = new OpenLayers.StyleMap({
        "default": pointsOnStyle
    });
    return pointStyleMap;
}
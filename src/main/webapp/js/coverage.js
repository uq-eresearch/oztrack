function createCoverageMap(id, wkt) {
    var projection900913 = new OpenLayers.Projection('EPSG:900913');
    var projection4326 =  new OpenLayers.Projection("EPSG:4326");

    var map = new OpenLayers.Map(id);
    var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
    map.addControl(layerSwitcher);

    var gphy = new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN});
    var gsat = new OpenLayers.Layer.Google("Google Satellite", {type: google.maps.MapTypeId.SATELLITE});
    var bbLayer = new OpenLayers.Layer.Vector("Bounding Box Layer");
    map.addLayers([gphy,gsat,bbLayer]);

    if (wkt) {
        var origPolygon = OpenLayers.Geometry.fromWKT(wkt);

        // reproject into google map coordinates so they appear correctly on the map
        var projVertices = origPolygon.getVertices();
        for (var i = 0; i < projVertices.length; i++) {
            projVertices[i] = projVertices[i].transform(projection4326, projection900913);
        }
        var newLR = new OpenLayers.Geometry.LinearRing(projVertices);
        var newPolygon = new OpenLayers.Geometry.Polygon(newLR);

        var featureVector = new OpenLayers.Feature.Vector(newPolygon);
        bbLayer.addFeatures([featureVector]);
        bbLayer.redraw();

        map.zoomToExtent(newPolygon.getBounds());
    }
    else {
        map.setCenter(new OpenLayers.LonLat(0, 0).transform(projection4326, projection900913), 0);
    }

    return map;
}
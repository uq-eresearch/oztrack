/*global OpenLayers, google*/
(function(OzTrack) {
    OzTrack.CoverageMap = function(div, options) {
        if (!(this instanceof OzTrack.CoverageMap)) {
            throw new Error("Constructor called as a function");
        }
        var that = this;

        that.projection900913 = new OpenLayers.Projection("EPSG:900913");
        that.projection4326 = new OpenLayers.Projection("EPSG:4326");

        that.wkt = options.wkt;

        that.map = new OpenLayers.Map(div, {theme: null});

        var gphy = new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN});
        that.map.addLayer(gphy);
    
        var bbLayer = new OpenLayers.Layer.Vector("Bounding Box Layer");
        that.map.addLayer(bbLayer);

        if (that.wkt) {
            var unprojectedPolygon = OpenLayers.Geometry.fromWKT(that.wkt);
            var projectedVertices = unprojectedPolygon.getVertices();
            for (var i = 0; i < projectedVertices.length; i++) {
                projectedVertices[i] = projectedVertices[i].transform(that.projection4326, that.projection900913);
            }
            var projectedLinearRing = new OpenLayers.Geometry.LinearRing(projectedVertices);
            var projectedPolygon = new OpenLayers.Geometry.Polygon(projectedLinearRing);
            var featureVector = new OpenLayers.Feature.Vector(projectedPolygon);
            bbLayer.addFeatures([featureVector]);
            bbLayer.redraw();
            that.map.zoomToExtent(projectedPolygon.getBounds(), true);
        }
        else {
            that.map.setCenter(new OpenLayers.LonLat(0, 0).transform(that.projection4326, that.projection900913), 0);
        }
    };
}(window.OzTrack = window.OzTrack || {}));
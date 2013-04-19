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

        that.map = new OpenLayers.Map(div, {
            theme: null,
            projection: that.projection900913
        });

        var gphy = new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN});
        that.map.addLayer(gphy);
    
        var bbLayer = new OpenLayers.Layer.Vector("Bounding Box Layer", {
            projection: that.projection4326
        });
        that.map.addLayer(bbLayer);

        if (that.wkt) {
            var unprojectedPolygon = OpenLayers.Geometry.fromWKT(that.wkt);
            var projectedPolygon = unprojectedPolygon.clone().transform(that.projection4326, that.projection900913);
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
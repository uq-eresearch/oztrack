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
        that.crosses180 = options.crosses180;

        that.map = new OpenLayers.Map(div, {
            theme: null,
            projection: that.projection900913
        });

        that.baseLayer = new OpenLayers.Layer.Google("Google Physical", {
            type: google.maps.MapTypeId.TERRAIN,
            sphericalMercator: true,
            maxExtent: new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34)
        });
        that.map.addLayer(that.baseLayer);
    
        that.bbLayer = new OpenLayers.Layer.Vector("Bounding Box Layer", {
            projection: that.projection4326
        });
        that.map.addLayer(that.bbLayer);

        if (that.wkt) {
            var unprojectedPolygon = OpenLayers.Geometry.fromWKT(that.wkt);
            var projectedPolygon = unprojectedPolygon.clone().transform(that.projection4326, that.projection900913);
            if (that.crosses180) {
                $.each(projectedPolygon.getVertices(), function(i, projectedVertex) {
                    projectedVertex.x = (projectedVertex.x + 40075016.68) % 40075016.68;
                });
            }
            var featureVector = new OpenLayers.Feature.Vector(projectedPolygon);
            that.bbLayer.addFeatures([featureVector]);
            that.bbLayer.redraw();
            that.map.zoomToExtent(projectedPolygon.getBounds(), false);
        }
        else {
            that.map.setCenter(new OpenLayers.LonLat(0, 0).transform(that.projection4326, that.projection900913), 0);
        }
    };
}(window.OzTrack = window.OzTrack || {}));
/*global OpenLayers*/
// Configure OpenLayers image path; needed because we use wro4j.
// http://dev.openlayers.org/apidocs/files/OpenLayers-js.html#OpenLayers.ImgPath
OpenLayers.ImgPath = "/js/openlayers/img/";

(function(OzTrack) {
    OzTrack.OpenLayers = OzTrack.OpenLayers || {};
    OzTrack.OpenLayers.Control = OzTrack.OpenLayers.Control || {};
    OzTrack.OpenLayers.Control.ZoomToExtent = OpenLayers.Class(OpenLayers.Control.Button, {
        initialize: function(options) {
            this.extent = options.extent;
            this.title = "Zoom to extent";
            options.displayClass = "OzTrackOpenLayersControlZoomToExtent";
            OpenLayers.Control.Button.prototype.initialize.apply(this, [options]);
        },
        trigger: function() {
            this.map.zoomToExtent(this.extent, false);
        },
        CLASS_NAME: "OzTrack.OpenLayers.Control.ZoomToExtent"
    });
}(window.OzTrack = window.OzTrack || {}));
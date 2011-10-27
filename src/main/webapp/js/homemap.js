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
   // layerSwitcher.div = OpenLayers.Util.getElement('homeMapOptions');
    //layerSwitcher.roundedCorner = false;
    map.addControl(layerSwitcher);

    var gphy = new OpenLayers.Layer.Google(
                "Google Physical",
                {type: google.maps.MapTypeId.TERRAIN}
    );

    var gsat = new OpenLayers.Layer.Google(
                "Google Satellite",
                {type: google.maps.MapTypeId.SATELLITE}
    );
    
    allProjectsLayer = new OpenLayers.Layer.Vector(
            "All Projects",{
            projection: projection4326,
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
               url:  "mapQueryWFS?queryType=ALL_PROJECTS",
               featureType: "Project",
               featureNS: "http://localhost:8080/",
               geometryName: "hello"
               }),
            strategies: [new OpenLayers.Strategy.Fixed()],
            eventListeners: {
                loadend: function (e) {
            		map.zoomToExtent(allProjectsLayer.getDataExtent(),false);
            	}
             }
            });
        

    map.addLayers([gsat,gphy, allProjectsLayer]);
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326,projection900913), 4);

}

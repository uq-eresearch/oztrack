function createSrsSelector(options) {
    return (function() {
        var srsSelector = {};

        var mapWidth = 700;
        var mapHeight = 500;

        var srsList = options.srsList;
        var onSrsSelected = options.onSrsSelected;
        var dialogDiv = null;

        (function() {
        })();

        srsSelector.showDialog = function() {
            if (dialogDiv == null) {
                dialogDiv = jQuery('<div style="display: none; text-align: center;">').appendTo('body');
                dialogDiv.dialog({
                    title: 'Select spatial reference system (SRS)',
                    width: mapWidth + 30,
                    height: mapHeight + 40,
                    resizable: false,
                    zIndex: 20000
                });
                var mapDiv = jQuery('<div style="width: ' + mapWidth + 'px; height: ' + mapHeight + 'px;">').appendTo(dialogDiv);

                var projection900913 = new OpenLayers.Projection("EPSG:900913");
                var projection4326 =  new OpenLayers.Projection("EPSG:4326");

                var map = new OpenLayers.Map(mapDiv[0], {
                    theme: null,
                    units: 'm',
                    projection: projection900913,
                    displayProjection: projection4326
                });
                map.addControl(new OpenLayers.Control.LayerSwitcher());

                var gphy = new OpenLayers.Layer.Google('Google Physical', {type: google.maps.MapTypeId.TERRAIN});
                map.addLayer(gphy);

                var srsLayer = new OpenLayers.Layer.Vector('Spatial Reference Systems', {
                    styleMap: new OpenLayers.StyleMap({
                        'temporary': {
                            strokeColor: '#0099ee',
                            fillColor: '#0099ee',
                            cursor: 'pointer',
                            label : '${title} (${id})',
                            fontSize: '11px',
                            fontWeight: 'normal',
                        }
                    })
                });
                for (var i = 0; i < srsList.length; i++) {
                    var srs = srsList[i];
                    var bounds = new OpenLayers.Bounds(srs.bounds);
                    bounds.transform(projection4326, map.getProjectionObject());
                    var srsFeature = new OpenLayers.Feature.Vector(
                        bounds.toGeometry(),
                        {
                            id: srs.id,
                            title: srs.title
                        }
                    );
                    srsLayer.addFeatures([srsFeature]);
                }
                map.addLayer(srsLayer);

                var hoverControl = new OpenLayers.Control.SelectFeature([srsLayer], {
                    hover: true,
                    multiple: false,
                    highlightOnly: true,
                    renderIntent: 'temporary'
                });
                map.addControl(hoverControl);
                hoverControl.activate();

                var selectControl = new OpenLayers.Control.SelectFeature([srsLayer], {
                    hover: false,
                    multiple: false,
                    onSelect: function(e) {
                        this.unselectAll();
                        dialogDiv.dialog('close');
                        onSrsSelected(e.attributes.id);
                    }
                });
                map.addControl(selectControl);
                selectControl.activate();

                map.zoomToExtent(srsLayer.getDataExtent());
            }
            else {
                dialogDiv.dialog('open');
            }
        };

        return srsSelector;
    }());
}

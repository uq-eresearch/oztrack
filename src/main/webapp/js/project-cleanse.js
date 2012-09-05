function createCleanseMap(div, options) {
    return (function() {
        var cleanseMap = {};

        var projection900913 = new OpenLayers.Projection('EPSG:900913');
        var projection4326 = new OpenLayers.Projection('EPSG:4326');

        var projectId = options.projectId;
        var onReset = options.onReset;
        var onPolygonFeatureAdded = options.onPolygonFeatureAdded;
        var onDeletePolygonFeature = options.onDeletePolygonFeature;

        var map;
        var allDetectionsLayer;
        var polygonLayer;
        var polygonFeatures;
        var highlightControl;
        var beforeFirstZoom = true;

        (function() {
            map = new OpenLayers.Map(div, {
                units: 'm',
                projection: projection900913,
                displayProjection: projection4326
            });
            var navToolbar = new OpenLayers.Control.NavToolbar();
            map.addControl(navToolbar);
            map.addControl(new OpenLayers.Control.MousePosition());
            map.addControl(new OpenLayers.Control.ScaleLine());
            map.addControl(new OpenLayers.Control.LayerSwitcher());
            map.addControl(new OpenLayers.Control.LoadingPanel());
            map.addControl(createControlPanel());

            var gphy = new OpenLayers.Layer.Google('Google Physical', {type: google.maps.MapTypeId.TERRAIN});
            var gsat = new OpenLayers.Layer.Google('Google Satellite', {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22});
            var gmap = new OpenLayers.Layer.Google('Google Streets', {numZoomLevels: 20});
            var ghyb = new OpenLayers.Layer.Google('Google Hybrid', {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20});
            var osmLayer = new OpenLayers.Layer.OSM('OpenStreetMap');
            allDetectionsLayer = createAllDetectionsLayer(projectId);
            polygonLayer = new OpenLayers.Layer.Vector('Polygons');
            map.addLayers([gsat, gphy, gmap, ghyb, osmLayer, allDetectionsLayer, polygonLayer]);

            polygonFeatures = [];
            var polygonControl = new OpenLayers.Control.DrawFeature(polygonLayer, OpenLayers.Handler.Polygon);
            polygonControl.events.register('featureadded', null, polygonFeatureAdded);
            navToolbar.addControls(polygonControl);
            navToolbar.activateControl(polygonControl);

            highlightControl = new OpenLayers.Control.SelectFeature([polygonLayer], {
                hover: true,
                highlightOnly: true,
                renderIntent: 'temporary'
            });
            map.addControl(highlightControl);

            map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326, projection900913), 4);
        }());

        cleanseMap.reset = function() {
            allDetectionsLayer.protocol.params['fromDate'] = jQuery('#fromDate').val();
            allDetectionsLayer.protocol.params['toDate'] = jQuery('#toDate').val();
            allDetectionsLayer.refresh();
            while (polygonFeatures.length > 0) {
                polygonFeatures.shift().destroy();
            }
            if (onReset) {
                onReset();
            }
        };
        
        function createControlPanel() {
            var panel = new OpenLayers.Control.Panel();
            panel.addControls([
                new OpenLayers.Control.Button({
                    title: 'Zoom to Data Extent',
                    displayClass: "zoomButton",
                    trigger: function() {
                        map.zoomToExtent(allDetectionsLayer.getDataExtent(), false);
                    }
                })
            ]);
            return panel;
        }

        cleanseMap.toggleAllAnimalFeatures = function(animalId, setVisible) {
            function getVectorLayers() {
                var vectorLayers = new Array();
                for (var c in map.controls) {
                    var control = map.controls[c];
                    if (control.id.indexOf("LayerSwitcher") != -1) {
                        for (var i=0; i < control.dataLayers.length; i++) {
                            vectorLayers.push(control.dataLayers[i].layer);
                        }
                    }
                }
                return vectorLayers;
            }

            var vectorLayers = getVectorLayers();
            for (var l in vectorLayers) {
                var layer = vectorLayers[l];
                var layerName = layer.name;
                for (var f in layer.features) {
                    var feature = layer.features[f];
                    if (feature.attributes.animalId == animalId) {
                        var featureIdentifier = layer.id + "-" + feature.id;
                        toggleFeature(featureIdentifier, setVisible);
                    }
                }
            }
            $("#animalInfo-"+animalId).find(':checkbox').attr("checked", setVisible);
        };

        function toggleFeature(featureIdentifier, setVisible) {
            var splitString = featureIdentifier.split("-");
            var layerId = splitString[0];
            var featureId = splitString[1];

            var layer = map.getLayer(layerId);
            for (var key in layer.features) {
                var feature = layer.features[key];
                if (feature.id == featureId) {
                    if (setVisible) {
                        feature.renderIntent = "default";
                    }
                    else {
                        feature.renderIntent = "temporary";
                    }
                    layer.drawFeature(feature);
                }
            }
        }

        function polygonFeatureAdded(e) {
            // Polygons must have at least 3 sides.
            // Discard any geometries that are just points or lines.
            if (e.feature.geometry.getVertices().length < 3) {
                e.feature.destroy();
                return;
            }
            polygonFeatures.push(e.feature);
            if (onPolygonFeatureAdded) {
                var geometry = e.feature.geometry.clone();
                geometry.transform(projection900913, projection4326);
                var wktFormat = new OpenLayers.Format.WKT();
                var wkt = wktFormat.extractGeometry(geometry);
                onPolygonFeatureAdded(e.feature.id, 'Selection ' + polygonFeatures.length, wkt);
            }
        }

        cleanseMap.selectPolygonFeature = function(id, selected) {
            for (var i = 0; i < polygonFeatures.length; i++) {
                if (polygonFeatures[i].id == id) {
                    highlightControl[selected ? 'select' : 'unselect'](polygonFeatures[i]);
                    break;
                }
            }
        };

        cleanseMap.deletePolygonFeature = function(id) {
            if (onDeletePolygonFeature) {
                onDeletePolygonFeature(id);
            }
            for (var i = 0; i < polygonFeatures.length; i++) {
                if (polygonFeatures[i].id == id) {
                    polygonFeatures[i].destroy();
                    polygonFeatures.splice(i, 1);
                    break;
                }
            }
        };

        function createAllDetectionsLayer(projectId) {
            return new OpenLayers.Layer.Vector(
                'Detections',
                {
                    projection: projection4326,
                    protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                        url:  '/mapQueryWFS',
                        params: {
                            projectId: projectId,
                            fromDate: jQuery('#fromDate').val(),
                            toDate: jQuery('#fromDate').val(),
                            queryType: 'POINTS',
                            includeDeleted: true
                        },
                        featureType: 'Detections',
                        featureNS: 'http://oztrack.org/xmlns#'
                    }),
                    strategies: [new OpenLayers.Strategy.Fixed()],
                    styleMap: createPointStyleMap(),
                    eventListeners: {
                        loadend: function (e) {
                            jQuery('.select-animal').each(function() {
                                cleanseMap.toggleAllAnimalFeatures(parseInt(jQuery(this).attr('id').substring('select-animal-'.length)), this.checked);
                            });
                            if (beforeFirstZoom) {
                                map.zoomToExtent(e.object.getDataExtent(), false);
                                beforeFirstZoom = false;
                            }
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
            var pointsOnStyle = new OpenLayers.Style(
                {
                    graphicName: 'cross',
                    pointRadius: 4,
                    fillOpacity: 1.0,
                    strokeOpacity: 0.8,
                    strokeWidth: 0.2
                },
                {
                    rules: [
                        new OpenLayers.Rule({
                            filter: new OpenLayers.Filter.Comparison({
                                type: OpenLayers.Filter.Comparison.EQUAL_TO,
                                property: "deleted",
                                value: 'true',
                            }),
                            symbolizer: {
                                fillColor: '#bc0000',
                                strokeColor: '#ffffff',
                            }
                        }),
                        new OpenLayers.Rule({
                            elseFilter: true,
                            symbolizer: {
                                fillColor: '${getColour}',
                                strokeColor: '${getColour}',
                            }
                        })
                    ],
                    context: {
                        getColour: function(feature) {
                            return colours[feature.attributes.animalId % colours.length];
                        }
                    }
                }
            );
            var pointOffStyle = {
                strokeOpacity: 0.0,
                fillOpacity: 0.0
            };
            var pointStyleMap = new OpenLayers.StyleMap({
                'default': pointsOnStyle,
                'temporary': pointOffStyle
            });
            return pointStyleMap;
        }

        return cleanseMap;
    }());
}
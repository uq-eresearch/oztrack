function createCleanseMap(div, options) {
    return (function() {
        var cleanseMap = {};

        var projection900913 = new OpenLayers.Projection('EPSG:900913');
        var projection4326 = new OpenLayers.Projection('EPSG:4326');

        var projectId = options.projectId;
        var fromDate = options.fromDate;
        var toDate = options.toDate;
        var animalIds = options.animalIds;
        var animalVisible = {};
        for (var i = 0; i < animalIds.length; i++) {
            animalVisible[animalIds[i]] = true;
        }
        var projectBounds = options.projectBounds.clone().transform(projection4326, projection900913);
        var onReset = options.onReset;
        var onPolygonFeatureAdded = options.onPolygonFeatureAdded;
        var onDeletePolygonFeature = options.onDeletePolygonFeature;

        var map;
        var allDetectionsLayer;
        var polygonLayer;
        var polygonFeatures;
        var highlightControl;

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
            var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
            map.addControl(layerSwitcher);
            layerSwitcher.maximizeControl();
            map.addControl(new OpenLayers.Control.LoadingPanel());
            map.addControl(createControlPanel());

            var gphy = new OpenLayers.Layer.Google('Google Physical', {type: google.maps.MapTypeId.TERRAIN});
            var gsat = new OpenLayers.Layer.Google('Google Satellite', {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22});
            var gmap = new OpenLayers.Layer.Google('Google Streets', {numZoomLevels: 20});
            var ghyb = new OpenLayers.Layer.Google('Google Hybrid', {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20});
            var osmLayer = new OpenLayers.Layer.OSM('OpenStreetMap');
            var bathymetryLayer = new OpenLayers.Layer.WMS(
                    'Bathymetry',
                    '/geoserver/gwc/service/wms',
                    {
                        layers: 'oztrack:gebco_08',
                        styles: 'bathymetry',
                        format: 'image/png'
                    },
                    {
                        isBaseLayer: true,
                        wrapDateLine: true,
                        attribution: '<a href="http://www.gebco.net">The GEBCO_08 Grid, version 20091120</a>'
                    }
                );
            map.addLayers([gsat, gphy, gmap, ghyb, osmLayer, bathymetryLayer]);

            allDetectionsLayer = createAllDetectionsLayer(projectId);
            polygonLayer = new OpenLayers.Layer.Vector('Selections');
            map.addLayers([allDetectionsLayer, polygonLayer]);

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

            map.zoomToExtent(projectBounds);
        }());

        cleanseMap.reset = function() {
            updateFilter();
            while (polygonFeatures.length > 0) {
                polygonFeatures.shift().destroy();
            }
            if (onReset) {
                onReset();
            }
        };

        function updateFilter() {
            allDetectionsLayer.params['CQL_FILTER'] = buildAllDetectionsFilter();
            allDetectionsLayer.redraw();
        }
        
        cleanseMap.updateSize = function() {
            map.updateSize();
        };
        
        function createControlPanel() {
            var panel = new OpenLayers.Control.Panel();
            panel.addControls([
                new OpenLayers.Control.Button({
                    title: 'Zoom to Data Extent',
                    displayClass: "zoomButton",
                    trigger: function() {
                        map.zoomToExtent(projectBounds, false);
                    }
                })
            ]);
            return panel;
        }

        cleanseMap.setFromDate = function(date) {
            fromDate = date;
            updateFilter();
        };

        cleanseMap.setToDate = function(date) {
            fromDate = date;
            updateFilter();
        };

        cleanseMap.setAnimalVisible = function(animalId, visible) {
            animalVisible[animalId] = visible;
            updateFilter();
        };

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

        function buildAllDetectionsFilter() {
            var visibleAnimalIds = [];
            for (i = 0; i < animalIds.length; i++) {
                if (animalVisible[animalIds[i]]) {
                    visibleAnimalIds.push(animalIds[i]);
                }
            }
            // Include bogus animal ID (e.g. -1) that will never be matched.
            // This covers the case where no animals are selected to be visible,
            // preventing the CQL_FILTER parameter from being syntactically invalid.
            if (visibleAnimalIds.length == 0) {
                visibleAnimalIds.push(-1);
            }
            var cqlFilter =
                'project_id = ' + projectId +
                ' and animal_id in (' + visibleAnimalIds.join(', ') + ')';
            var fromDate = jQuery('#fromDate').val();
            var toDate = jQuery('#toDate').val();
            if (fromDate) {
                cqlFilter += ' and detectiontime >= \'' + dateTimeToISO8601(new Date(fromDate)) + '\'';
            }
            if (toDate) {
                cqlFilter += ' and detectiontime <= \'' + dateTimeToISO8601(new Date(toDate)) + '\'';
            }
            return cqlFilter;
        }

        function createAllDetectionsLayer() {
            return new OpenLayers.Layer.WMS(
                'Detections',
                '/geoserver/wms',
                {
                    layers: 'oztrack:positionfixlayer',
                    styles: 'positionfixlayer',
                    cql_filter: buildAllDetectionsFilter(),
                    format: 'image/png',
                    transparent: true
                },
                {
                    isBaseLayer: false,
                    tileSize: new OpenLayers.Size(512,512)
                }
            );
        }

        return cleanseMap;
    }());
}

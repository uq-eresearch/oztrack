function createAnalysisMap(div, options) {
    return (function() {
        var analysisMap = {};

        var projection900913 = new OpenLayers.Projection("EPSG:900913");
        var projection4326 = new OpenLayers.Projection("EPSG:4326");

        var projectId = options.projectId;
        var animalIds = options.animalIds;
        var animalVisible = {};
        for (var i = 0; i < animalIds.length; i++) {
            animalVisible[animalIds[i]] = true;
        }
        var projectBounds = options.projectBounds.clone().transform(projection4326, projection900913);
        var animalBounds = {};
        for (animalId in options.animalBounds) {
            animalBounds[animalId] = options.animalBounds[animalId].clone().transform(projection4326, projection900913);
        }
        var animalColours = options.animalColours;
        var minDate = options.minDate;
        var maxDate = options.maxDate;
        var onAnalysisError = options.onAnalysisError;
        var onAnalysisSuccess = options.onAnalysisSuccess;

        var map;
        var loadingPanel;
        var detectionLayers = [];
        var trajectoryLayers = [];
        var polygonStyleMap;
        var lineStyleMap;
        var pointStyleMap;
        var startEndStyleMap;

        (function() {
            map = new OpenLayers.Map(div, {
                units : 'm',
                projection : projection900913,
                displayProjection : projection4326
            });
            map.addControl(new OpenLayers.Control.MousePosition());
            map.addControl(new OpenLayers.Control.ScaleLine());
            map.addControl(new OpenLayers.Control.NavToolbar());
            var layerSwitcher = new OpenLayers.Control.LayerSwitcher();
            map.addControl(layerSwitcher);
            layerSwitcher.maximizeControl();
            loadingPanel = new OpenLayers.Control.LoadingPanel();
            map.addControl(loadingPanel);

            var gphy = new OpenLayers.Layer.Google('Google Physical', {
                type : google.maps.MapTypeId.TERRAIN
            });
            var gsat = new OpenLayers.Layer.Google('Google Satellite', {
                type : google.maps.MapTypeId.SATELLITE,
                numZoomLevels : 22
            });
            var gmap = new OpenLayers.Layer.Google('Google Streets', {
                numZoomLevels : 20
            });
            var ghyb = new OpenLayers.Layer.Google('Google Hybrid', {
                type : google.maps.MapTypeId.HYBRID,
                numZoomLevels : 20
            });
            map.addLayers([
                    gsat, gphy, gmap, ghyb
            ]);

            var osmLayer = new OpenLayers.Layer.OSM('OpenStreetMap');
            map.addLayer(osmLayer);

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
            map.addLayer(bathymetryLayer);

            map.addControl(createControlPanel());

            lineStyleMap = createLineStyleMap();
            pointStyleMap = createPointStyleMap();
            startEndStyleMap = createStartEndPointsStyleMap();
            polygonStyleMap = createPolygonStyleMap();

            var allDetectionsLayer = createDetectionLayer({});
            detectionLayers.push(allDetectionsLayer);
            map.addLayer(allDetectionsLayer.getWMSLayer());

            var allTrajectoriesLayer = createTrajectoryLayer({});
            trajectoryLayers.push(allTrajectoriesLayer);
            map.addLayer(allTrajectoriesLayer.getWMSLayer());

            map.addLayer(createWFSLayer('Start and End Points', 'StartEnd', {
                projectId : projectId,
                queryType : 'START_END'
            }, startEndStyleMap));
            
            map.zoomToExtent(projectBounds, false);
        }());

        function createControlPanel() {
            var panel = new OpenLayers.Control.Panel();
            panel.addControls([
                new OpenLayers.Control.Button({
                    title : 'Zoom to Data Extent',
                    displayClass : "zoomButton",
                    trigger : function() {
                        map.zoomToExtent(projectBounds, false);
                    }
                })
            ]);
            return panel;
        }

        function createLineStyleMap() {
            var styleContext = {
                getColour : function(feature) {
                    return animalColours[feature.attributes.animalId];
                }
            };
            var lineOnStyle = new OpenLayers.Style({
                strokeColor : "${getColour}",
                strokeWidth : 2.0,
                strokeOpacity : 0.8
            }, {
                context : styleContext
            });
            var lineOffStyle = {
                strokeOpacity : 0.0,
                fillOpacity : 0.0
            };
            var lineStyleMap = new OpenLayers.StyleMap({
                "default" : lineOnStyle,
                "temporary" : lineOffStyle
            });
            return lineStyleMap;
        }

        function createPointStyleMap() {
            var styleContext = {
                getColour : function(feature) {
                    return animalColours[feature.attributes.animalId];
                }
            };
            var pointOnStyle = new OpenLayers.Style({
                fillColor : "${getColour}",
                strokeColor : "${getColour}",
                strokeOpacity : 0.8,
                strokeWidth : 0.2,
                graphicName : "cross",
                pointRadius : 4
            }, {
                context : styleContext
            });
            var pointOffStyle = {
                strokeOpacity : 0.0,
                fillOpacity : 0.0
            };
            var pointStyleMap = new OpenLayers.StyleMap({
                "default" : pointOnStyle,
                "temporary" : pointOffStyle
            });
            return pointStyleMap;
        }

        function createPolygonStyleMap() {
            var styleContext = {
                getColour : function(feature) {
                    return animalColours[feature.attributes.id.value];
                }
            };
            var polygonOnStyle = new OpenLayers.Style({
                strokeColor : "${getColour}",
                strokeWidth : 2.0,
                strokeOpacity : 0.8,
                fillColor : "${getColour}",
                fillOpacity : 0.5
            }, {
                context : styleContext
            });
            var polygonOffStyle = {
                strokeOpacity : 0.0,
                fillOpacity : 0.0
            };
            var polygonStyleMap = new OpenLayers.StyleMap({
                "default" : polygonOnStyle,
                "temporary" : polygonOffStyle
            });
            return polygonStyleMap;
        }

        function createStartEndPointsStyleMap() {
            var styleContext = {
                getColour : function(feature) {
                    if (feature.attributes.identifier == "start") {
                        return "#00CD00";
                    }
                    if (feature.attributes.identifier == "end") {
                        return "#CD0000";
                    }
                    return "#CDCDCD";
                }
            };
            var startEndPointsOnStyle = new OpenLayers.Style({
                pointRadius : 2,
                strokeColor : "${getColour}",
                strokeWidth : 1.2,
                strokeOpacity : 1.0,
                fillColor : "${getColour}",
                fillOpacity : 0
            }, {
                context : styleContext
            });
            var startEndPointsOffStyle = {
                strokeOpacity : 0,
                fillOpacity : 0
            };
            var startEndPointsStyleMap = new OpenLayers.StyleMap({
                "default" : startEndPointsOnStyle,
                "temporary" : startEndPointsOffStyle
            });
            return startEndPointsStyleMap;
        }

        analysisMap.addProjectMapLayer = function() {
            var queryType = $('input[name=mapQueryTypeSelect]:checked');
            var queryTypeValue = queryType.val();
            var queryTypeLabel = $('label[for="' + queryType.attr('id') + '"]')
                    .text();

            if (queryTypeValue != null) {
                var layerName = queryTypeLabel;
                var params = {
                    queryType : queryTypeValue,
                    projectId : $('#projectId').val(),
                    percent : $('input[id=percent]').val(),
                    h : $('input[id=h]').val(),
                    alpha : $('input[id=alpha]').val(),
                    gridSize : $('input[id=gridSize]').val(),
                    extent : $('input[id=extent]').val()
                };
                var fromDate = $('#fromDate').val();
                if (fromDate) {
                    params.fromDate = fromDate;
                }
                var toDate = $('#toDate').val();
                if (toDate) {
                    params.toDate = toDate;
                }
                params.animals = $('input[name=animal]:checked').map(function() {return $(this).val();}).toArray();
                if (queryTypeValue == "LINES") {
                    var trajectoryLayer = createTrajectoryLayer(params);
                    trajectoryLayers.push(trajectoryLayer);
                    map.addLayer(trajectoryLayer.getWMSLayer());
                }
                else if (queryTypeValue == "POINTS") {
                    var detectionLayer = createDetectionLayer(params);
                    detectionLayers.push(detectionLayer);
                    map.addLayer(detectionLayer.getWMSLayer());
                }
                else if (queryTypeValue == "START_END") {
                    map.addLayer(createWFSLayer(layerName, 'StartEnd', params, startEndStyleMap));
                }
                else if ((queryTypeValue == "HEATMAP_POINT") || (queryTypeValue == "HEATMAP_LINE")) {
                    map.addLayer(createKMLLayer(layerName, params, null, true));
                }
                else {
                    map.addLayer(createKMLLayer(layerName, params, polygonStyleMap, null));
                }
            }
            else {
                alert("Please set a Layer Type.");
            }
        };
        
        analysisMap.addKMLLayer = function(layerName, params) {
            if ((params.queryType == "HEATMAP_POINT") || (params.queryType == "HEATMAP_LINE")) {
                map.addLayer(createKMLLayer(layerName, params, null, true));
            }
            else {
                map.addLayer(createKMLLayer(layerName, params, polygonStyleMap, null));
            }
        };
        
        function createDetectionLayer(params) {
            function buildFilter(params) {
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
                    'deleted = false' +
                    ' and project_id = ' + projectId +
                    ' and animal_id in (' + visibleAnimalIds.join(', ') + ')';
                if (params.fromDate) {
                    cqlFilter += ' and detectiontime >= \'' + dateTimeToISO8601(new Date(params.fromDate)) + '\'';
                }
                if (params.toDate) {
                    cqlFilter += ' and detectiontime <= \'' + dateTimeToISO8601(new Date(params.toDate)) + '\'';
                }
                return cqlFilter;
            }
            var title = 'Detections';
            var wmsLayer = new OpenLayers.Layer.WMS(
                title,
                '/geoserver/wms',
                {
                    layers: 'oztrack:positionfixlayer',
                    styles: 'positionfixlayer',
                    cql_filter: buildFilter(params),
                    format: 'image/png',
                    transparent: true
                },
                {
                    isBaseLayer: false,
                    tileSize: new OpenLayers.Size(512,512)
                }
            );
            var layer = {
                getTitle: function() {
                    return title;
                },
                getParams: function() {
                    return params;
                },
                getCQLFilter: function() {
                    return buildFilter(params);
                },
                getWMSLayer: function() {
                    return wmsLayer;
                }
            };
            updateAnimalInfoFromLayer(layer);
            onAnalysisSuccess();
            return layer;
        }
        
        function updateDetectionLayers() {
            for (var i = 0; i < detectionLayers.length; i++) {
                detectionLayers[i].getWMSLayer().params['CQL_FILTER'] = detectionLayers[i].getCQLFilter();
                detectionLayers[i].getWMSLayer().redraw();
            }
        }
        
        function createTrajectoryLayer(params) {
            function buildFilter(params) {
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
                if (params.fromDate) {
                    cqlFilter += ' and startdetectiontime >= \'' + dateTimeToISO8601(new Date(params.fromDate)) + '\'';
                }
                if (params.toDate) {
                    cqlFilter += ' and enddetectiontime <= \'' + dateTimeToISO8601(new Date(params.toDate)) + '\'';
                }
                return cqlFilter;
            }
            var title = 'Trajectory';
            var wmsLayer = new OpenLayers.Layer.WMS(
                title,
                '/geoserver/wms',
                {
                    layers: 'oztrack:trajectorylayer',
                    styles: 'trajectorylayer',
                    cql_filter: buildFilter(params),
                    format: 'image/png',
                    transparent: true
                },
                {
                    isBaseLayer: false,
                    tileSize: new OpenLayers.Size(512,512)
                }
            );
            var layer = {
                getTitle: function() {
                    return title;
                },
                getParams: function() {
                    return params;
                },
                getCQLFilter: function() {
                    return buildFilter(params);
                },
                getWMSLayer: function() {
                    return wmsLayer;
                }
            };
            updateAnimalInfoFromLayer(layer);
            onAnalysisSuccess();
            return layer;
        }
        
        function updateTrajectoryLayers() {
            for (var i = 0; i < trajectoryLayers.length; i++) {
                trajectoryLayers[i].getWMSLayer().params['CQL_FILTER'] = trajectoryLayers[i].getCQLFilter();
                trajectoryLayers[i].getWMSLayer().redraw();
            }
        }

        function createWFSLayer(layerName, featureType, params, styleMap) {
            return new OpenLayers.Layer.Vector(layerName, {
                projection : projection4326,
                protocol : new OpenLayers.Protocol.WFS.v1_1_0({
                    url : "/mapQueryWFS",
                    params : params,
                    featureType : featureType,
                    featureNS : "http://oztrack.org/xmlns#"
                }),
                strategies : [
                    new OpenLayers.Strategy.Fixed()
                ],
                styleMap : styleMap,
                eventListeners : {
                    loadend : function(e) {
                        updateAnimalInfoFromWFS(e.object);
                        onAnalysisSuccess();
                    }
                }
            });
        }

        function createKMLLayer(layerName, params, styleMap, extractStyles) {
            var url = "/mapQueryKML";
            var queryOverlay = new OpenLayers.Layer.Vector(layerName, {
                styleMap : styleMap
            });
            var protocol = new OpenLayers.Protocol.HTTP({
                url : url,
                params : params,
                format : new OpenLayers.Format.KML({
                    extractStyles : extractStyles,
                    extractAttributes : true,
                    maxDepth : 2,
                    internalProjection : projection900913,
                    externalProjection : projection4326,
                    kmlns : "http://oztrack.org/xmlns#"
                })
            });
            var callback = function(resp) {
                loadingPanel.decreaseCounter();
                if (resp.code == OpenLayers.Protocol.Response.SUCCESS) {
                    queryOverlay.addFeatures(resp.features);
                    updateAnimalInfoFromKML(layerName, url, params, resp.features);
                    onAnalysisSuccess();
                }
                else {
                    onAnalysisError(jQuery(resp.priv.responseText).find('error').text() || 'Error processing request');
                }
            };
            loadingPanel.increaseCounter();
            protocol.read({
                callback : callback
            });
            return queryOverlay;
        }

        function updateAnimalInfoFromWFS(wfsLayer) {
            var animalProcessed = {};
            for ( var key in wfsLayer.features) {
                var feature = wfsLayer.features[key];
                if (feature.attributes && feature.attributes.animalId) {
                    if (animalProcessed[feature.attributes.animalId]) {
                        continue;
                    }
                    animalProcessed[feature.attributes.animalId] = true;
                    feature.renderIntent = "default";
                    $('input[id=select-animal-' + feature.attributes.animalId + ']').attr('checked', 'checked');
                    var html = '<div class="layerInfoTitle">' + wfsLayer.name + '</div>';
                    var tableRowsHtml = '';
                    if (feature.attributes.fromDate) {
                        tableRowsHtml += '<tr>';
                        tableRowsHtml += '<td class="layerInfoLabel">Date From:</td>';
                        tableRowsHtml += '<td>' + feature.attributes.fromDate + '</td>';
                        tableRowsHtml += '</tr>';
                    }
                    if (feature.attributes.toDate) {
                        tableRowsHtml += '<tr>';
                        tableRowsHtml += '<td class="layerInfoLabel">Date To:</td>';
                        tableRowsHtml += '<td>' + feature.attributes.toDate + '</td>';
                        tableRowsHtml += '</tr>';
                    }
                    if (feature.geometry.CLASS_NAME == 'OpenLayers.Geometry.LineString') {
                        var distance = (Math.round(feature.geometry.getGeodesicLength(map.projection)) / 1000);
                        tableRowsHtml += '<tr>';
                        tableRowsHtml += '<td class="layerInfoLabel">Min Distance: </td>';
                        tableRowsHtml += '<td>' + distance + ' km </td>';
                        tableRowsHtml += '</tr>';
                    }
                    if (tableRowsHtml != '') {
                        html += '<table>' + tableRowsHtml + '</table>';
                    }
                    $('#animalInfo-' + feature.attributes.animalId).append('<div class="layerInfo">' + html + '</div>');
                }
            }
        }
        
        function updateAnimalInfoFromLayer(layer) {
            for (var i = 0; i < animalIds.length; i++) {
                var html = '<div class="layerInfoTitle">' + layer.getTitle() + '</div>';
                var tableRowsHtml = '';
                var fromDate = layer.getParams().fromDate || dateToISO8601(minDate);
                tableRowsHtml += '<tr>';
                tableRowsHtml += '<td class="layerInfoLabel">Date From:</td>';
                tableRowsHtml += '<td>' + fromDate + '</td>';
                tableRowsHtml += '</tr>';
                var toDate = layer.getParams().toDate || dateToISO8601(maxDate);
                tableRowsHtml += '<tr>';
                tableRowsHtml += '<td class="layerInfoLabel">Date To:</td>';
                tableRowsHtml += '<td>' + toDate + '</td>';
                tableRowsHtml += '</tr>';
                if (tableRowsHtml != '') {
                    html += '<table>' + tableRowsHtml + '</table>';
                }
                $('#animalInfo-' + animalIds[i]).append('<div class="layerInfo">' + html + '</div>');
            }
        }

        function updateAnimalInfoFromKML(layerName, url, params, features) {
            var animalProcessed = {};
            for ( var f in features) {
                var feature = features[f];
                if (animalProcessed[feature.attributes.id.value]) {
                    continue;
                }
                animalProcessed[feature.attributes.id.value] = true;
                feature.renderIntent = "default";
                feature.layer.drawFeature(feature);
                var html = '<div class="layerInfoTitle">' + layerName + '</div>';
                var tableRowsHtml = '';
                if (params.percent) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Percent: </td>';
                    tableRowsHtml += '<td>' + params.percent + '%</td>';
                    tableRowsHtml += '</tr>';
                }
                if (params.h) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">h value: </td>';
                    tableRowsHtml += '<td>' + params.h + '</td>';
                    tableRowsHtml += '</tr>';
                }
                if (params.alpha) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">alpha: </td>';
                    tableRowsHtml += '<td>' + params.alpha + '</td>';
                    tableRowsHtml += '</tr>';
                }
                if (params.gridSize) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Grid size (m): </td>';
                    tableRowsHtml += '<td>' + params.gridSize + '</td>';
                    tableRowsHtml += '</tr>';
                }
                if (params.extent) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Extent: </td>';
                    tableRowsHtml += '<td>' + params.extent + '</td>';
                    tableRowsHtml += '</tr>';
                }
                if (feature.attributes.area.value) {
                    var area = Math.round(feature.attributes.area.value * 1000) / 1000;
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Area: </td>';
                    tableRowsHtml += '<td>' + area + ' km<sup>2</sup></td>';
                    tableRowsHtml += '</tr>';
                }
                var urlWithParams = url;
                var paramString = OpenLayers.Util.getParameterString(params);
                if (paramString.length > 0) {
                    var separator = (urlWithParams.indexOf('?') > -1) ? '&'
                            : '?';
                    urlWithParams += separator + paramString;
                }
                tableRowsHtml += '<tr><td class="layerInfoLabel">Export as: </td><td><a href="' + urlWithParams + '">KML</a></td></tr>';
                if (tableRowsHtml) {
                    html += '<table>' + tableRowsHtml + '</table>';
                }
                $('#animalInfo-' + feature.attributes.id.value).append('<div class="layerInfo">' + html + '</div>');
            }
        }

        analysisMap.zoomToAnimal = function(animalId) {
            if (animalBounds[animalId]) {
                map.zoomToExtent(animalBounds[animalId], false);
            }
        };

        function toggleFeature(feature, visible) {
            feature.renderIntent = visible ? 'default' : 'temporary';
            feature.layer.drawFeature(feature);
        }

        analysisMap.toggleAllAnimalFeatures = function(animalId, visible) {
            animalVisible[animalId] = visible;
            updateDetectionLayers();
            updateTrajectoryLayers();
            function getVectorLayers() {
                var vectorLayers = new Array();
                for (var c in map.controls) {
                    var control = map.controls[c];
                    if (control.id.indexOf("LayerSwitcher") != -1) {
                        for (var i = 0; i < control.dataLayers.length; i++) {
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
                    var featureAnimalId =
                        (feature.attributes.animalId) ? feature.attributes.animalId :
                        (feature.attributes.id.value) ? feature.attributes.id.value :
                        null;
                    if (featureAnimalId == animalId) {
                        toggleFeature(feature, visible);
                    }
                }
            }
            $("#animalInfo-" + animalId).find(':checkbox').attr("checked", visible);
        };

        analysisMap.updateSize = function() {
            map.updateSize();
        };

        return analysisMap;
    }());
}

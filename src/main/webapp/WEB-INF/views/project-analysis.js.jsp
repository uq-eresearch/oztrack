<%@ page contentType="text/javascript; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        var onAnalysisCreate = options.onAnalysisCreate;
        var onAnalysisError = options.onAnalysisError;
        var onAnalysisSuccess = options.onAnalysisSuccess;

        var map;
        var loadingPanel;
        var detectionLayers = [];
        var trajectoryLayers = [];
        var projectMapLayers = {};
        var wfsLayers = {};
        var analyses = {};
        var polygonStyleMap;
        var lineStyleMap;
        var pointStyleMap;
        var startEndStyleMap;
        var projectMapLayerIdSeq = 0;
        var wfsLayerIdSeq = 0;

        (function() {
            map = new OpenLayers.Map(div, {
                theme: null,
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
            map.addLayers([gsat, gphy, gmap, ghyb]);

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
                strokeColor : "\${getColour}",
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
                fillColor : "\${getColour}",
                strokeColor : "\${getColour}",
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
                strokeColor : "\${getColour}",
                strokeWidth : 2.0,
                strokeOpacity : 0.8,
                fillColor : "\${getColour}",
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
                strokeColor : "\${getColour}",
                strokeWidth : 1.2,
                strokeOpacity : 1.0,
                fillColor : "\${getColour}",
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
            var queryType = $('input[name=queryTypeSelect]:checked');
            var queryTypeValue = queryType.val();
            var queryTypeLabel = $('label[for="' + queryType.attr('id') + '"]').text();
            if (queryTypeValue == null) {
                alert("Please set a Layer Type.");
            }
            var layerName = queryTypeLabel;
            var params = {
                queryType : queryTypeValue,
                projectId : $('#projectId').val()
            };
            var fromDate = $('#fromDate').val();
            if (fromDate) {
                params.fromDate = fromDate;
            }
            var toDate = $('#toDate').val();
            if (toDate) {
                params.toDate = toDate;
            }
            params.animalIds = $('input[name=animal]:checked').map(function() {return $(this).val();}).toArray().join(',');
            $('.paramField-' + queryTypeValue).each(function() {
                if ($(this).attr('type') == 'checkbox') {
                    params[$(this).attr('name')] = $(this).is(':checked') ? 'true' : 'false';
                }
                else if ($(this).val()) {
                    params[$(this).attr('name')] = $(this).val();
                }
            });
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
            else {
                createAnalysisLayer(params, layerName);
            }
        };

        function createAnalysisLayer(params, layerName) {
            $.ajax({
                url: '/projects/' + projectId + '/analyses',
                type: 'POST',
                data: params,
                error: function(xhr, textStatus, errorThrown) {
                    onAnalysisError($(xhr.responseText).find('error').text() || 'Error processing request');
                },
                complete: function (xhr, textStatus) {
                    if (textStatus == 'success') {
                        var analysisUrl = xhr.getResponseHeader('Location');
                        onAnalysisCreate(layerName, analysisUrl);
                        analysisMap.addAnalysisLayer(analysisUrl, layerName);
                    }
                }
            });
        }

        analysisMap.addAnalysisLayer = function(analysisUrl, layerName) {
            $.ajax({
                url: analysisUrl,
                type: 'GET',
                error: function(xhr, textStatus, errorThrown) {
                    onAnalysisError($(xhr.responseText).find('error').text() || 'Error getting analysis');
                },
                complete: function (xhr, textStatus) {
                    if (textStatus == 'success') {
                        var analysis = $.parseJSON(xhr.responseText);
                        analyses[analysis.id] = analysis;
                        updateAnimalInfoForAnalysis(layerName, analysis);
                        loadingPanel.increaseCounter();
                        pollAnalysisLayer(analysisUrl, layerName);
                    }
                }
            });
        };

        function pollAnalysisLayer(analysisUrl, layerName) {
            $.ajax({
                url: analysisUrl,
                type: 'GET',
                error: function(xhr, textStatus, errorThrown) {
                    loadingPanel.decreaseCounter();
                    onAnalysisError($(xhr.responseText).find('error').text() || 'Error getting analysis');
                },
                complete: function (xhr, textStatus) {
                    if (textStatus == 'success') {
                        var analysis = $.parseJSON(xhr.responseText);
                        if (!analyses[analysis.id]) {
                            loadingPanel.decreaseCounter();
                            return;
                        }
                        analyses[analysis.id] = analysis;
                        if (analysis.status == 'COMPLETE') {
                            addAnalysisResultLayer(analysis, layerName);
                        }
                        else if ((analysis.status == 'NEW') || (analysis.status == 'PROCESSING')) {
                            setTimeout(function () {pollAnalysisLayer(analysisUrl, layerName);}, 1000);
                        }
                        else {
                            loadingPanel.decreaseCounter();
                            onAnalysisError(analysis.message || 'Error running analysis');
                        }
                    }
                }
            });
        }

        function addAnalysisResultLayer(analysis, layerName) {
            var styleMap = polygonStyleMap;
            var extractStyles = false;
            if ((analysis.params.queryType == "HEATMAP_POINT") || (analysis.params.queryType == "HEATMAP_LINE")) {
                styleMap = null;
                extractStyles = true;
            }
            var queryOverlay = new OpenLayers.Layer.Vector(layerName, {
                styleMap : styleMap
            });
            if (analyses[analysis.id]) {
                analyses[analysis.id].layer = queryOverlay;
            }
            var protocol = new OpenLayers.Protocol.HTTP({
                url : analysis.resultUrl,
                format : new OpenLayers.Format.KML({
                    extractStyles: extractStyles,
                    extractAttributes: true,
                    maxDepth: 2,
                    internalProjection: projection900913,
                    externalProjection: projection4326,
                    kmlns: "http://oztrack.org/xmlns#"
                })
            });
            var callback = function(resp) {
                loadingPanel.decreaseCounter();
                if (resp.code == OpenLayers.Protocol.Response.SUCCESS) {
                    queryOverlay.addFeatures(resp.features);
                    updateAnimalInfoFromKML(analysis, resp.features);
                    onAnalysisSuccess();
                }
                else {
                    onAnalysisError(jQuery(resp.priv.responseText).find('error').text() || 'Error processing request');
                }
            };
            protocol.read({
                callback : callback
            });
            map.addLayer(queryOverlay);
        }
        
        analysisMap.deleteAnalysis = function(id) {
            var confirmMessage =
                (analyses[id] && (analyses[id].params.animalIds.length > 1))
                ? 'This will delete the analysis for all animals. Do you wish to continue?'
                : 'Are you sure you wish to delete this analysis?';
            if (!confirm(confirmMessage)) {
                return;
            }
            if (analyses[id]) {
                if (analyses[id].layer) {
                    analyses[id].layer.destroy();
                }
                delete analyses[id];
            }
            $('.analysisInfo-' + id).fadeOut().remove();
        };
        
        analysisMap.deleteProjectMapLayer = function(id) {
            if (!confirm('This will delete the layer for all animals. Do you wish to continue?')) {
                return;
            }
            if (projectMapLayers[id]) {
                projectMapLayers[id].getWMSLayer().destroy();
                delete projectMapLayers[id];
            }
            $('.projectMapLayerInfo-' + id).fadeOut().remove();
        };

        analysisMap.deleteWFSLayer = function(id) {
            if (!confirm('This will delete the layer for all animals. Do you wish to continue?')) {
                return;
            }
            if (wfsLayers[id]) {
                wfsLayers[id].destroy();
                delete wfsLayers[id];
            }
            $('.wfsLayerInfo-' + id).fadeOut().remove();
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
                id: projectMapLayerIdSeq++,
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
            projectMapLayers[layer.id] = layer;
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
                id: projectMapLayerIdSeq++,
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
            projectMapLayers[layer.id] = layer;
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
            var wfsLayerId = wfsLayerIdSeq++;
            var wfsLayer = new OpenLayers.Layer.Vector(layerName, {
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
                        updateAnimalInfoFromWFS(e.object, wfsLayerId);
                        onAnalysisSuccess();
                    }
                }
            });
            wfsLayers[wfsLayerId] = wfsLayer;
            return wfsLayer;
        }

        function updateAnimalInfoFromWFS(wfsLayer, wfsLayerId) {
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
                    var html = '<div class="layerInfoTitle">';
                    html += '<a class="layer-delete" href="javascript:analysisMap.deleteWFSLayer(' + wfsLayerId + ');">delete</a></span>';
                    html += wfsLayer.name;
                    html += '</div>';
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
                    $('#animalInfo-' + feature.attributes.animalId).append('<div class="layerInfo wfsLayerInfo-' + wfsLayerId + '">' + html + '</div>');
                }
            }
        }
        
        function updateAnimalInfoFromLayer(layer) {
            for (var i = 0; i < animalIds.length; i++) {
                var html = '<div class="layerInfoTitle">';
                html += '<a class="layer-delete" href="javascript:analysisMap.deleteProjectMapLayer(' + layer.id + ');">delete</a></span>';
                html += layer.getTitle();
                html += '</div>';
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
                $('#animalInfo-' + animalIds[i]).append('<div class="layerInfo projectMapLayerInfo-' + layer.id + '">' + html + '</div>');
            }
        }

        function updateAnimalInfoForAnalysis(layerName, analysis) {
            for (var i = 0; i < analysis.params.animalIds.length; i++) {
                var html = '<div class="layerInfoTitle">';
                html += '<a class="layer-delete" href="javascript:analysisMap.deleteAnalysis(' + analysis.id + ');">delete</a></span>';
                html += layerName;
                html += '</div>';
                var tableRowsHtml = '';
                if (analysis.params.fromDate) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Date From:</td>';
                    tableRowsHtml += '<td>' + analysis.params.fromDate + '</td>';
                    tableRowsHtml += '</tr>';
                }
                if (analysis.params.toDate) {
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Date To:</td>';
                    tableRowsHtml += '<td>' + analysis.params.toDate + '</td>';
                    tableRowsHtml += '</tr>';
                }
                <c:forEach items="${analysisTypeList}" var="analysisType">
                if (analysis.params.queryType == '${analysisType}') {
                    <c:forEach items="${analysisType.parameterTypes}" var="parameterType">
                    if (analysis.params.${parameterType.identifier}) {
                        tableRowsHtml += '<tr>';
                        tableRowsHtml += '<td class="layerInfoLabel">${parameterType.displayName}: </td>';
                        tableRowsHtml += '<td>' + analysis.params.${parameterType.identifier} + ' ${parameterType.units}</td>';
                        tableRowsHtml += '</tr>';
                    }
                    </c:forEach>
                }
                </c:forEach>
                html += '<table id="analysis-table-' + analysis.params.animalIds[i] + '-' + analysis.id + '" style="' + (tableRowsHtml ? '' : 'display: none;') + '">' + tableRowsHtml + '</table>';
                $('#animalInfo-' + analysis.params.animalIds[i]).append('<div class="layerInfo analysisInfo-' + analysis.id + '">' + html + '</div>');
            }
        }

        function updateAnimalInfoFromKML(analysis, features) {
            var animalProcessed = {};
            for (var f in features) {
                var feature = features[f];
                if (animalProcessed[feature.attributes.id.value]) {
                    continue;
                }
                animalProcessed[feature.attributes.id.value] = true;
                feature.renderIntent = "default";
                feature.layer.drawFeature(feature);
                var tableRowsHtml = '';
                if (feature.attributes.area && feature.attributes.area.value) {
                    var area = Math.round(feature.attributes.area.value * 1000) / 1000;
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">Area: </td>';
                    tableRowsHtml += '<td>' + area + ' km<sup>2</sup></td>';
                    tableRowsHtml += '</tr>';
                }
                if (!analysis.params.hValue && feature.attributes.hval && feature.attributes.hval.value) {
                    var hval = Math.round(feature.attributes.hval.value * 1000) / 1000;
                    tableRowsHtml += '<tr>';
                    tableRowsHtml += '<td class="layerInfoLabel">h value: </td>';
                    tableRowsHtml += '<td>' + hval + '</td>';
                    tableRowsHtml += '</tr>';
                }
                tableRowsHtml += '<tr>';
                tableRowsHtml += '<td class="layerInfoLabel">Export as: </td>';
                tableRowsHtml += '<td><a href="' + analysis.resultUrl + '">KML</a></td>';
                tableRowsHtml += '</tr>';
                if (tableRowsHtml) {
                    $('#analysis-table-' + feature.attributes.id.value + '-' + analysis.id).show().append(tableRowsHtml);
                }
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

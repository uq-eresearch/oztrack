/*global OpenLayers*/

(function(OzTrack) {
    OzTrack.AnalysisMap = function(div, options) {
        if (!(this instanceof OzTrack.AnalysisMap)) {
            throw new Error('Constructor called as a function');
        }
        var that = this;

        that.projection900913 = new OpenLayers.Projection('EPSG:900913');
        that.projection4326 = new OpenLayers.Projection('EPSG:4326');

        that.project = options.project;
        that.project.bounds.transform(that.projection4326, that.projection900913);
        if (that.project.crosses180) {
            that.project.bounds.left = (that.project.bounds.left + 40075016.68) % 40075016.68;
            that.project.bounds.right = (that.project.bounds.right + 40075016.68) % 40075016.68;
        }
        that.animals = options.animals;
        $.each(that.animals, function(i, animal) {
            animal.bounds.transform(that.projection4326, that.projection900913);
            if (that.project.crosses180) {
                animal.bounds.left = (animal.bounds.left + 40075016.68) % 40075016.68;
                animal.bounds.right = (animal.bounds.right + 40075016.68) % 40075016.68;
            }
            animal.visible = true;
        });
        that.onAnalysisCreate = options.onAnalysisCreate;
        that.onAnalysisError = options.onAnalysisError;
        that.onLayerSuccess = options.onLayerSuccess;
        that.onUpdateAnimalInfoFromLayer = options.onUpdateAnimalInfoFromLayer;
        that.onUpdateAnimalInfoForAnalysis = options.onUpdateAnimalInfoForAnalysis;
        that.onUpdateAnimalInfoFromKML = options.onUpdateAnimalInfoFromKML;

        that.projectMap = new OzTrack.ProjectMap(div, {
            project: that.project,
            animals: that.animals,
            onUpdateAnimalInfoFromLayer: that.onUpdateAnimalInfoFromLayer,
            onLayerSuccess: that.onLayerSuccess
        });

        that.analyses = {};

        function getAnimal(id) {
            return $.grep(that.animals, function(x) {return x.id == id;})[0];
        }

        that.addProjectMapLayer = function(layerType, queryTypeValue, queryTypeLabel) {
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
            params.animalIds = $('input[name=animal]:not(:disabled):checked').map(function() {return $(this).val();}).toArray().join(',');
            $('.paramField-' + queryTypeValue).each(function() {
                if ($(this).attr('type') == 'checkbox') {
                    params[$(this).attr('name')] = $(this).is(':checked') ? 'true' : 'false';
                }
                else if ($(this).val()) {
                    params[$(this).attr('name')] = $(this).val();
                }
            });
            if (queryTypeValue == 'LINES') {
                var trajectoryLayer = that.projectMap.createTrajectoryLayer(params, 'analysis');
                that.projectMap.addLayer(trajectoryLayer.getWMSLayer());
            }
            else if (queryTypeValue == 'POINTS') {
                var detectionLayer = that.projectMap.createDetectionLayer(params, 'analysis');
                that.projectMap.addLayer(detectionLayer.getWMSLayer());
            }
            else if (queryTypeValue == 'START_END') {
                var startEndLayer = that.projectMap.createStartEndLayer(params, 'analysis');
                that.projectMap.addLayer(startEndLayer);
            }
            else {
                createAnalysisLayer(params, layerName);
            }
        };

        function createAnalysisLayer(params, layerName) {
            $.ajax({
                url: '/projects/' + that.project.id + '/analyses',
                type: 'POST',
                data: params,
                error: function(xhr, textStatus, errorThrown) {
                    that.onAnalysisError($(xhr.responseText).find('error').text() || 'Error processing request');
                },
                complete: function (xhr, textStatus) {
                    if (textStatus == 'success') {
                        var analysisUrl = xhr.getResponseHeader('Location');
                        that.onAnalysisCreate(layerName, analysisUrl);
                        that.addAnalysisLayer(analysisUrl, layerName);
                    }
                }
            });
        }

        that.addAnalysisLayer = function(analysisUrl, layerName) {
            $.ajax({
                url: analysisUrl,
                type: 'GET',
                error: function(xhr, textStatus, errorThrown) {
                    that.onAnalysisError($(xhr.responseText).find('error').text() || 'Error getting analysis');
                },
                complete: function (xhr, textStatus) {
                    if (textStatus == 'success') {
                        var analysis = $.parseJSON(xhr.responseText);
                        that.analyses[analysis.id] = analysis;
                        currentAnalysisId = analysis.id;
                        updateAnimalInfoForAnalysis(layerName, analysis);
                        that.loadingPanel.increaseCounter();
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
                    that.loadingPanel.decreaseCounter();
                    that.onAnalysisError($(xhr.responseText).find('error').text() || 'Error getting analysis');
                },
                complete: function (xhr, textStatus) {
                    if (textStatus == 'success') {
                        var analysis = $.parseJSON(xhr.responseText);
                        if (!that.analyses[analysis.id]) {
                            that.loadingPanel.decreaseCounter();
                            return;
                        }
                        that.analyses[analysis.id] = analysis;
                        if (analysis.status == 'COMPLETE') {
                            addAnalysisResultLayer(analysis, layerName);
                        }
                        else if ((analysis.status == 'NEW') || (analysis.status == 'PROCESSING')) {
                            setTimeout(function () {pollAnalysisLayer(analysisUrl, layerName);}, 1000);
                        }
                        else {
                            that.loadingPanel.decreaseCounter();
                            that.onAnalysisError(analysis.message || 'Error running analysis');
                        }
                    }
                }
            });
        }

        function addAnalysisResultLayer(analysis, layerName) {
            var queryOverlay = new OpenLayers.Layer.Vector(layerName, {
                styleMap : analysis.hasAnimalFeatures ? that.polygonStyleMap : null,
                metadata: {
                    category: 'analysis',
                    showInformation: false
                }
            });
            if (that.analyses[analysis.id]) {
                that.analyses[analysis.id].layer = queryOverlay;
            }
            var kmlResultFile = $.grep(analysis.resultFiles, function(r) {return r.format === 'kml';})[0];
            var protocol = new OpenLayers.Protocol.HTTP({
                url : kmlResultFile.url,
                format : new OpenLayers.Format.KML({
                    extractStyles: !analysis.hasAnimalFeatures,
                    extractAttributes: true,
                    maxDepth: 2,
                    internalProjection: that.projection900913,
                    externalProjection: that.projection4326,
                    kmlns: 'http://oztrack.org/xmlns#'
                })
            });
            var callback = function(resp) {
                that.loadingPanel.decreaseCounter();
                if (resp.code == OpenLayers.Protocol.Response.SUCCESS) {
                    if (that.project.crosses180) {
                        $.each(resp.features, function(i, feature) {
                            var geometries = feature.components || [feature.geometry];
                            $.each(geometries, function(j, geometry) {
                                $.each(geometry.getVertices(), function(k, vertex) {
                                    vertex.x = (vertex.x + 40075016.68) % 40075016.68;
                                });
                            });
                        });
                    }
                    queryOverlay.addFeatures(resp.features);

                    // Workaround: OpenLayers sometimes fails to draw polygons until map is panned.
                    if (that.project.crosses180) {
                        that.projectMap.nudge();
                    }

                    updateAnimalInfoFromKML(analysis, resp.features);
                    that.onLayerSuccess();
                }
                else {
                    that.onAnalysisError(jQuery(resp.priv.responseText).find('error').text() || 'Error processing request');
                }
            };
            protocol.read({
                callback: callback
            });
            that.projectMap.addLayer(queryOverlay);
        }

        that.deleteCurrentAnalysis = function() {
            if (currentAnalysisId) {
                that.deleteAnalysis(currentAnalysisId);
            }
        };

        that.deleteAnalysis = function(id) {
            var confirmMessage =
                (that.analyses[id] && (that.analyses[id].params.animalIds.length > 1))
                ? 'This will delete the analysis for all animals. Do you wish to continue?'
                : 'Are you sure you wish to delete this analysis?';
            if (!confirm(confirmMessage)) {
                return;
            }
            if (that.analyses[id]) {
                if (that.analyses[id].layer) {
                    that.analyses[id].layer.destroy();
                }
                delete that.analyses[id];
            }
            if (id == currentAnalysisId) {
                currentAnalysisId = null;
            }
            $('.analysisInfo-' + id).fadeOut().remove();
        };

        function updateAnimalInfoForAnalysis(layerName, analysis) {
            var fromDate = moment(analysis.params.fromDate || that.project.minDate).format('YYYY-MM-DD');
            var toDate = moment(analysis.params.toDate || that.project.maxDate).format('YYYY-MM-DD');
            for (var i = 0; i < analysis.params.animalIds.length; i++) {
                that.onUpdateAnimalInfoForAnalysis(layerName, analysis.params.animalIds[i], analysis, fromDate, toDate);
            }
        }

        function updateAnimalInfoFromKML(analysis, features) {
            for (i = 0; i < analysis.params.animalIds.length; i++) {
                var animalId = analysis.params.animalIds[i];
                var animalFeature = null;
                for (j = 0; j < features.length; j++) {
                    if (features[j].attributes.id && features[j].attributes.id.value == animalId) {
                        animalFeature = features[j];
                        break;
                    }
                }
                if (animalFeature) {
                    animalFeature.renderIntent = 'default';
                    animalFeature.layer.drawFeature(animalFeature);
                }
                that.onUpdateAnimalInfoFromKML(
                    animalId,
                    analysis,
                    animalFeature ? animalFeature.attributes : null
                );
            }
        }

        // Delegate to properties/functions of OzTrack.ProjectMap
        that.loadingPanel = that.projectMap.loadingPanel;
        that.polygonStyleMap = that.projectMap.polygonStyleMap;
        that.updateSize = that.projectMap.updateSize;
        that.zoomToAnimal = that.projectMap.zoomToAnimal;
        that.deleteProjectMapLayer = function(id) {
            that.projectMap.deleteProjectMapLayer(id);
            $('.projectMapLayerInfo-' + id).fadeOut().remove();
        };
        that.toggleAllAnimalFeatures = function(animalId, visible) {
            that.projectMap.toggleAllAnimalFeatures(animalId, visible);
            $('#animalInfo-' + animalId).find(':checkbox').attr('checked', visible);
        };
        that.toggleAllAnimalFeaturesCommit = that.projectMap.toggleAllAnimalFeaturesCommit;
    };
}(window.OzTrack = window.OzTrack || {}));

/*global OpenLayers, google*/

(function(OzTrack) {
    OzTrack.CleanseMap = function(div, options) {
        if (!(this instanceof OzTrack.CleanseMap)) {
            throw new Error("Constructor called as a function");
        }
        var that = this;

        that.projection900913 = new OpenLayers.Projection('EPSG:900913');
        that.projection4326 = new OpenLayers.Projection('EPSG:4326');

        that.project = options.project;
        that.animals = options.animals;
        that.onReset = options.onReset;
        that.onPolygonFeatureAdded = options.onPolygonFeatureAdded;
        that.onDeletePolygonFeature = options.onDeletePolygonFeature;
        that.kalmanFilterAnalysis = null;

        that.projectMap = new OzTrack.ProjectMap(div, {
            project: that.project,
            animals: that.animals,
            showAllDetections: true,
            showAllTrajectories: true,
            showAllStartEnd: false,
            includeDeleted: true,
            highlightProbable: true,
            extraCategories: {'filter': {label: 'Filter layers'}},
            onAnalysisCreate: function(layerName, analysis) {
                that.kalmanFilterAnalysis = analysis;
            },
            onAnalysisError: options.onKalmanFilterError,
            onAnalysisSuccess: function() {
                that.projectMap.showMessage(
                    'Complete',
                    '<p>\n' +
                    '    The most probable track is shown on the map,\n' +
                    '    with Kalman filter parameters and result values on the left of screen.\n' +
                    '</p>\n' +
                    '<p>\n' +
                    '    To replace the animal\'s original track with output from the filter,\n' +
                    '    click <i>Replace original track</i>.\n' +
                    '    Alternatively, you can click <i>Cancel</i> to remove the filtered track.' +
                    '</p>' +
                    '<p>\n' +
                    '    Once replaced, the new track will appear on the <i>Tracks and analysis</i> page.\n' +
                    '</p>'
                );
                options.onKalmanFilterSuccess && options.onKalmanFilterSuccess();
            },
            onUpdateAnimalInfoFromAnalysisCreate: function(layerName, animalId, analysis, fromDate, toDate) {
                options.onUpdateInfoFromKalmanFilterCreate(layerName, analysis, fromDate, toDate);
            },
            onUpdateAnimalInfoFromAnalysisSuccess: function(animalId, analysis, animalAttributes) {
                options.onUpdateInfoFromKalmanFilterSuccess(analysis, animalAttributes);
            }
        });

        that.submitCleanseRequest = function(operation, params) {
            $.ajax({
                url: '/projects/' + that.project.id + '/cleanse',
                type: 'POST',
                data: params,
                beforeSend: function(jqXHR, settings) {
                    cleanseMap.increaseLoadingCounter();
                },
                success: function(data, textStatus, jqXHR) {
                    cleanseMap.reset();
                    var message = null;
                    if ((operation == 'delete')) {
                        var numDeleted = $(data).find('num-deleted').text();
                        message = numDeleted + ' detections deleted';
                    }
                    else if ((operation == 'undelete')) {
                        var numUndeleted = $(data).find('num-undeleted').text();
                        message = numUndeleted + ' detections restored';
                    }
                    that.projectMap.showMessage('Complete', message);
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    var message = $(jqXHR.responseText).find('error').text() || 'Error processing request';
                    that.projectMap.showMessage('Error', message);
                },
                complete: function(jqXHR, textStatus) {
                    cleanseMap.decreaseLoadingCounter();
                }
            });
        };

        that.applyKalmanFilterAnalysis = function() {
            if (!that.kalmanFilterAnalysis) {
                return;
            }
            $.ajax({
                url: that.kalmanFilterAnalysis.url + '/apply',
                type: 'POST',
                beforeSend: function(jqXHR, settings) {
                    that.increaseLoadingCounter();
                },
                success: function(data, textStatus, jqXHR) {
                    that.reset();
                    that.projectMap.showMessage(
                        'Complete',
                        'Original track replaced with most probable track from Kalman filter.'
                    );
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    that.projectMap.showMessage(
                        'Error',
                        $(jqXHR.responseText).find('error').text() || 'Error applying filter'
                    );
                },
                complete: function(jqXHR, textStatus) {
                    that.decreaseLoadingCounter();
                }
            });
            that.deleteKalmanFilterAnalysis();
        };

        that.deleteKalmanFilterAnalysis = function() {
            if (!that.kalmanFilterAnalysis) {
                return;
            }
            that.projectMap.deleteAnalysis(that.kalmanFilterAnalysis.id);
            that.kalmanFilterAnalysis = null;
        };

        that.polygonLayer = new OpenLayers.Layer.Vector('Polygon Selections', {
            metadata: {category: 'filter'}
        });
        that.projectMap.addLayer(that.polygonLayer);

        that.polygonFeatures = [];

        that.polygonControl = new OpenLayers.Control.DrawFeature(that.polygonLayer, OpenLayers.Handler.Polygon, {
            title: 'Draw polygons'
        });
        that.polygonControl.events.register('featureadded', null, polygonFeatureAdded);
        that.polygonControl.events.on({
            activate: function(e) {
                this.formerViewPortDivTitle = $(e.object.map.viewPortDiv).attr('title');
                $(e.object.map.viewPortDiv).attr('title', 'Double click to finish polygon');
            },
            deactivate: function(e) {
                if (this.formerViewPortDivTitle) {
                    $(e.object.map.viewPortDiv).attr('title', this.formerViewPortDivTitle);
                }
                else {
                    $(e.object.map.viewPortDiv).removeAttr('title');
                }
            }
        });
        that.projectMap.addControl(that.polygonControl, true);
        that.projectMap.activateControl(that.polygonControl);

        that.highlightPolygonControl = new OpenLayers.Control.SelectFeature([that.polygonLayer], {
            hover: true,
            highlightOnly: true,
            renderIntent: 'temporary'
        });
        that.projectMap.addControl(that.highlightPolygonControl, false);

        that.reset = function() {
            that.projectMap.updateLayers();
            while (that.polygonFeatures.length > 0) {
                that.polygonFeatures.shift().destroy();
            }
            that.onReset && that.onReset();
        };

        function polygonFeatureAdded(e) {
            // Polygons must have at least 3 sides.
            // Discard any geometries that are just points or lines.
            if (e.feature.geometry.getVertices().length < 3) {
                e.feature.destroy();
                return;
            }
            that.polygonFeatures.push(e.feature);
            if (that.onPolygonFeatureAdded) {
                var geometry = e.feature.geometry.clone();
                geometry.transform(that.projection900913, that.projection4326);
                var wktFormat = new OpenLayers.Format.WKT();
                var wkt = wktFormat.extractGeometry(geometry);
                that.onPolygonFeatureAdded(e.feature.id, 'Selection ' + that.polygonFeatures.length, wkt);
            }
        }

        that.selectPolygonFeature = function(id, selected) {
            for (var i = 0; i < that.polygonFeatures.length; i++) {
                if (that.polygonFeatures[i].id == id) {
                    that.highlightPolygonControl[selected ? 'select' : 'unselect'](that.polygonFeatures[i]);
                    break;
                }
            }
        };

        that.deletePolygonFeature = function(id) {
            if (that.onDeletePolygonFeature) {
                that.onDeletePolygonFeature(id);
            }
            for (var i = 0; i < that.polygonFeatures.length; i++) {
                if (that.polygonFeatures[i].id == id) {
                    that.polygonFeatures[i].destroy();
                    that.polygonFeatures.splice(i, 1);
                    break;
                }
            }
        };

        that.setPolygonControlActivated = function(activated) {
            that.projectMap.activateControl(activated ? that.polygonControl : null);
        };

        // Delegate to properties/functions of OzTrack.ProjectMap
        that.updateSize = that.projectMap.updateSize;
        that.zoomToAnimal = that.projectMap.zoomToAnimal;
        that.increaseLoadingCounter = that.projectMap.increaseLoadingCounter;
        that.decreaseLoadingCounter = that.projectMap.decreaseLoadingCounter;
        that.setFromDate = that.projectMap.setFromDate;
        that.setToDate = that.projectMap.setToDate;
        that.setAnimalVisible = that.projectMap.setAnimalVisible;
        that.createAnalysisLayer = that.projectMap.createAnalysisLayer;
    };
}(window.OzTrack = window.OzTrack || {}));
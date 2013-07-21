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

        that.projectMap = new OzTrack.ProjectMap(div, {
            project: that.project,
            animals: that.animals,
            showAllDetections: true,
            showAllTrajectories: true,
            showAllStartEnd: false,
            includeDeleted: true,
            extraCategories: {'filter': {label: 'Filter layers'}},
            onLayerSuccess: options.onLayerSuccess,
            onAnalysisCreate: options.onAnalysisCreate,
            onAnalysisError: options.onAnalysisError
        });

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

        // Delegate to properties/functions of OzTrack.ProjectMap
        that.updateSize = that.projectMap.updateSize;
        that.zoomToAnimal = that.projectMap.zoomToAnimal;
        that.increaseLoadingCounter = that.projectMap.increaseLoadingCounter;
        that.decreaseLoadingCounter = that.projectMap.decreaseLoadingCounter;
        that.setFromDate = that.projectMap.setFromDate;
        that.setToDate = that.projectMap.setToDate;
        that.setAnimalVisible = that.projectMap.setAnimalVisible;
        that.createAnalysisLayer = that.projectMap.createAnalysisLayer;
        that.deleteCurrentAnalysis = that.projectMap.deleteCurrentAnalysis;
    };
}(window.OzTrack = window.OzTrack || {}));
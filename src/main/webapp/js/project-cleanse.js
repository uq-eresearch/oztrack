/*global OpenLayers, google*/
(function(OzTrack) {
    OzTrack.CleanseMap = function(div, options) {
        if (!(this instanceof OzTrack.CleanseMap)) {
            throw new Error("Constructor called as a function");
        }
        var that = this;

        that.projection900913 = new OpenLayers.Projection('EPSG:900913');
        that.projection4326 = new OpenLayers.Projection('EPSG:4326');

        that.projectId = options.projectId;
        that.fromDate = options.fromDate;
        that.toDate = options.toDate;
        that.animalIds = options.animalIds;
        that.animalVisible = {};
        $.each(that.animalIds, function(i, animalId) {
            that.animalVisible[animalId] = true;
        });
        that.projectBounds = options.projectBounds.clone().transform(that.projection4326, that.projection900913);
        that.onReset = options.onReset;
        that.onPolygonFeatureAdded = options.onPolygonFeatureAdded;
        that.onDeletePolygonFeature = options.onDeletePolygonFeature;

        that.map = new OpenLayers.Map(div, {
            theme: null,
            units: 'm',
            projection: that.projection900913,
            displayProjection: that.projection4326
        });
        var OzTrackNavToolbar = OpenLayers.Class(OpenLayers.Control.NavToolbar, {
            initialize: function() { 
                OpenLayers.Control.Panel.prototype.initialize.apply(this, [options]);
                this.addControls([
                    new OpenLayers.Control.Navigation(),
                    new OpenLayers.Control.ZoomBox(),
                    new OzTrack.OpenLayers.Control.ZoomToExtent({extent: that.projectBounds})
                ])
            }
        });
        that.navToolbar = new OzTrackNavToolbar();
        that.map.addControl(that.navToolbar);
        that.map.addControl(new OpenLayers.Control.ScaleLine());

        that.layerSwitcher = new OzTrack.OpenLayers.Control.OzTrackLayerSwitcher({
            categoryLabels: {
                'base': 'Base layer',
                'project': 'Project layers'
            }
        });
        that.map.addControl(that.layerSwitcher);
        that.layerSwitcher.maximizeControl();
        that.loadingPanel = new OpenLayers.Control.LoadingPanel();
        that.map.addControl(that.loadingPanel);

        that.googlePhysicalLayer = new OpenLayers.Layer.Google('Google Physical', {
            type: google.maps.MapTypeId.TERRAIN,
            metadata: {category: 'base'}
        });
        that.googleSatelliteLayer = new OpenLayers.Layer.Google('Google Satellite', {
            type: google.maps.MapTypeId.SATELLITE,
            numZoomLevels: 22,
            metadata: {category: 'base'}
        });
        that.googleStreetsLayer = new OpenLayers.Layer.Google('Google Streets', {
            numZoomLevels: 20,
            metadata: {category: 'base'}
        });
        that.googleHybridLayer = new OpenLayers.Layer.Google('Google Hybrid', {
            type: google.maps.MapTypeId.HYBRID,
            numZoomLevels: 20,
            metadata: {category: 'base'}
        });
        that.map.addLayers([that.googleSatelliteLayer, that.googlePhysicalLayer, that.googleStreetsLayer, that.googleHybridLayer]);

        that.osmLayer = new OpenLayers.Layer.OSM('OpenStreetMap', null, {
            metadata: {category: 'base'}
        });
        that.map.addLayer(that.osmLayer);

        that.allDetectionsLayer = createAllDetectionsLayer(that.projectId);
        that.polygonLayer = new OpenLayers.Layer.Vector('Polygon selections', {
            metadata: {category: 'project'}
        });
        that.map.addLayers([that.allDetectionsLayer, that.polygonLayer]);

        that.polygonFeatures = [];
        that.polygonControl = new OpenLayers.Control.DrawFeature(that.polygonLayer, OpenLayers.Handler.Polygon);
        that.polygonControl.events.register('featureadded', null, polygonFeatureAdded);
        that.navToolbar.addControls(that.polygonControl);
        that.navToolbar.activateControl(that.polygonControl);

        that.highlightControl = new OpenLayers.Control.SelectFeature([that.polygonLayer], {
            hover: true,
            highlightOnly: true,
            renderIntent: 'temporary'
        });
        that.map.addControl(that.highlightControl);

        that.map.zoomToExtent(that.projectBounds);

        that.reset = function() {
            updateFilter();
            while (that.polygonFeatures.length > 0) {
                that.polygonFeatures.shift().destroy();
            }
            if (that.onReset) {
                that.onReset();
            }
        };

        function updateFilter() {
            that.allDetectionsLayer.params['CQL_FILTER'] = buildAllDetectionsFilter();
            that.allDetectionsLayer.redraw(true);
        }
        
        that.updateSize = function() {
            that.map.updateSize();
        };

        that.setFromDate = function(date) {
            that.fromDate = date;
            updateFilter();
        };

        that.setToDate = function(date) {
            that.toDate = date;
            updateFilter();
        };

        that.setAnimalVisible = function(animalId, visible) {
            that.animalVisible[animalId] = visible;
            updateFilter();
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
                    that.highlightControl[selected ? 'select' : 'unselect'](that.polygonFeatures[i]);
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
        
        that.increaseLoadingCounter = function() {
            that.loadingPanel.increaseCounter();
        };
        
        that.decreaseLoadingCounter = function() {
            that.loadingPanel.decreaseCounter();
        };

        function buildAllDetectionsFilter() {
            var visibleAnimalIds = [];
            for (i = 0; i < that.animalIds.length; i++) {
                if (that.animalVisible[that.animalIds[i]]) {
                    visibleAnimalIds.push(that.animalIds[i]);
                }
            }
            // Include bogus animal ID (e.g. -1) that will never be matched.
            // This covers the case where no animals are selected to be visible,
            // preventing the CQL_FILTER parameter from being syntactically invalid.
            if (visibleAnimalIds.length == 0) {
                visibleAnimalIds.push(-1);
            }
            var cqlFilter =
                'project_id = ' + that.projectId +
                ' and animal_id in (' + visibleAnimalIds.join(', ') + ')';
            var fromDate = jQuery('#fromDate').val();
            var toDate = jQuery('#toDate').val();
            if (fromDate) {
                cqlFilter += ' and detectiontime >= \'' + moment(new Date(fromDate)).format('YYYY-MM-DD') + '\'';
            }
            if (toDate) {
                cqlFilter += ' and detectiontime <= \'' + moment(new Date(toDate)).format('YYYY-MM-DD') + '\'';
            }
            return cqlFilter;
        }

        function createAllDetectionsLayer() {
            return new OpenLayers.Layer.WMS(
                'Detections',
                '/geoserver/wms',
                {
                    layers: 'oztrack:positionfixlayer',
                    styles: 'oztrack_positionfixlayer',
                    cql_filter: buildAllDetectionsFilter(),
                    format: 'image/png',
                    transparent: true
                },
                {
                    isBaseLayer: false,
                    tileSize: new OpenLayers.Size(512,512),
                    metadata: {category: 'project'}
                }
            );
        }
    };
}(window.OzTrack = window.OzTrack || {}));
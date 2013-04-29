(function(OzTrack) {
    OzTrack.AnalysisMap = function(div, options) {
        if (!(this instanceof OzTrack.AnalysisMap)) {
            throw new Error("Constructor called as a function");
        }
        var that = this;

        that.projection900913 = new OpenLayers.Projection("EPSG:900913");
        that.projection4326 = new OpenLayers.Projection("EPSG:4326");

        that.projectId = options.projectId;
        that.crosses180 = options.crosses180;
        that.dataLicence = options.dataLicence;
        that.animalIds = options.animalIds;
        that.animalVisible = {};
        $.each(that.animalIds, function(i, animalId) {
            that.animalVisible[animalId] = true;
        });
        that.projectBounds = options.projectBounds.clone().transform(that.projection4326, that.projection900913);
        that.animalBounds = {};
        $.each(options.animalBounds, function(animalId, bounds) {
            that.animalBounds[animalId] = bounds.clone().transform(that.projection4326, that.projection900913);
        });
        that.animalColours = options.animalColours;
        that.minDate = options.minDate;
        that.maxDate = options.maxDate;
        that.onAnalysisCreate = options.onAnalysisCreate;
        that.onAnalysisError = options.onAnalysisError;
        that.onAnalysisSuccess = options.onAnalysisSuccess;
        that.onUpdateAnimalInfoFromWFS = options.onUpdateAnimalInfoFromWFS;
        that.onUpdateAnimalInfoFromLayer = options.onUpdateAnimalInfoFromLayer;
        that.onUpdateAnimalInfoForAnalysis = options.onUpdateAnimalInfoForAnalysis;
        that.onUpdateAnimalInfoFromKML = options.onUpdateAnimalInfoFromKML;

        that.detectionLayers = [];
        that.trajectoryLayers = [];
        that.projectMapLayers = {};
        that.wfsLayers = {};
        that.analyses = {};
        that.projectMapLayerIdSeq = 0;
        that.wfsLayerIdSeq = 0;

        that.map = new OpenLayers.Map(div, {
            theme: null,
            units : 'm',
            projection : that.projection900913,
            displayProjection : that.projection4326,
            controls: []
        });
        that.map.Z_INDEX_BASE.Popup = 1500;
        that.map.addControl(new OpenLayers.Control.Zoom());
        that.map.addControl(new OpenLayers.Control.Attribution());
        that.map.addControl(new OpenLayers.Control.ScaleLine());
        that.map.addControl(new OpenLayers.Control({displayClass: 'projectMapLogo'}));
        if (that.dataLicence) {
            that.map.addControl(new OzTrack.OpenLayers.Control.OzTrackDataLicence({
                dataLicence: that.dataLicence
            }));
        }
        
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
        that.map.addControl(new OzTrackNavToolbar());

        that.layerSwitcher = new OzTrack.OpenLayers.Control.OzTrackLayerSwitcher({
            categoryLabels: {
                'base': 'Base layer',
                'environment': 'Environmental layers',
                'project': 'Project layers',
                'analysis': 'Analysis layers'
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

        that.emptyBaseLayer = new OpenLayers.Layer("None", {
            isBaseLayer: true,
            numZoomLevels : 22,
            metadata: {category: 'base'}
        });
        that.map.addLayer(that.emptyBaseLayer);

        that.bathymetryLayer = new OpenLayers.Layer.WMS(
            'Bathymetry',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:gebco_08',
                styles: 'oztrack_bathymetry',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.gebco.net/">The GEBCO_08 Grid, version 20091120 (bathymetry)</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.bathymetryLayer);

        that.elevationLayer = new OpenLayers.Layer.WMS(
            'Elevation',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:gebco_08',
                styles: 'oztrack_elevation',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.gebco.net/">The GEBCO_08 Grid, version 20091120 (elevation)</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.elevationLayer);

        that.dlcdClass = new OpenLayers.Layer.WMS(
            'Land Cover',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:dlcd-class',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.ga.gov.au/earth-observation/landcover.html">Dynamic Land Cover Dataset</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.dlcdClass);

        that.nvisGroups = new OpenLayers.Layer.WMS(
            'NVIS Groups',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:nvis_4_1_aust_mvg',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B116AACA6-9E11-43E6-AD68-75AE380504CD%7D">NVIS 4.1 Major Vegetation Groups</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.nvisGroups);

        that.nvisSubgroups = new OpenLayers.Layer.WMS(
            'NVIS Subgroups',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:nvis_4_1_aust_mvs',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B245434BF-95D1-4C3E-8104-EC4B2988782D%7D">NVIS 4.1 Major Vegetation Subgroups</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.nvisSubgroups);

        that.fireFrequency = new OpenLayers.Layer.WMS(
            'Fire Frequency',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:fire-frequency-avhrr-1997-2009',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://data.auscover.org.au/geonetwork/srv/en/main.home?uuid=3535a8c1-940e-4f60-b55b-24185730acba">Fire Frequency - AVHRR</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.fireFrequency);

        that.capadLand = new OpenLayers.Layer.WMS(
            'CAPAD',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:capad10_external_all',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution:
                    'CAPAD 2010' +
                    ' <a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={C4B70940-75BC-4114-B935-D28EE8A52937}">[1]</a>' +
                    ' <a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={0E24A4B5-BA44-48D5-AF2F-7F4749F4EA2D}">[2]</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.capadLand);

        that.capadMarine = new OpenLayers.Layer.WMS(
            'CAPAD Marine',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:capad10_m_external_all',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution:
                    'CAPAD 2010 Marine' +
                    ' <a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B4970516C-6F4A-4B1E-AF33-AB6BDE6B008A%7D">[1]</a>' +
                    ' <a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B905AD083-39A0-41C6-B2F9-CBF5E0B86A3C%7D">[2]</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.capadMarine);
        
        that.nrmRegions = new OpenLayers.Layer.WMS(
            'NRM Regions',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:nrm_regions_2010',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={FA68F769-550B-4605-A0D5-50B10ECD0EB9}">NRM Regions 2010</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.nrmRegions);

        that.ibraRegions = new OpenLayers.Layer.WMS(
            'IBRA Regions',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:ibra7_regions',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B573FA186-1997-4F8B-BCF8-58B5876A156B%7D">IBRA 7 Regions</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.ibraRegions);

        that.ibraSubregions = new OpenLayers.Layer.WMS(
            'IBRA Subregions',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:ibra7_subregions',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7BC88F4317-42B0-4D4B-AC5D-47F6ACF1A24F%7D">IBRA 7 Subregions</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.ibraSubregions);

        that.imcraProvincial = new OpenLayers.Layer.WMS(
            'IMCRA Provincial Bioregions',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:imcra4_pb',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B30DA5FD4-AE08-405B-9F55-7E1833C230A4%7D">IMCRA 4 Provincial Bioregions</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.imcraProvincial);

        that.imcraMesoscale = new OpenLayers.Layer.WMS(
            'IMCRA Mesoscale Bioregions',
            '/geoserver/gwc/service/wms',
            {
                layers: 'oztrack:imcra4_meso',
                format: 'image/png',
                tiled: true
            },
            {
                visibility: false,
                isBaseLayer: false,
                wrapDateLine: true,
                attribution: '<a target="_blank" href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7BA0D9F8EE-4261-438A-8ADE-EFF664EFF55C%7D">IMCRA 4 Meso-scale Bioregions</a>',
                metadata: {category: 'environment'}
            }
        );
        that.map.addLayer(that.imcraMesoscale);

        that.startEndStyleMap = createStartEndPointsStyleMap();
        that.polygonStyleMap = createPolygonStyleMap();

        that.allDetectionsLayer = createDetectionLayer({}, 'project');
        that.map.addLayer(that.allDetectionsLayer.getWMSLayer());

        that.allTrajectoriesLayer = createTrajectoryLayer({}, 'project');
        that.map.addLayer(that.allTrajectoriesLayer.getWMSLayer());

        that.map.addLayer(createWFSLayer('Start and End Points', 'StartEnd', {
            projectId : that.projectId,
            queryType : 'START_END'
        }, that.startEndStyleMap, 'project'));

        function coordString(geometry) {
            var x = geometry.x || geometry.lon;
            var y = geometry.y || geometry.lat;
            var round = function(n) {return (Math.round(x * 1000000) / 1000000);};
            return '(' + round(x) + ', ' + round(y) + ')';
        }
        var capadPropertyNames = [
            'NAME',
            'TYPE',
            'IUCN',
            'GAZ_AREA',
            'GIS_AREA'
        ];
        var capadSummary = function(feature) {
            var areaInHa = (feature.attributes.GAZ_AREA || feature.attributes.GIS_AREA);
            var areaInKm2 = areaInHa / 100.0;
            var areaRounded = Math.round(areaInKm2 * 1000) / 1000.0;
            return $('<span>')
                .append(feature.attributes.NAME + ', ' + areaRounded + ' km<sup>2</sup><br />')
                .append(feature.attributes.TYPE + ', IUCN Code ' + feature.attributes.IUCN);
        };
        var getFeatureInfoControl = new OzTrack.OpenLayers.Control.WMSGetFeatureInfo({
            url: '/geoserver/wms',
            layerUrls: ['/geoserver/gwc/service/wms'],
            layerDetails: [
                {
                    layer: that.bathymetryLayer,
                    propertyNames: [
                        'GRAY_INDEX'
                    ],
                    summary: function(feature) {
                        return (feature.attributes.GRAY_INDEX < 0)
                            ? $('<span>').append('Depth: ' + (-1 * feature.attributes.GRAY_INDEX) + ' m')
                            : $();
                    }
                },
                {
                    layer: that.elevationLayer,
                    propertyNames: [
                        'GRAY_INDEX'
                    ],
                    summary: function(feature) {
                        return (feature.attributes.GRAY_INDEX >= 0)
                            ? $('<span>').append('Elevation: ' + feature.attributes.GRAY_INDEX + ' m')
                            : $();
                    }
                },
                {
                    layer: that.dlcdClass,
                    propertyNames: [
                        'GRAY_INDEX'
                    ],
                    summary: function(feature) {
                        return $('<span>').append('Class: ' + Math.floor(feature.attributes.GRAY_INDEX));
                    }
                },
                {
                    layer: that.nvisGroups,
                    propertyNames: [
                        'PALETTE_INDEX'
                    ],
                    summary: function(feature) {
                        return $('<span>').append('MVG Number: ' + Math.floor(feature.attributes.PALETTE_INDEX));
                    }
                },
                {
                    layer: that.nvisSubgroups,
                    propertyNames: [
                        'PALETTE_INDEX'
                    ],
                    summary: function(feature) {
                        return $('<span>').append('MVS Number: ' + Math.floor(feature.attributes.PALETTE_INDEX));
                    }
                },
                {
                    layer: that.fireFrequency,
                    propertyNames: [
                        'GRAY_INDEX'
                    ],
                    summary: function(feature) {
                        return $('<span>').append('Fires per annum: ' + Math.floor(feature.attributes.GRAY_INDEX));
                    }
                },
                {
                    layer: that.capadLand,
                    propertyNames: capadPropertyNames,
                    summary: capadSummary
                },
                {
                    layer: that.capadMarine,
                    propertyNames: capadPropertyNames,
                    summary: capadSummary
                },
                {
                    layer: that.nrmRegions,
                    propertyNames: [
                        'NRM_REGION',
                        'NRM_BODY'
                    ],
                    summary: function(feature) {
                        return $('<span>')
                            .append(feature.attributes.NRM_REGION + '<br />')
                            .append(feature.attributes.NRM_BODY);
                    }
                },
                {
                    layer: that.ibraRegions,
                    propertyNames: [
                        'REG_CODE_7',
                        'REG_NAME_7',
                        'SQ_KM'
                    ],
                    summary: function(feature) {
                        return $('<span>')
                            .append(feature.attributes.REG_NAME_7 + ' (' + feature.attributes.REG_CODE_7  + ')')
                            .append(', ' + feature.attributes.SQ_KM + ' km<sup>2</sup>');
                    }
                },
                {
                    layer: that.ibraSubregions,
                    propertyNames: [
                        'SUB_CODE_7',
                        'SUB_NAME_7',
                        'REG_CODE_7',
                        'REG_NAME_7',
                        'SQ_KM'
                    ],
                    summary: function(feature) {
                        return $('<span>')
                            .append(feature.attributes.SUB_NAME_7 + ' (' + feature.attributes.SUB_CODE_7  + ')')
                            .append(', ' + feature.attributes.SQ_KM + ' km<sup>2</sup>');
                    }
                },
                {
                    layer: that.imcraProvincial,
                    propertyNames: [
                         'PB_NAME',
                         'PB_NUM',
                         'WATER_TYPE',
                         'AREA_KM2'
                     ],
                     summary: function(feature) {
                         return $('<span>')
                             .append('Number ' + feature.attributes.PB_NUM + ', ')
                             .append(feature.attributes.PB_NAME + '<br />')
                             .append(feature.attributes.WATER_TYPE + ', ' + feature.attributes.AREA_KM2 + ' km<sup>2</sup>');
                     }
                },
                {
                    layer: that.imcraMesoscale,
                    propertyNames: [
                         'MESO_NAME',
                         'MESO_NUM',
                         'MESO_ABBR',
                         'WATER_TYPE',
                         'AREA_KM2'
                     ],
                     summary: function(feature) {
                         return $('<span>')
                             .append('Number ' + feature.attributes.MESO_NUM + ', ')
                             .append(feature.attributes.MESO_NAME + ' (' + feature.attributes.MESO_ABBR  + ')<br />')
                             .append(feature.attributes.WATER_TYPE + ', ' + feature.attributes.AREA_KM2 + ' km<sup>2</sup>');
                     }
                },
                {
                    layer: that.allDetectionsLayer.getWMSLayer(),
                    propertyNames: [
                        'animal_id',
                        'detectiontime',
                        'locationgeometry'
                    ],
                    summary: function(feature) {
                        return $('<span>')
                            .append($('#animalLabel-' + feature.attributes.animal_id).text())
                            .append(' at ' + moment(feature.attributes.detectiontime, 'YYYY-MM-DDTHH:mm:ss').format('YYYY-MM-DD HH:mm:ss'))
                            .append(' ' + coordString(feature.geometry));
                    }
                },
                {
                    layer: that.allTrajectoriesLayer.getWMSLayer(),
                    propertyNames: [
                        'animal_id',
                        'startdetectiontime',
                        'enddetectiontime',
                        'trajectorygeometry'
                    ],
                    summary: function(feature) {
                        return $('<span>')
                            .append($('#animalLabel-' + feature.attributes.animal_id).text())
                            .append(' from ' + moment(feature.attributes.startdetectiontime, 'YYYY-MM-DDTHH:mm:ss').format('YYYY-MM-DD HH:mm:ss'))
                            .append(' ' + coordString(feature.geometry.components[0]) + '<br />')
                            .append(' to ' + moment(feature.attributes.enddetectiontime, 'YYYY-MM-DDTHH:mm:ss').format('YYYY-MM-DD HH:mm:ss'))
                            .append(' ' + coordString(feature.geometry.components[1]));
                    }
                }
            ],
            queryVisible: true,
            maxFeatures: 10,
            infoFormat: 'application/vnd.ogc.gml',
            eventListeners: {
                getfeatureinfo: function(event) {
                    var control = this;
                    var lonlat900913 = that.map.getLonLatFromPixel(event.xy);
                    var lonlat4326 = lonlat900913.clone().transform(that.projection900913, that.projection4326);
                    var content = $('<div>');
                    var innerContent = $('<div>').addClass('featureInfoContent').appendTo(content);
                    innerContent.append($('<p>')
                        .css('font-weight', 'bold')
                        .css('width', '400px')
                        .append('Layer Information at ' + coordString(lonlat4326))
                    );
                    if (event.features && (event.features.length > 0)) {
                        innerContent.append($.map(control.layerDetails, function(layerDetail) {
                            var layerFeatures = $.grep(event.features, function(feature) {
                                return layerDetail.layer.params.LAYERS === (feature.gml.featureNSPrefix + ':' + feature.gml.featureType);
                            });
                            var summaries = $.map(layerFeatures, function(feature) {
                                return layerDetail.summary(feature);
                            });
                            var nonEmptySummaries = $.grep(summaries, function(summary) {
                                return summary.length > 0;
                            });
                            // Filter summaries for uniqueness because GetFeatureInfo may return
                            // identical features from different layers having the same feature type.
                            //
                            // In this case, we expect only one summary function in layerDetails to
                            // return a non-empty result for each such feature; this means, though,
                            // that one layerDetail will return N summaries for N identical features.
                            //
                            // Example: the Bathymetry and Elevation layers are both from the gebco_08
                            // feature type, differing only by style; without this uniqueness filter,
                            // we would get two lines of either "Depth: X m" or "Elevation: X m".
                            var uniqueSummaries = [], summaryFound = {};
                            $.each(nonEmptySummaries, function(i, summary) {
                                var summaryHtml = $('<div>').append(summary).html();
                                if (!summaryFound[summaryHtml]) {
                                    summaryFound[summaryHtml] = true;
                                    uniqueSummaries.push(summary);
                                }
                            });
                            return (uniqueSummaries.length == 0) ? [] : [
                                $('<div>').addClass('layerInfoTitle').css('margin-bottom', '4px').text(layerDetail.layer.name).get(0),
                                $('<ul>').append($.map(uniqueSummaries, function(summary) {
                                    return $('<li>').append(summary);
                                })).get(0)
                            ];
                        }));
                        // Remove other feature info popups and then show our own.
                        var popups = that.map.popups.slice(0);
                        $.each(popups, function(i, popup) {
                            if ($(popup.contentHTML).hasClass('featureInfoContent')) {
                                that.map.removePopup(popup);
                            }
                        });
                        that.map.addPopup(new OpenLayers.Popup.FramedCloud(null, lonlat900913, null, content.html(), null, true));
                    }
                }
            }
        });
        that.map.addControl(getFeatureInfoControl);
        getFeatureInfoControl.activate();
        
        that.map.zoomToExtent(that.projectBounds, false);

        function createPointStyleMap() {
            var styleContext = {
                getColour : function(feature) {
                    return that.animalColours[feature.attributes.animalId];
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
                    return that.animalColours[feature.attributes.id.value];
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

        that.addProjectMapLayer = function() {
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
                var trajectoryLayer = createTrajectoryLayer(params, 'analysis');
                that.map.addLayer(trajectoryLayer.getWMSLayer());
            }
            else if (queryTypeValue == "POINTS") {
                var detectionLayer = createDetectionLayer(params, 'analysis');
                that.map.addLayer(detectionLayer.getWMSLayer());
            }
            else if (queryTypeValue == "START_END") {
                that.map.addLayer(createWFSLayer(layerName, 'StartEnd', params, that.startEndStyleMap, 'analysis'));
            }
            else {
                createAnalysisLayer(params, layerName);
            }
        };

        function createAnalysisLayer(params, layerName) {
            $.ajax({
                url: '/projects/' + that.projectId + '/analyses',
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
            var styleMap = that.polygonStyleMap;
            var extractStyles = false;
            if ((analysis.params.queryType == "HEATMAP_POINT") || (analysis.params.queryType == "HEATMAP_LINE")) {
                styleMap = null;
                extractStyles = true;
            }
            var queryOverlay = new OpenLayers.Layer.Vector(layerName, {
                styleMap : styleMap,
                metadata: {category: 'analysis'}
            });
            if (that.analyses[analysis.id]) {
                that.analyses[analysis.id].layer = queryOverlay;
            }
            var protocol = new OpenLayers.Protocol.HTTP({
                url : analysis.resultUrl,
                format : new OpenLayers.Format.KML({
                    extractStyles: extractStyles,
                    extractAttributes: true,
                    maxDepth: 2,
                    internalProjection: that.projection900913,
                    externalProjection: that.projection4326,
                    kmlns: "http://oztrack.org/xmlns#"
                })
            });
            var callback = function(resp) {
                that.loadingPanel.decreaseCounter();
                if (resp.code == OpenLayers.Protocol.Response.SUCCESS) {
                    if (that.crosses180) {
                        var primeMeridianLon = that.map.baseLayer.maxExtent.getCenterLonLat().lon;
                        var boundsWidth = that.map.baseLayer.maxExtent.getWidth();
                        $.each(resp.features, function(i, feature) {
                            var geometries = feature.components || [feature.geometry];
                            $.each(geometries, function(j, geometry) {
                                $.each(geometry.getVertices(), function(k, vertex) {
                                    if (vertex.x < primeMeridianLon) {
                                        vertex.x += boundsWidth;
                                    }
                                });
                            });
                        });
                    }
                    queryOverlay.addFeatures(resp.features);
                    updateAnimalInfoFromKML(analysis, resp.features);
                    that.onAnalysisSuccess();
                }
                else {
                    that.onAnalysisError(jQuery(resp.priv.responseText).find('error').text() || 'Error processing request');
                }
            };
            protocol.read({
                callback : callback
            });
            that.map.addLayer(queryOverlay);
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
        
        that.deleteProjectMapLayer = function(id) {
            if (!confirm('This will delete the layer for all animals. Do you wish to continue?')) {
                return;
            }
            if (that.projectMapLayers[id]) {
                that.projectMapLayers[id].getWMSLayer().destroy();
                delete that.projectMapLayers[id];
            }
            that.detectionLayers = $.grep(that.detectionLayers, function(e, i) {return e.id != id});
            that.trajectoryLayers = $.grep(that.trajectoryLayers, function(e, i) {return e.id != id});
            $('.projectMapLayerInfo-' + id).fadeOut().remove();
        };

        that.deleteWFSLayer = function(id) {
            if (!confirm('This will delete the layer for all animals. Do you wish to continue?')) {
                return;
            }
            if (that.wfsLayers[id]) {
                that.wfsLayers[id].destroy();
                delete that.wfsLayers[id];
            }
            $('.wfsLayerInfo-' + id).fadeOut().remove();
        };

        function createDetectionLayer(params, category) {
            function buildFilter(params) {
                // If supplied, use param to filter animals; otherwise, include all animals.
                var cqlFilterAnimalIds = params.animalIds ? params.animalIds.split(',') : that.animalIds;
                
                // Exclude animals not currently visible on the map.
                cqlFilterAnimalIds = $.grep(cqlFilterAnimalIds, function(e, i) {return that.animalVisible[e]});
                
                // If filter is empty, include dummy ID (i.e. -1) that will never be matched.
                // This prevents the CQL_FILTER parameter from being syntactically invalid.
                cqlFilterAnimalIds = (cqlFilterAnimalIds.length > 0) ? cqlFilterAnimalIds : [-1];
                
                var cqlFilter = 'project_id = ' + that.projectId + ' and deleted = false';
                cqlFilter += ' and animal_id in (' + cqlFilterAnimalIds.join(',') + ')';
                if (params.fromDate) {
                    cqlFilter += ' and detectiontime >= \'' + moment(new Date(params.fromDate)).format('YYYY-MM-DD') + '\'';
                }
                if (params.toDate) {
                    cqlFilter += ' and detectiontime <= \'' + moment(new Date(params.toDate)).format('YYYY-MM-DD') + '\'';
                }
                return cqlFilter;
            }
            var title = 'Detections';
            var wmsLayer = new OpenLayers.Layer.WMS(
                title,
                '/geoserver/wms',
                {
                    layers: 'oztrack:positionfixlayer',
                    styles: 'oztrack_positionfixlayer',
                    cql_filter: buildFilter(params),
                    format: 'image/png',
                    transparent: true
                },
                {
                    isBaseLayer: false,
                    tileSize: new OpenLayers.Size(512,512),
                    metadata: {category: category}
                }
            );
            var layer = {
                id: that.projectMapLayerIdSeq++,
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
            that.projectMapLayers[layer.id] = layer;
            that.detectionLayers.push(layer);
            updateAnimalInfoFromLayer(layer);
            that.onAnalysisSuccess();
            return layer;
        }
        
        function updateDetectionLayers() {
            for (var i = 0; i < that.detectionLayers.length; i++) {
                that.detectionLayers[i].getWMSLayer().params['CQL_FILTER'] = that.detectionLayers[i].getCQLFilter();
                that.detectionLayers[i].getWMSLayer().redraw();
            }
        }
        
        function createTrajectoryLayer(params, category) {
            function buildFilter(params) {
                // If supplied, use param to filter animals; otherwise, include all animals.
                var cqlFilterAnimalIds = params.animalIds ? params.animalIds.split(',') : that.animalIds;
                
                // Exclude animals not currently visible on the map.
                cqlFilterAnimalIds = $.grep(cqlFilterAnimalIds, function(e, i) {return that.animalVisible[e]});
                
                // If filter is empty, include dummy ID (i.e. -1) that will never be matched.
                // This prevents the CQL_FILTER parameter from being syntactically invalid.
                cqlFilterAnimalIds = (cqlFilterAnimalIds.length > 0) ? cqlFilterAnimalIds : [-1];

                var cqlFilter = 'project_id = ' + that.projectId;
                cqlFilter += ' and animal_id in (' + cqlFilterAnimalIds.join(',') + ')';
                if (params.fromDate) {
                    cqlFilter += ' and startdetectiontime >= \'' + moment(new Date(params.fromDate)).format('YYYY-MM-DD') + '\'';
                }
                if (params.toDate) {
                    cqlFilter += ' and enddetectiontime <= \'' + moment(new Date(params.toDate)).format('YYYY-MM-DD') + '\'';
                }
                return cqlFilter;
            }
            var title = 'Trajectory';
            var wmsLayer = new OpenLayers.Layer.WMS(
                title,
                '/geoserver/wms',
                {
                    layers: 'oztrack:trajectorylayer',
                    styles: 'oztrack_trajectorylayer',
                    cql_filter: buildFilter(params),
                    format: 'image/png',
                    transparent: true
                },
                {
                    isBaseLayer: false,
                    tileSize: new OpenLayers.Size(512,512),
                    metadata: {category: category}
                }
            );
            var layer = {
                id: that.projectMapLayerIdSeq++,
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
            that.projectMapLayers[layer.id] = layer;
            that.trajectoryLayers.push(layer);
            updateAnimalInfoFromLayer(layer);
            that.onAnalysisSuccess();
            return layer;
        }
        
        function updateTrajectoryLayers() {
            for (var i = 0; i < that.trajectoryLayers.length; i++) {
                that.trajectoryLayers[i].getWMSLayer().params['CQL_FILTER'] = that.trajectoryLayers[i].getCQLFilter();
                that.trajectoryLayers[i].getWMSLayer().redraw();
            }
        }

        function createWFSLayer(layerName, featureType, params, styleMap, category) {
            var wfsLayerId = that.wfsLayerIdSeq++;
            var wfsLayer = new OpenLayers.Layer.Vector(layerName, {
                projection : that.projection4326,
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
                        that.onAnalysisSuccess();
                    }
                },
                metadata: {category: category}
            });
            that.wfsLayers[wfsLayerId] = wfsLayer;
            return wfsLayer;
        }
        
        function updateAnimalInfoFromWFS(wfsLayer, wfsLayerId) {
            var animalProcessed = {};
            for (var key in wfsLayer.features) {
                var feature = wfsLayer.features[key];
                if (feature.attributes && feature.attributes.animalId) {
                    if (animalProcessed[feature.attributes.animalId]) {
                        continue;
                    }
                    animalProcessed[feature.attributes.animalId] = true;
                    feature.renderIntent = "default";
                    that.onUpdateAnimalInfoFromWFS(
                        wfsLayer.name,
                        wfsLayerId,
                        feature.attributes.animalId,
                        feature.attributes.fromDate,
                        feature.attributes.toDate
                    );
                }
            }
        }
        
        function updateAnimalInfoFromLayer(layer) {
            var layerAnimalIds = layer.getParams().animalIds ? layer.getParams().animalIds.split(',') : that.animalIds
            for (var i = 0; i < layerAnimalIds.length; i++) {
                that.onUpdateAnimalInfoFromLayer(
                    layer.getTitle(),
                    layer.id,
                    layerAnimalIds[i],
                    layer.getParams().fromDate || moment(that.minDate).format('YYYY-MM-DD'),
                    layer.getParams().toDate || moment(that.maxDate).format('YYYY-MM-DD')
                );
            }
        }
        
        function updateAnimalInfoForAnalysis(layerName, analysis) {
            for (var i = 0; i < analysis.params.animalIds.length; i++) {
                that.onUpdateAnimalInfoForAnalysis(layerName, analysis.params.animalIds[i], analysis);
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
                    animalFeature.renderIntent = "default";
                    animalFeature.layer.drawFeature(animalFeature);
                }
                that.onUpdateAnimalInfoFromKML(
                    animalId,
                    analysis,
                    (animalFeature && animalFeature.attributes.area) ? animalFeature.attributes.area.value : null,
                    (animalFeature && !analysis.params.hValue && animalFeature.attributes.hval) ? animalFeature.attributes.hval.value : null
                );
            }
        }

        that.zoomToAnimal = function(animalId) {
            if (that.animalBounds[animalId]) {
                that.map.zoomToExtent(that.animalBounds[animalId], false);
            }
        };

        that.toggleAllAnimalFeatures = function(animalId, visible) {
            that.animalVisible[animalId] = visible;
            $("#animalInfo-" + animalId).find(':checkbox').attr("checked", visible);
        };

        that.toggleAllAnimalFeaturesCommit = function() {
            updateDetectionLayers();
            updateTrajectoryLayers();
            function getVectorLayers() {
                var vectorLayers = [];
                if (that.map.layers) {
                    for (var i = 0; i < that.map.layers.length; i++) {
                        if (that.map.layers[i].features) {
                            vectorLayers.push(that.map.layers[i]);
                        }
                    }
                }
                return vectorLayers;
            }
            var vectorLayers = getVectorLayers();
            for (i = 0; i < that.animalIds.length; i++) {
                var animalId = that.animalIds[i];
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
                            feature.renderIntent = that.animalVisible[animalId] ? 'default' : 'temporary';
                            feature.layer.drawFeature(feature);
                        }
                    }
                }
            }
        };

        that.updateSize = function() {
            that.map.updateSize();

            // Workaround zooming bug:
            // Map resize displays google layer at incorrect zoom level
            // Calling pan results in zoom levels for all layers getting back in synch
            // http://gis.stackexchange.com/questions/30075/map-resize-displays-google-layer-at-incorrect-zoom-level
            that.map.pan(1, 0, null);
            that.map.pan(-1, 0, null);
        };
    };
}(window.OzTrack = window.OzTrack || {}));
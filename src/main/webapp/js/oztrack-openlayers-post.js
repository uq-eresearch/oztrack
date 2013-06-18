/*global OpenLayers*/
// Configure OpenLayers image path; needed because we use wro4j.
// http://dev.openlayers.org/apidocs/files/OpenLayers-js.html#OpenLayers.ImgPath
OpenLayers.ImgPath = "/js/openlayers/img/";

// Hack to LoadingPanel control: create svg element instead of div so we can use pointer-events.
// The pointer-events CSS property was originally from SVG and is supported as an experimental
// property for HTML elements in Firefox and Chrome; however, Internet Explorer apparently have
// no plans to support it except on SVG elements. We use pointer-events: none so that operations
// can still be performed on the map even when the loading panel is overlayed.
OpenLayers.Control.LoadingPanel.prototype.draw = function(px) {
    if (this.div == null) {
        this.div = document.createElement('svg');
        this.div.id = this.id;
        this.div.style.position = 'absolute';
        this.div.className = this.displayClass;
        if (!this.allowSelection) {
            this.div.className += " olControlNoSelect";
            this.div.setAttribute("unselectable", "on", 0);
            this.div.onselectstart = OpenLayers.Function.False; 
        }    
        if (this.title != "") {
            this.div.title = this.title;
        }
    }
    OpenLayers.Control.prototype.draw.apply(this, arguments);
    return this.div;
};

(function(OzTrack) {
    OzTrack.OpenLayers = OzTrack.OpenLayers || {};
    OzTrack.OpenLayers.Control = OzTrack.OpenLayers.Control || {};

    OzTrack.OpenLayers.Control.ZoomToExtent = OpenLayers.Class(OpenLayers.Control.Button, {
        initialize: function(options) {
            this.extent = options.extent;
            this.title = "Zoom to extent";
            options.displayClass = options.displayClass || "OzTrackOpenLayersControlZoomToExtent";
            OpenLayers.Control.Button.prototype.initialize.apply(this, [options]);
        },
        trigger: function() {
            this.map.zoomToExtent(this.extent, false);
        },
        CLASS_NAME: "OzTrack.OpenLayers.Control.ZoomToExtent"
    });

    OzTrack.OpenLayers.Control.OzTrackDataLicence = OpenLayers.Class(OpenLayers.Control, {
        initialize: function(options) {
            options = options || {};
            this.dataLicence = options.dataLicence;
            options.displayClass = options.displayClass || "OzTrackOpenLayersControlOzTrackDataLicence";
            OpenLayers.Control.prototype.initialize.apply(this, [options]);
        },
        draw: function (px) {
            if (this.div == null) {
                this.div =
                    $('<div>')
                        .addClass(this.displayClass)
                        .append($('<a>')
                            .attr('target', '_blank')
                            .attr('href', this.dataLicence.infoUrl)
                            .attr('title',
                                'Data in this project are made available under the ' +
                                this.dataLicence.title + '.'
                            )
                            .append($('<img>').attr('src', this.dataLicence.imageUrl))
                        )
                        .get(0);
            }
            if (px != null) {
                this.position = px.clone();
            }
            this.moveTo(this.position);
            return this.div;
        }
    });

    OzTrack.OpenLayers.Control.OzTrackLayerSwitcher = OpenLayers.Class(OpenLayers.Control, {
        layersDiv: null,
        minimizeDiv: null,
        maximizeDiv: null,
        layerInfoDialogs: {},

        initialize: function(options) {
            options = options || {};
            this.categories = {};
            for (categoryId in options.categoryLabels) {
                var categoryLabel = options.categoryLabels[categoryId];
                this.categories[categoryId] = {
                    label: categoryLabel,
                    layers: [],
                    labelDiv: null,
                    layersDiv: null
                };
            }
            options.displayClass = options.displayClass || "OzTrackOpenLayersControlOzTrackLayerSwitcher";
            OpenLayers.Control.prototype.initialize.apply(this, [options]);
        },

        clearLayersArray: function(layersType) {
            $('.layer-opacity-popover-icon').popover('hide').popover('destroy');
            for (categoryId in this.categories) {
                var category = this.categories[categoryId];
                category.layers = [];
                category.layersDiv.innerHTML = "";
            }
        },

        destroy: function() {
            this.clearLayersArray();

            this.map.events.un({
                buttonclick: this.onButtonClick,
                addlayer: this.redraw,
                changelayer: this.redraw,
                removelayer: this.redraw,
                changebaselayer: this.redraw,
                scope: this
            });
            this.events.unregister("buttonclick", this, this.onButtonClick);

            OpenLayers.Control.prototype.destroy.apply(this, arguments);
        },

        setMap: function(map) {
            OpenLayers.Control.prototype.setMap.apply(this, arguments);

            this.map.events.on({
                addlayer: this.redraw,
                changelayer: this.redraw,
                removelayer: this.redraw,
                changebaselayer: this.redraw,
                scope: this
            });
            if (this.outsideViewport) {
                this.events.attachToElement(this.div);
                this.events.register("buttonclick", this, this.onButtonClick);
            }
            else {
                this.map.events.register("buttonclick", this, this.onButtonClick);
            }
        },

        draw: function() {
            OpenLayers.Control.prototype.draw.apply(this);
            this.loadContents();
            if (!this.outsideViewport) {
                this.minimizeControl();
            }
            this.redraw();
            return this.div;
        },

        onButtonClick: function(evt) {
            var button = evt.buttonElement;
            if (button === this.minimizeDiv) {
                this.minimizeControl();
            }
            else if (button === this.maximizeDiv) {
                this.maximizeControl();
            }
            else if (button._layerSwitcher === this.id) {
                if (button["for"]) {
                    button = document.getElementById(button["for"]);
                }
                if (!button.disabled) {
                    button.checked = (button.type == "radio") || !button.checked;
                    var layer = this.map.getLayer(button._layer);
                    if (layer.isBaseLayer) {
                        this.map.setBaseLayer(layer);
                    }
                    else {
                        layer.setVisibility(button.checked);
                    }
                }
            }
        },

        redraw: function(evt) {
            var that = this;

            if (evt) {
                // Ignore changes in opacity - irrelevant to layer switcher
                if ((evt.type === 'changelayer') && (evt.property === 'opacity')) {
                    return;
                }
            }

            this.clearLayersArray();

            var layers = this.map.layers.slice();
            var len = layers.length;
            for (var i = 0; i < len; i++) {
                var layer = layers[i];
                if (!layer.displayInLayerSwitcher) {
                    continue;
                }

                var layerDiv = $('<div class="layerDiv">').get(0);

                function addLayerOpacitySpan(map, layer) {
                    function buildPopoverContent() {
                        function updateLayerOpacity(value) {
                            layer.setOpacity(value);
                            percentDiv.text((value * 100).toFixed(0) + '%');
                        }
                        var percentDiv = $('<div>');
                        var sliderDiv = $('<div>')
                            .css('height', '100px')
                            .css('margin', '10px')
                            .slider({
                                min: 0,
                                max: 1.0,
                                step: 0.05,
                                value: layer.opacity,
                                orientation: "vertical",
                                change: function(event, ui) {
                                    updateLayerOpacity(ui.value);
                                }
                            });
                        updateLayerOpacity(layer.opacity);
                        var popoverContent = $('<div>')
                            .css('text-align', 'center')
                            .append($('<div>')
                                .append($('<div>')
                                    .css('display', 'inline-block')
                                    .append(sliderDiv)
                                )
                            )
                            .append($('<div>')
                                .append(percentDiv)
                            );
                        return popoverContent;
                    }
                    var span = $('<span>')
                        .addClass('icon-white')
                        .addClass('icon-wrench')
                        .addClass('layer-opacity-popover-icon')
                        .css('float', 'right')
                        .css('cursor', 'pointer')
                        .attr('title', 'Opacity')
                        .popover({
                            container: 'body',
                            placement: 'left',
                            trigger: 'click',
                            html: true,
                            content: buildPopoverContent
                        });
                    $(layerDiv).append(span);
                }
                function addLayerMoveSpan(map, layer, delta) {
                    var layerMoveSpan = $('<span>').addClass('icon-white').css('float', 'right');
                    layerMoveSpan.addClass((delta > 0) ? 'icon-arrow-down' : 'icon-arrow-up');
                    var targetLayer = map.layers[map.getLayerIndex(layer) + delta];
                    if (targetLayer && (layer.metadata.category == targetLayer.metadata.category)) {
                        layerMoveSpan
                            .css('cursor', 'pointer')
                            .attr('title', 'Move ' + ((delta > 0) ? 'down' : 'up'))
                            .click(function(e) {
                                var targetLayer = map.layers[map.getLayerIndex(layer) + delta];
                                if (targetLayer && (layer.metadata.category == targetLayer.metadata.category)) {
                                    map.raiseLayer(layer, delta);
                                }
                            });
                    }
                    $(layerDiv).append(layerMoveSpan);
                }
                function addLayerInfoSpan(map, layer) {
                    if (!layer.metadata.showInformation) {
                        return;
                    }
                    if (!(layer.attribution) &&
                        !(layer.params && layer.params.LAYERS && layer.params.STYLES)) {
                        return;
                    }
                    var span = $('<span>')
                        .addClass('icon-white')
                        .addClass('icon-info-sign')
                        .css('float', 'right')
                        .css('cursor', 'pointer')
                        .attr('title', 'Information')
                        .click(function(e) {
                            var dialog = that.layerInfoDialogs[layer.id];
                            $.each(that.layerInfoDialogs, function(id, d) {
                                if (d !== dialog) {
                                    d.dialog('close');
                                }
                            });
                            if (dialog) {
                                dialog.dialog('open');
                            }
                            else {
                                var content = $('<div>').append($('<div>').addClass('layerInfoContent'));
                                if (layer.metadata.description) {
                                    content.append(layer.metadata.description);
                                }
                                if (layer.attribution) {
                                    content.append($('<p>')
                                        .append('For general information about this layer see:<br />')
                                        .append(layer.attribution)
                                    );
                                }
                                if (layer.params && layer.params.LAYERS && layer.params.STYLES) {
                                    content
                                        .append($('<p>').css('font-weight', 'bold').append('Map legend'))
                                        .append($('<img>').attr('src',
                                            '/geoserver/wms' +
                                            '?REQUEST=GetLegendGraphic' +
                                            '&VERSION=1.0.0' +
                                            '&FORMAT=image/png' +
                                            '&WIDTH=15' +
                                            '&HEIGHT=15' +
                                            '&LEGEND_OPTIONS=forceLabels:on' +
                                            '&LAYER=' + layer.params.LAYERS +
                                            '&STYLE=' + layer.params.STYLES
                                        ));
                                }
                                that.layerInfoDialogs[layer.id] = content.dialog({
                                    title: layer.name,
                                    modal: false,
                                    resizable: true,
                                    dialogClass: 'layerInfoDialog',
                                    width: 420,
                                    maxWidth: map.getSize().w - (8 + 28 + 8) - (285 + 8),
                                    maxHeight: map.getSize().h - 88,
                                    position: {my: 'left top', at: 'left+44 top+8', of: map.div},
                                    create: function(event, ui) {
                                        $(event.target).closest('.ui-dialog').find('.ui-dialog-titlebar-close').text('×');
                                    }
                                });
                            }
                        });
                    $(layerDiv).append(span);
                }
                if (!layer.isBaseLayer) {
                    addLayerOpacitySpan(this.map, layer);
                    addLayerMoveSpan(this.map, layer, 1);
                    addLayerMoveSpan(this.map, layer, -1);
                }
                addLayerInfoSpan(this.map, layer);

                var inputElem = document.createElement("input");
                var checked = (layer.isBaseLayer) ? (layer == this.map.baseLayer) : layer.getVisibility();
                inputElem.id = this.id + "_input_" + layer.id;
                inputElem.name = (layer.isBaseLayer) ? this.id + "_baseLayers" : layer.id;
                inputElem.type = (layer.isBaseLayer) ? "radio" : "checkbox";
                inputElem.value = layer.id;
                inputElem.checked = checked;
                inputElem.defaultChecked = checked;
                inputElem.className = "olButton";
                inputElem._layer = layer.id;
                inputElem._layerSwitcher = this.id;

                if (!layer.isBaseLayer && !layer.inRange) {
                    inputElem.disabled = true;
                }

                var labelElem = document.createElement("label");
                labelElem["for"] = inputElem.id;
                OpenLayers.Element.addClass(labelElem, "labelElem olButton");
                labelElem._layer = layer.id;
                labelElem._layerSwitcher = this.id;
                if (!layer.isBaseLayer && !layer.inRange) {
                    labelElem.style.color = "gray";
                }
                labelElem.innerHTML = layer.name;
                labelElem.style.verticalAlign = (layer.isBaseLayer) ? "bottom" : "baseline";

                layerDiv.appendChild(inputElem);
                layerDiv.appendChild(labelElem);

                var category = this.categories[layer.metadata.category];
                category.layers.push(layer);
                category.layersDiv.appendChild(layerDiv);
            }

            for (categoryId in this.categories) {
                var category = this.categories[categoryId];
                category.labelDiv.style.display = category.layers.length ? "" : "none";
            }

            return this.div;
        },

        maximizeControl: function(e) {
            this.div.style.width = "";
            this.div.style.height = "";
            this.showControls(false);
            if (e != null) {
                OpenLayers.Event.stop(e);
            }
        },

        minimizeControl: function(e) {
            this.div.style.width = "0px";
            this.div.style.height = "0px";
            this.showControls(true);
            if (e != null) {
                OpenLayers.Event.stop(e);
            }
        },

        showControls: function(minimize) {
            this.maximizeDiv.style.display = minimize ? "" : "none";
            this.minimizeDiv.style.display = minimize ? "none" : "";
            this.layersDiv.style.display = minimize ? "none" : "";
        },

        loadContents: function() {
            this.layersDiv = $('<div id="' + this.id + '_layersDiv" class="layersDiv">')[0];
            var layersHeading = $('<div class="layersHeading">Show/hide map layers</div>')[0];
            this.layersDiv.appendChild(layersHeading);
            
            for (categoryId in this.categories) {
                var category = this.categories[categoryId];
                category.labelDiv = $('<div class="categoryLabelDiv">')
                    .append($('<span>')
                        .addClass('icon-chevron-up')
                        .addClass('icon-white')
                        .css('float', 'right')
                        .css('cursor', 'pointer')
                        .click(function(e) {
                            $(this)
                                .toggleClass('icon-chevron-up')
                                .toggleClass('icon-chevron-down')
                                .closest('.categoryLabelDiv').next().slideToggle();
                        })
                    )
                    .append(category.label)
                    .get(0);
                category.layersDiv = $('<div class="categoryLayersDiv">')[0];
                this.layersDiv.appendChild(category.labelDiv);
                this.layersDiv.appendChild(category.layersDiv);
            }
            this.div.appendChild(this.layersDiv);

            this.maximizeDiv = $('<div class="maximizeDiv olButton" style="position:absolute; display: none;" title="Maximise layer switcher">')[0];
            this.maximizeDiv.appendChild($('<i class="icon-plus icon-white">')[0]);
            this.div.appendChild(this.maximizeDiv);

            this.minimizeDiv = $('<div class="minimizeDiv olButton" style="position:absolute; display: none;" title="Minimise layer switcher">')[0];
            this.minimizeDiv.appendChild($('<i class="icon-minus icon-white">')[0]);
            this.div.appendChild(this.minimizeDiv);
        },

        CLASS_NAME: "OzTrack.OpenLayers.Control.OzTrackLayerSwitcher"
    });

    OzTrack.OpenLayers.Control.WMSGetFeatureInfo = OpenLayers.Class(OpenLayers.Control, {
         maxFeatures: 10,
         layerDetails: null,
         queryVisible: false,
         url: null,
         layerUrls: null,
         infoFormat: null,
         vendorParams: {},
         format: null,
         handler: null,
         formerViewPortDivTitle: null,
         crosses180: false,

         /**
          * APIProperty: events
          * {<OpenLayers.Events>} Events instance for listeners and triggering
          *     control specific events.
          *
          * Register a listener for a particular event with the following syntax:
          * (code)
          * control.events.register(type, obj, listener);
          * (end)
          *
          * Supported event types (in addition to those from <OpenLayers.Control.events>):
          * beforegetfeatureinfo - Triggered before the request is sent.
          *      The event object has an *xy* property with the position of the 
          *      mouse click or hover event that triggers the request.
          * nogetfeatureinfo - no queryable layers were found.
          * getfeatureinfo - Triggered when a GetFeatureInfo response is received.
          *      The event object has a *text* property with the body of the
          *      response (String), a *features* property with an array of the
          *      parsed features, an *xy* property with the position of the mouse
          *      click or hover event that triggered the request, and a *request*
          *      property with the request itself.
          */
         initialize: function(options) {
             options = options || {};
             options.displayClass = options.displayClass || "OzTrackOpenLayersControlWMSGetFeatureInfo";
             OpenLayers.Control.prototype.initialize.apply(this, [options]);
             
             this.format = new OpenLayers.Format.WMSGetFeatureInfo();
             
             var callbacks = {click: this.getInfoForClick};
             this.handler = new OpenLayers.Handler.Click(this, callbacks, {});
             
             this.events.register('activate', this, function(evt) {
                 OpenLayers.Element.addClass(this.map.viewPortDiv, "OzTrackOpenLayersControlWMSGetFeatureInfo");
                 this.formerViewPortDivTitle = $(this.map.viewPortDiv).attr('title');
                 $(this.map.viewPortDiv).attr('title', 'Click for layer information');
             });
             this.events.register('deactivate', this, function(evt) {
                 if (this.formerViewPortDivTitle) {
                     $(this.map.viewPortDiv).attr('title', this.formerViewPortDivTitle);
                 }
                 else {
                     $(this.map.viewPortDiv).removeAttr('title');
                 }
                 OpenLayers.Element.removeClass(this.map.viewPortDiv, "OzTrackOpenLayersControlWMSGetFeatureInfo");
             });
         },

         getInfoForClick: function(evt) {
             // Ignore click event unless on a layer: prevent events "bubbling up"
             // when user clicks on attribution links, layer reordering arrows, etc.
             if ($(evt.target).closest('.olLayerDiv').length == 0) {
                 return;
             }
             this.events.triggerEvent("beforegetfeatureinfo", {xy: evt.xy});
             OpenLayers.Element.addClass(this.map.viewPortDiv, "olCursorWait");
             if (this.layerDetails.length == 0) {
                 this.events.triggerEvent("nogetfeatureinfo");
                 OpenLayers.Element.removeClass(this.map.viewPortDiv, "olCursorWait");
                 return;
             }

             var acc = {
                 numPendingRequests: 1 + (this.crosses180 ? 1 : 0),
                 features: []
             };

             var unshiftedExtent = this.map.getExtent();
             var unshiftedWmsOptions = this.buildWMSOptions(evt.xy, unshiftedExtent, acc);
             var unshiftedRequest = OpenLayers.Request.GET(unshiftedWmsOptions);

             if (this.crosses180) {
                 var shiftedExtent = unshiftedExtent.clone();
                 shiftedExtent.left -= 20037508.34;
                 shiftedExtent.right -= 20037508.34;
                 var shiftedWmsOptions = this.buildWMSOptions(evt.xy, shiftedExtent, acc);
                 var shiftedRequest = OpenLayers.Request.GET(shiftedWmsOptions);
             }
         },

         buildWMSOptions: function(clickPosition, extent, acc) {
             var layerNames = [], styleNames = [], propertyNameParams = [], cqlFilterParams = [];
             for (var i = 0, len = this.layerDetails.length; i < len; i++) {
                 if (
                     (this.layerDetails[i].layer.params.LAYERS != null) &&
                     (!this.queryVisible || this.layerDetails[i].layer.getVisibility())
                 ) {
                     layerNames = layerNames.concat(this.layerDetails[i].layer.params.LAYERS);
                     styleNames = styleNames.concat(this.getStyleNames(this.layerDetails[i].layer));
                     propertyNameParams.push(this.layerDetails[i].propertyNames.join(','));
                     cqlFilterParams.push(this.layerDetails[i].layer.params.CQL_FILTER || 'include');
                 }
             }
             var firstLayer = this.layerDetails[0].layer;
             // use the firstLayer's projection if it matches the map projection -
             // this assumes that all layers will be available in this projection
             var projection = this.map.getProjection();
             var layerProj = firstLayer.projection;
             if (layerProj && layerProj.equals(this.map.getProjectionObject())) {
                 projection = layerProj.getCode();
             }
             var params = OpenLayers.Util.extend({
                 service: "WMS",
                 version: firstLayer.params.VERSION,
                 request: "GetFeatureInfo",
                 exceptions: firstLayer.params.EXCEPTIONS,
                 bbox: extent.toBBOX(null, firstLayer.reverseAxisOrder()),
                 feature_count: this.maxFeatures,
                 height: this.map.getSize().h,
                 width: this.map.getSize().w,
                 format: firstLayer.params.FORMAT,
                 info_format: firstLayer.params.INFO_FORMAT || this.infoFormat
             }, (parseFloat(firstLayer.params.VERSION) >= 1.3) ?
                 {
                     crs: projection,
                     i: parseInt(clickPosition.x),
                     j: parseInt(clickPosition.y)
                 } :
                 {
                     srs: projection,
                     x: parseInt(clickPosition.x),
                     y: parseInt(clickPosition.y)
                 }
             );
             if (layerNames.length != 0) {
                 params = OpenLayers.Util.extend({
                     layers: layerNames,
                     query_layers: layerNames,
                     styles: styleNames,
                     propertyName: $.map(propertyNameParams, function(p) {return '(' + p + ')'}).join(''),
                     cql_filter: cqlFilterParams.join(';')
                 }, params);
             }
             OpenLayers.Util.applyDefaults(params, this.vendorParams);
             return {
                 url: this.url,
                 params: OpenLayers.Util.upperCaseObject(params),
                 callback: function(request) {
                     this.handleResponse(clickPosition, request, this.url, acc);
                 },
                 scope: this
             };
         },

         getStyleNames: function(layer) {
             // in the event of a WMS layer bundling multiple layers but not
             // specifying styles,we need the same number of commas to specify
             // the default style for each of the layers.  We can't just leave it
             // blank as we may be including other layers that do specify styles.
             var styleNames;
             if (layer.params.STYLES) {
                 styleNames = layer.params.STYLES;
             } else if (OpenLayers.Util.isArray(layer.params.LAYERS)) {
                 styleNames = new Array(layer.params.LAYERS.length);
             } else { // Assume it's a String
                 styleNames = layer.params.LAYERS.replace(/[^,]/g, "");
             }
             return styleNames;
         },
         
         handleResponse: function(xy, request, url, acc) {
             var doc = request.responseXML;
             if (!doc || !doc.documentElement) {
                 doc = request.responseText;
             }
             var features = this.format.read(doc);
             acc.features = acc.features.concat(features);
             acc.numPendingRequests--;
             if (acc.numPendingRequests == 0) {
                 this.events.triggerEvent("getfeatureinfo", {
                     features: acc.features,
                     xy: xy
                 });
             }
             OpenLayers.Element.removeClass(this.map.viewPortDiv, "olCursorWait");
         },

         CLASS_NAME: "OzTrack.OpenLayers.Control.WMSGetFeatureInfo"
     });
}(window.OzTrack = window.OzTrack || {}));
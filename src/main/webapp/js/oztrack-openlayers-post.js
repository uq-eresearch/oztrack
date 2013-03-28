/*global OpenLayers*/
// Configure OpenLayers image path; needed because we use wro4j.
// http://dev.openlayers.org/apidocs/files/OpenLayers-js.html#OpenLayers.ImgPath
OpenLayers.ImgPath = "/js/openlayers/img/";

(function(OzTrack) {
    OzTrack.OpenLayers = OzTrack.OpenLayers || {};
    OzTrack.OpenLayers.Control = OzTrack.OpenLayers.Control || {};

    OzTrack.OpenLayers.Control.ZoomToExtent = OpenLayers.Class(OpenLayers.Control.Button, {
        initialize: function(options) {
            this.extent = options.extent;
            this.title = "Zoom to extent";
            options.displayClass = "OzTrackOpenLayersControlZoomToExtent";
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
            options.displayClass = "OzTrackOpenLayersControlOzTrackDataLicence";
            OpenLayers.Control.prototype.initialize.apply(this, [options]);
        },
        draw: function (px) {
            if (this.div == null) {
                this.div =
                    $('<div>')
                        .addClass(this.displayClass)
                        .append($('<a>')
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
            options.displayClass = "OzTrackOpenLayersControlOzTrackLayerSwitcher";
            OpenLayers.Control.prototype.initialize.apply(this, [options]);
        },

        clearLayersArray: function(layersType) {
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
            if(!this.outsideViewport) {
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

        redraw: function() {
            this.clearLayersArray();

            var layers = this.map.layers.slice();
            var len = layers.length;
            for (var i = 0; i < len; i++) {
                var layer = layers[i];
                if (!layer.displayInLayerSwitcher) {
                    continue;
                }

                var layerDiv = $('<div class="layerDiv">').get(0);

                function addLayerMoveSpan(map, layer, delta) {
                    var layerMoveSpan = $('<span>').addClass('icon-white').css('float', 'right');
                    layerMoveSpan.addClass((delta > 0) ? 'icon-arrow-down' : 'icon-arrow-up');
                    var targetLayer = map.layers[map.getLayerIndex(layer) + delta];
                    if (targetLayer && (layer.metadata.category == targetLayer.metadata.category)) {
                        layerMoveSpan
                            .css('cursor', 'pointer')
                            .click(function(e) {
                                var targetLayer = map.layers[map.getLayerIndex(layer) + delta];
                                if (targetLayer && (layer.metadata.category == targetLayer.metadata.category)) {
                                    map.raiseLayer(layer, delta);
                                }
                            });
                    }
                    $(layerDiv).append(layerMoveSpan);
                }
                if (!layer.isBaseLayer) {
                    addLayerMoveSpan(this.map, layer, 1);
                    addLayerMoveSpan(this.map, layer, -1);
                }

                var inputElem = document.createElement("input");
                var checked = (layer.isBaseLayer) ? (layer == this.map.baseLayer) : layer.getVisibility();
                inputElem.id = this.id + "_input_" + layer.name;
                inputElem.name = (layer.isBaseLayer) ? this.id + "_baseLayers" : layer.name;
                inputElem.type = (layer.isBaseLayer) ? "radio" : "checkbox";
                inputElem.value = layer.name;
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
}(window.OzTrack = window.OzTrack || {}));
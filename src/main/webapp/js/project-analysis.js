var projection900913 = new OpenLayers.Projection("EPSG:900913");
var projection4326 =  new OpenLayers.Projection("EPSG:4326");
var colours = [
    '#8DD3C7',
    '#FFFFB3',
    '#BEBADA',
    '#FB8072',
    '#80B1D3',
    '#FDB462',
    '#B3DE69',
    '#FCCDE5',
    '#D9D9D9',
    '#BC80BD',
    '#CCEBC5',
    '#FFED6F'
];

var map;
var allDetectionsLayer;
var polygonStyleMap;
var lineStyleMap;
var pointStyleMap;

function animalInfoToggle () {
    $('.animalInfoToggle').each(function() {
        $(this).parent().next().hide();
        $(this).click(function() {
            $(this).parent().next().slideToggle('fast');
            return false;
        });
    });
}

function initializeProjectMap(projectId) {
    animalInfoToggle();
    
    map = new OpenLayers.Map('projectMap', {
        units: 'm',
        projection: projection900913,
        displayProjection: projection4326
    });
    map.addControl(new OpenLayers.Control.MousePosition());
    map.addControl(new OpenLayers.Control.ScaleLine());
    map.addControl(new OpenLayers.Control.NavToolbar());
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.LoadingPanel());
    
    var gphy = new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN});
    var gsat = new OpenLayers.Layer.Google("Google Satellite", {type: google.maps.MapTypeId.SATELLITE});
    
    map.addControl(createControlPanel());
    
    lineStyleMap = createLineStyleMap();
    pointStyleMap = createPointStyleMap();
    polygonStyleMap = createPolygonStyleMap();
   
    allDetectionsLayer = createAllDetectionsLayer(projectId);
        
    map.addLayers([gsat, gphy, allDetectionsLayer]);
    map.setCenter(new OpenLayers.LonLat(133,-28).transform(projection4326, projection900913), 4);
    
    reportProjectionDescr();
}

function createControlPanel() {
    var panel = new OpenLayers.Control.Panel();
    panel.addControls([
        new OpenLayers.Control.Button({
            title: 'Zoom to Data Extent',
            displayClass: "zoomButton",
            trigger: function() {
                map.zoomToExtent(allDetectionsLayer.getDataExtent(),false);
            }
        })
    ]);
    return panel;
}

function createAllDetectionsLayer(projectId) {
    return new OpenLayers.Layer.Vector(
        "All Detections",
        {
            projection: projection4326,
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                url:  "/mapQueryWFS?projectId=" + projectId + "&queryType=ALL_POINTS",
                featureType: "Track",
                featureNS: "http://localhost:8080/"
            }),
            strategies: [new OpenLayers.Strategy.Fixed()],
            styleMap: pointStyleMap, 
            eventListeners: {
                loadend: function (e) {
                    map.zoomToExtent(e.object.getDataExtent(),false);
                    updateAnimalInfo(e.object);
                    createTrajectoryLayer(e.object);
                    createStartEndPointsLayer(e.object);
                }
            }
        }
    );
}

function createTrajectoryLayer(detectionsLayer) {
    var trajectoryLayer = new OpenLayers.Layer.Vector(
        "Trajectory",
        {
            projection: projection4326,
            styleMap: lineStyleMap    
        }
    );
    for (var key in detectionsLayer.features) {
        var feature = detectionsLayer.features[key];
        var trajectoryFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(feature.geometry.components));
        trajectoryFeature.attributes = {
            animalId : feature.attributes.animalId,
            animalName : feature.attributes.animalName, 
            fromDate: feature.attributes.fromDate,
            toDate: feature.attributes.toDate
        };
        trajectoryLayer.addFeatures([trajectoryFeature]);
    }  
    map.addLayer(trajectoryLayer);
    updateAnimalInfo(trajectoryLayer);
}

function createStartEndPointsLayer(allDetectionsLayer) {
    var startEndPointsLayer = new OpenLayers.Layer.Vector(
        "Start and End Points",
        {
            projection: projection4326,
            styleMap: createStartEndPointsStyleMap()    
        }
    );
    for (var key in allDetectionsLayer.features) {
        var feature = allDetectionsLayer.features[key];
        if (feature.attributes) {
            var startPoint = new OpenLayers.Feature.Vector(feature.geometry.components[0]);
            startPoint.attributes = {
                animalId : feature.attributes.animalId,
                animalName : feature.attributes.animalName, 
                fromDate: feature.attributes.fromDate,
                pointName: "start"
            };
            var endPoint = new OpenLayers.Feature.Vector(feature.geometry.components[feature.geometry.components.length - 1]);
            endPoint.attributes = {
                animalId : feature.attributes.animalId,
                animalName : feature.attributes.animalName, 
                toDate: feature.attributes.toDate,
                pointName: "end"
            };
            startEndPointsLayer.addFeatures([startPoint,endPoint]);
        }
    }
    map.addLayer(startEndPointsLayer);
}

function createLineStyleMap() {
    var styleContext = {
        getColour: function(feature) {
            return colours[feature.attributes.animalId % colours.length];
        }
    };
    var lineOnStyle = new OpenLayers.Style(
        {
            strokeColor: "${getColour}",
            strokeWidth: 1,
            strokeOpacity: 1.0
        },
        {
            context: styleContext
        }
    );
    var lineOffStyle = {
        strokeOpacity: 0.0,
        fillOpacity: 0.0
    };
    var lineStyleMap = new OpenLayers.StyleMap({
        "default": lineOnStyle,
        "temporary": lineOffStyle
    });
    return lineStyleMap;
}

function createPointStyleMap() {
    var styleContext = {
        getColour: function(feature) {
            return colours[feature.attributes.animalId % colours.length];
        }
    };
    var pointOnStyle = new OpenLayers.Style(
        {
            fillColor: "${getColour}",
            strokeColor:"${getColour}",    
            //fillOpacity: 0.5,
            strokeOpacity: 0.8,
            strokeWidth: 0.2,
            graphicName: "cross",
            pointRadius:4
        },
        {
            context: styleContext
        }
    );
    var pointOffStyle = {
        strokeOpacity: 0.0,
        fillOpacity: 0.0
    };
    var pointStyleMap = new OpenLayers.StyleMap({
        "default": pointOnStyle,
        "temporary": pointOffStyle
    });
    return pointStyleMap;
}

function createPolygonStyleMap() {
    var styleContext = {
        getColour: function(feature) {
            return colours[feature.attributes.id.value % colours.length];
        }
    };
    var polygonOnStyle = new OpenLayers.Style(
        {
            strokeColor: "${getColour}",
            strokeWidth: 2,
            strokeOpacity: 1.0,
            fillColor: "${getColour}",
            fillOpacity: 0.5         
        },
        {
            context: styleContext
        }
    );
    var polygonOffStyle = {
        strokeOpacity: 0.0,
        fillOpacity: 0.0
    };
    var polygonStyleMap = new OpenLayers.StyleMap({
        "default" : polygonOnStyle,
        "temporary" : polygonOffStyle
    });
    return polygonStyleMap;
}

function createStartEndPointsStyleMap() {
    var styleContext = {
        getPointColour: function(feature) {
            return (feature.attributes.pointName == "start") ? "#00CD00" : "#CD0000";
        }
    };
    var startEndPointsOnStyle = new OpenLayers.Style(
        {
            pointRadius: 2,
            fillColor: "${getPointColour}",
            strokeColor:"${getPointColour}",    
            fillOpacity: 0,
            strokeOpacity: 1,
            strokeWidth: 1.2
        },
        {
            context:styleContext
        }
    );
    var startEndPointsOffStyle = {
        strokeOpacity: 0,
        fillOpacity: 0
    };
    var startEndPointsStyleMap = new OpenLayers.StyleMap({
        "default" : startEndPointsOnStyle,
        "temporary" : startEndPointsOffStyle
    });
    return startEndPointsStyleMap;
}

function reportProjectionDescr() {
    var projectionCode = $('input[id=projectionCode]').val();
    $('#projectionDescr').html("Searching for " + projectionCode + "...");
    new Proj4js.Proj(projectionCode, projectionCallback);
}

function projectionCallback(projection) {
    var detailText = "";
    var strArray = projection.defData.split("+");
    var title;
    
    for (var s in strArray) {
        if (strArray[s].indexOf("title") != -1) {
            detailText = "Title: " + strArray[s].split("=")[1];
        }
    }
    
    if (detailText == "") {
        if (projection.ellipseName != null) {
            detailText = "Ellipse Name: " + projection.ellipseName;
        }
        else {
            if (projection.defData != null) {
                detailText = "Details: ";
                for (var i=1; i <strArray.length; i++) {
                    detailText = detailText + strArray[i].replace(/^\s+|\s+$/g,"") + ", ";
                }
            }
            else {
                detailText = "Projection exists.";
            }
        }
    }
    
    // TODO: get the units, must be in m for the calculations to work!
    var headerText = "<b>" + projection.srsCodeInput + "</b><br>";
    $('#projectionDescr').html(headerText + detailText);
}

Proj4js.reportError = function(msg) {
    $('#projectionDescr').html(msg + "Manually search for a code at <a href='http://spatialreference.org'>spatialreference.org</a>");
};

function updateAnimalInfo(wfsLayer) {
    var layerName = wfsLayer.name;
    var layerId = wfsLayer.id;
    var featureId = "";
    
    for (var key in wfsLayer.features) {
        var feature = wfsLayer.features[key];
        if (feature.attributes && feature.attributes.animalId) {
            feature.renderIntent = "default";
            
            // set the colour and make sure the show/hide all box is checked
            var colour = colours[feature.attributes.animalId % colours.length];
            $('#legend-colour-' + feature.attributes.animalId).attr('style', 'background-color: ' + colour + ';');
            $('input[id=select-animal-' + feature.attributes.animalId + ']').attr('checked','checked');
            
            // add detail for this layer 
            
            var checkboxValue = layerId + "-" + feature.id;
            var checkboxId = checkboxValue.replace(/\./g,"");
            
            var checkboxHtml =
                "<input type='checkbox' " 
                + "id='select-feature-" + checkboxId + "' value='" + checkboxValue + "' checked='true'/></input>";
            
            var layerNameHtml = "<b>&nbsp;&nbsp;" + layerName + "</b>";
            var tableRowsHtml = "";
            
            if (layerName.indexOf("All Detections") == -1) {
                tableRowsHtml =
                    "<table><tr><td class='label'>Date From:</td><td>" + feature.attributes.fromDate + "</td></tr>"
                    + "<tr><td class='label'>Date To:</td><td>" + feature.attributes.toDate + "</td></tr>";
            }
            
            var distanceRowHtml = "";
            if (feature.geometry.CLASS_NAME == "OpenLayers.Geometry.LineString") {
                var distance = feature.geometry.getGeodesicLength(map.projection)/1000;
                distanceRowHtml = "<tr><td class='label'>Min Distance: </td><td>" + Math.round(distance*1000)/1000 + "km </td></tr>";
            }
            
            var html = layerNameHtml + tableRowsHtml + distanceRowHtml + "</table>";
            
            $('#animalInfo-'+ feature.attributes.animalId).append('<div class="layerInfo">' + checkboxHtml + html + '</div>');
            $('input[id=select-feature-' + checkboxId + ']').change(function() {
                toggleFeature(this.value,this.checked);
            });
        }
    }
}

function zoomToTrack(animalId) {
    for (var key in allDetectionsLayer.features) {
         var feature = allDetectionsLayer.features[key];
         if (feature.attributes && animalId == feature.attributes.animalId) {
               map.zoomToExtent(feature.geometry.getBounds(),false);
         }
    }
}

function getVectorLayers() {
    var vectorLayers = new Array();
    for (var c in map.controls) {
        var control = map.controls[c];
        if (control.id.indexOf("LayerSwitcher") != -1) {
            for (var i=0; i < control.dataLayers.length; i++) {
                vectorLayers.push(control.dataLayers[i].layer);
            }
        }
    }
    return vectorLayers;
}

function toggleFeature(featureIdentifier, setVisible) {
    var splitString = featureIdentifier.split("-");
    var layerId = splitString[0];
    var featureId = splitString[1];
    
    var layer = map.getLayer(layerId);
    for (var key in layer.features) {
        var feature = layer.features[key];
        if (feature.id == featureId) {
            if (setVisible) {
                feature.renderIntent = "default";
            }
            else {
                feature.renderIntent = "temporary";
            }
            layer.drawFeature(feature);
        }
    }
}

function toggleAllAnimalFeatures(animalId, setVisible) {
    var vectorLayers = getVectorLayers();
    for (var l in vectorLayers) {
        var layer = vectorLayers[l];
        var layerName = layer.name;
        for (var f in layer.features) {
            var feature = layer.features[f];
            var featureAnimalId;
            if (feature.geometry.CLASS_NAME == "OpenLayers.Geometry.LineString" ||
                feature.geometry.CLASS_NAME == "OpenLayers.Geometry.MultiPoint" ||
                feature.geometry.CLASS_NAME == "OpenLayers.Geometry.Point" ) {
                featureAnimalId = feature.attributes.animalId;
            }
            else {
                featureAnimalId = feature.attributes.id.value;
            }
            if (featureAnimalId == animalId) {
                var featureIdentifier = layer.id + "-" + feature.id;
                toggleFeature(featureIdentifier, setVisible);
            }
        }
     }
    $("#animalInfo-"+animalId).find(':checkbox').attr("checked",setVisible);
}

function addProjectMapLayer() {
    var queryType = $('input[name=mapQueryTypeSelect]:checked');
    var queryTypeValue = queryType.val();
    var queryTypeLabel = $('label[for="' + queryType.attr('id') + '"]').text();

    if (queryTypeValue != null) {
        var layerName = queryTypeLabel;
        var params = {
            queryType: queryTypeValue,
            projectId: $('#projectId').val(),
            srs: $('input[id=projectionCode]').val(),
            percent: $('input[id=percent]').val(),
            h: $('input[id=h]').val(),
            alpha: $('input[id=alpha]').val()
        };
        var dateFrom = $('input[id=fromDatepicker]').val();
        var dateTo = $('input[id=toDatepicker]').val();
        if (dateFrom.length == 10) {
            layerName = layerName + " from " + dateFrom;
            params.dateFrom = dateFrom;
        }
        if (dateTo.length == 10) {
            layerName = layerName + " to " + dateTo;
            params.dateTo = dateTo;
        }
    
        if (queryTypeValue == "LINES") {
            addWFSLayer(layerName, params, lineStyleMap);
        }
        else if (queryTypeValue == "POINTS") {
            addWFSLayer(layerName, params, pointStyleMap);
        }
        else {
            addKMLLayer(layerName, params);
        }
    }
    else {
        alert("Please set a Layer Type.");
    }
}

function addKMLLayer(layerName, params) {
    var url = "/mapQueryKML";
    var queryOverlay = new OpenLayers.Layer.Vector(
        layerName,
        {
            strategies: [new OpenLayers.Strategy.Fixed()],
            eventListeners: {
                loadend: function (e) {updateAnimalInfoFromKML(layerName, url, params, e);}
            },
            protocol: new OpenLayers.Protocol.HTTP({
                url: url,
                params: params,
                format: new OpenLayers.Format.KML({
                    extractAttributes: true,
                    maxDepth: 2,
                    internalProjection: projection900913,
                    externalProjection: projection4326,
                    kmlns:"http://localhost:8080/"
                })
            }),
            styleMap: polygonStyleMap        
        }
    );
    map.addLayer(queryOverlay);
}
                
function updateAnimalInfoFromKML(layerName, url, params, e) {
    for (var f in e.object.features) {
        var feature = e.object.features[f];
        var area = feature.attributes.area.value;
        
        feature.renderIntent = "default";
        feature.layer.drawFeature(feature);

        var checkboxValue = feature.layer.id + "-" + feature.id;
        var checkboxId = checkboxValue.replace(/\./g,"");
        var checkboxHtml = "<input type='checkbox' " 
             + "id='select-feature-" + checkboxId + "' value='" + checkboxValue + "' checked='true'/></input>";
        var html = "&nbsp;&nbsp;<b>" + layerName + "</b>"; 
        html += '<table>';
        if (params.percent) {
            html += '<tr><td class="label">Percent: </td><td>' + params.percent + '%</td></tr>';
        }
        if (params.h) {
            html += '<tr><td class="label">h value: </td><td>' + params.h + '</td></tr>';
        }
        if (params.alpha) {
            html += '<tr><td class="label">alpha: </td><td>' + params.alpha + '</td></tr>';
        }
        html += '<tr><td class="label">SRS: </td><td>' + params.srs + '</td></tr>';
        html += '<tr><td class="label">Area: </td><td>' + Math.round(area*1000)/1000 + ' km<sup>2</sup></td></tr>';
        html += '</table>';
        var urlWithParams = url;
        var paramString = OpenLayers.Util.getParameterString(params);
        if (paramString.length > 0) {
            var separator = (urlWithParams.indexOf('?') > -1) ? '&' : '?';
            urlWithParams += separator + paramString;
        }
        $('#animalInfo-'+ feature.attributes.id.value).append(
            '<a style="float: right; margin-right: 18px;" href="' + urlWithParams + '">KML</a>' +
            '<div class="layerInfo">' + checkboxHtml + html + '</div>'
        );
        $('input[id=select-feature-' + checkboxId + ']').change(function() {
            toggleFeature(this.value,this.checked);
        });
    }
}

function addWFSLayer(layerName, params, styleMap) {
    newWFSOverlay = new OpenLayers.Layer.Vector(
        layerName,
        {
            strategies: [new OpenLayers.Strategy.Fixed()],
            styleMap:styleMap,
            eventListeners: {
                loadend: function (e) {
                    map.zoomToExtent(newWFSOverlay.getDataExtent(),false);
                    updateAnimalInfo(newWFSOverlay);
                }
            },
            projection: projection4326,
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                url:  "/mapQueryWFS",
                params:params,
                featureType: "Track",
                featureNS: "http://localhost:8080/"
            })
        }
    );
    map.addLayer(newWFSOverlay);       
}
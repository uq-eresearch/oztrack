function createHomeMap(div) {
    var projection900913 = new OpenLayers.Projection('EPSG:900913');
    var projection4326 =  new OpenLayers.Projection("EPSG:4326");

    var map = new OpenLayers.Map(div, {
        units: 'm',
        projection: projection900913,
        displayProjection: projection4326
    });
    map.addControl(new OpenLayers.Control.LayerSwitcher());

    var gphy = new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN});
    var gsat = new OpenLayers.Layer.Google("Google Satellite", {type: google.maps.MapTypeId.SATELLITE});
    var projectPointsLayer = createProjectPointsLayer();
    map.addLayers([gsat,gphy, projectPointsLayer]);

    var projectHighlightControl = createProjectHighlightControl(projectPointsLayer);
    map.addControl(projectHighlightControl);
    projectHighlightControl.activate();

    var projectClickControl = createProjectClickControl(map, projectPointsLayer);
    map.addControl(projectClickControl);
    projectClickControl.activate();

    map.setCenter(new OpenLayers.LonLat(133, -28).transform(projection4326, projection900913), 4);

    return map;
}

function createProjectPointsLayer() {
    return new OpenLayers.Layer.Vector(
        "Projects",
        {
            projection: new OpenLayers.Projection("EPSG:4326"),
            styleMap: createProjectPointsStyleMap(),
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                url:  "/mapQueryWFS?queryType=PROJECTS",
                featureNS: "http://oztrack.org/xmlns#",
                featureType: "Project",
                geometryName: "projectCentroid"
            })
        }
    );
}

function createProjectPointsStyleMap() {
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
    var wfsStyleContext = {
        getColour: function(feature) {
            var c = feature.attributes.projectId%colours.length;
            return colours[c];
        }
    };
    var pointsDefaultStyle = new OpenLayers.Style(
        {
            pointRadius: 4.5,
            strokeColor: "#000000",
            strokeWidth: 1.5,
            strokeOpacity: 0.6,
            fillColor: "${getColour}",
            fillOpacity: 0.6
        },
        {
            context: wfsStyleContext
        }
    );
    var pointsSelectStyle = new OpenLayers.Style(
        {
            pointRadius: 4.5,
            strokeColor: "#000000",
            strokeWidth: 1.5,
            strokeOpacity: 0.9,
            fillColor: "${getColour}",
            fillOpacity: 1.0
        },
        {
            context: wfsStyleContext
        }
    );
    var pointsTempStyle = {
        fillOpacity: 0.9,
        cursor: 'pointer'
    };
    var pointsStyleMap = new OpenLayers.StyleMap({
        "default": pointsDefaultStyle,
        "temporary": pointsTempStyle,
        "select": pointsSelectStyle
    });
    return pointsStyleMap;
}

function createProjectHighlightControl(projectPointsLayer) {
    return new OpenLayers.Control.SelectFeature(
        [projectPointsLayer],
        {
            hover: true,
            highlightOnly: true,
            renderIntent: "temporary"
        }
    );
}

function createProjectClickControl(map, projectPointsLayer) {
    return new OpenLayers.Control.SelectFeature(
        [projectPointsLayer],
        {
            clickout: true,
            eventListeners: {
                featurehighlighted: function(e) {
                    map.addPopup(buildPopup(e.feature));
                }
            }
        }
    );
}

function buildPopup(f) {
    var popupHtml =
        '<div class="home-popup">' +
        '    <h3>' + f.attributes.projectTitle + '</h3>' +
        '    <div class="home-popup-attr-name">Species:</div>' +
        '    <div class="home-popup-attr-value">' + f.attributes.speciesCommonName + '</div>' +
        '    <div class="home-popup-attr-name">Coverage:</div>' +
        '    <div class="home-popup-attr-value">' + f.attributes.spatialCoverageDescr + '</div>' +
        '    <div class="home-popup-attr-name">Date range:</div>' +
        '    <div class="home-popup-attr-value">' + f.attributes.firstDetectionDate + ' to ' + f.attributes.lastDetectionDate + '</div>' +
        '    <div class="home-popup-attr-name">Data access:</div>' +
        '    <div class="home-popup-attr-value">' +
        (
            (f.attributes.global == 'true')
            ? '<span style="font-weight: bold; color: green;">Open Access</span>'
            : '<span style="font-weight: bold; color: red;">Restricted Access</span>'
        ) +
        '    </div>' +
        '    <div style="margin-top: 1em;"><a href="projects/' + f.attributes.projectId + '">Open project</a></div>' +
        '</div>';
    var popup = new OpenLayers.Popup.AnchoredBubble(
        f.attributes.projectId,
        f.geometry.getBounds().getCenterLonLat(),
        null,
        popupHtml,
        null,
        true
    );
    popup.autoSize = true;
    popup.setBackgroundColor("#FBFEE9");
    popup.setOpacity("0.95");
    popup.closeOnMove = true;
    return popup;
}
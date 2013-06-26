/*global OpenLayers, google*/
function createHomeMap(div) {
    var projection900913 = new OpenLayers.Projection('EPSG:900913');
    var projection4326 =  new OpenLayers.Projection('EPSG:4326');

    var map = new OpenLayers.Map(div, {
        theme: null,
        units: 'm',
        projection: projection900913,
        displayProjection: projection4326
    });
    map.addControl(new OpenLayers.Control.LoadingPanel());

    var gphy = new OpenLayers.Layer.Google('Google Physical', {type: google.maps.MapTypeId.TERRAIN});
    var gsat = new OpenLayers.Layer.Google('Google Satellite', {type: google.maps.MapTypeId.SATELLITE});
    var projectPointsLayer = createProjectPointsLayer();
    map.addLayers([gsat,gphy, projectPointsLayer]);

    var projectHighlightControl = createProjectHighlightControl(projectPointsLayer);
    map.addControl(projectHighlightControl);
    projectHighlightControl.activate();

    var projectClickControl = createProjectClickControl(map, projectPointsLayer);
    map.addControl(projectClickControl);
    projectClickControl.activate();

    map.setCenter(new OpenLayers.LonLat(133, -28).transform(projection4326, projection900913), 3);

    return map;
}

function createProjectPointsLayer() {
    return new OpenLayers.Layer.Vector(
        'Projects',
        {
            projection: new OpenLayers.Projection('EPSG:4326'),
            styleMap: createProjectPointsStyleMap(),
            strategies: [
                new OpenLayers.Strategy.Fixed(),
                new OpenLayers.Strategy.Cluster()
            ],
            protocol: new OpenLayers.Protocol.WFS.v1_1_0({
                url:  '/projectsWFS',
                featureNS: 'http://oztrack.org/xmlns#',
                featureType: 'Project',
                geometryName: 'projectCentroid'
            })
        }
    );
}

function createProjectPointsStyleMap() {
    var wfsStyleContext = {
        pointRadius: function(feature) {
            return Math.min(2 * feature.attributes.count, 10) + 4;
        },
        strokeWidth: function(feature) {
            return Math.min(0.25 * (feature.attributes.count - 1), 3) + 1.5;
        },
        label: function(feature) {
            return (feature.attributes.count > 1) ? feature.attributes.count : '';
        }
    };
    var pointsDefaultStyle = new OpenLayers.Style(
        {
            pointRadius: '${pointRadius}',
            strokeColor: '#D6FF99',
            strokeWidth: '${strokeWidth}',
            strokeOpacity: 0.6,
            fillColor: '#B3DE69',
            fillOpacity: 0.6,
            fontColor: '#DBFF9F',
            fontSize: '10px',
            fontFamily: 'sans-serif',
            label: '${label}',
            labelXOffset: 0,
            labelYOffset: 0,
            labelAlign: 'center'
        },
        {
            context: wfsStyleContext
        }
    );
    var pointsSelectStyle = new OpenLayers.Style(
        {
            strokeOpacity: 0.9,
            fillOpacity: 0.9
        },
        {
            context: wfsStyleContext
        }
    );
    var pointsTempStyle = {
        strokeOpacity: 0.9,
        fillOpacity: 0.9,
        cursor: 'pointer'
    };
    var pointsStyleMap = new OpenLayers.StyleMap({
        'default': pointsDefaultStyle,
        'temporary': pointsTempStyle,
        'select': pointsSelectStyle
    });
    return pointsStyleMap;
}

function createProjectHighlightControl(projectPointsLayer) {
    return new OpenLayers.Control.SelectFeature(
        [projectPointsLayer],
        {
            hover: true,
            highlightOnly: true,
            renderIntent: 'temporary'
        }
    );
}

function createProjectClickControl(map, projectPointsLayer) {
    return new OpenLayers.Control.SelectFeature(
        [projectPointsLayer],
        {
            eventListeners: {
                featurehighlighted: function(e) {
                    $('#map-instructions').fadeOut();
                    $.each(map.popups, function(i, p) {map.removePopup(p);});
                    if (e.feature.cluster.length > 1) {
                        map.addPopup(buildClusterPopup(map, e.feature));
                        $.each(e.feature.cluster, function(i, f) {
                            $('*[id="' + f.id + '_popup_link"]')
                                .click(function(e) {
                                    console.log('here');
                                    console.log(e);
                                    e.preventDefault();
                                    $.each(map.popups, function(j, p) {
                                        $(p.div).fadeOut('normal', function() {map.removePopup(p);});
                                    });
                                    map.addPopup(buildProjectPopup(f));
                                })
                        });
                    }
                    else {
                        map.addPopup(buildProjectPopup(e.feature.cluster[0]));
                    }
                }
            }
        }
    );
}

function buildClusterPopup(map, cf) {
    var content = $('<div>').append($('<div class="home-popup">')
        .append($('<div class="home-popup-title">').append(cf.cluster.length + ' projects'))
        .append($('<ul>').append(
            $.map(cf.cluster, function(f, i) {
                var link = $('<a>')
                    .attr('id', f.id + '_popup_link')
                    .attr('href', '#')
                    .append(f.attributes.projectTitle);
                return $('<li>').append(link)[0];
            })
        ))
    );
    var lonlat = cf.geometry.getBounds().getCenterLonLat();
    if ((cf.cluster[0].attributes.crosses180 === 'true') && (lonlat.lon < 0)) {
        lonlat.lon += 20037508.34 * 2;
    }
    var popup = new OpenLayers.Popup.FramedCloud(
        cf.id + '_popup',
        lonlat,
        null,
        content.html(),
        null,
        true
    );
    popup.autoSize = true;
    popup.panMapIfOutOfView = true;
    popup.setBackgroundColor('#FBFEE9');
    return popup;
}

function buildProjectPopup(f) {
    var div = $('<div>');
    var content = $('<div class="home-popup">').appendTo(div);
    content.append($('<div class="home-popup-title">').append(f.attributes.projectTitle));
    var pairs = [];
    pairs.push(['Species',
        ((f.attributes.speciesScientificName) ? ('<i>' + f.attributes.speciesScientificName + '</i>') : '') +
        ((f.attributes.speciesCommonName) ? ('<br />' + f.attributes.speciesCommonName) : '')
    ]);
    pairs.push(['Coverage', f.attributes.spatialCoverageDescr]);
    pairs.push(['Date range', f.attributes.firstDetectionDate + ' to ' + f.attributes.lastDetectionDate]);
    pairs.push(['Data access',
        (f.attributes.access == 'OPEN')
        ? '<span style="font-weight: bold; color: green;">Open Access</span>'
        : (f.attributes.access == 'EMBARGO')
        ? '<span style="font-weight: bold; color: orange;">Delayed Open Access</span>'
        : '<span style="font-weight: bold; color: red;">Closed Access</span>'
    ]);
    $.each(pairs, function(i, p) {
        if (p[0] && p[1]) {
            content.append($('<div class="home-popup-attr-name">').append(p[0] + ':'));
            content.append($('<div class="home-popup-attr-value">').append(p[1]));
        }
    });
    var footer = $('<div class="home-popup-footer">');
    footer.append('<a href="projects/' + f.attributes.projectId + '">View details</a>')
    if (f.attributes.access == 'OPEN') {
        footer.append(' | ');
        footer.append('<a href="projects/' + f.attributes.projectId + '/analysis">View tracks</a>');
    }
    content.append(footer);
    var lonlat = f.geometry.getBounds().getCenterLonLat().clone();
    if ((f.attributes.crosses180 === 'true') && (lonlat.lon < 0)) {
        lonlat.lon += 20037508.34 * 2;
    }
    var popup = new OpenLayers.Popup.FramedCloud(
        f.id + '_popup',
        lonlat,
        null,
        div.html(),
        null,
        true
    );
    popup.autoSize = true;
    popup.panMapIfOutOfView = true;
    popup.setBackgroundColor('#FBFEE9');
    return popup;
}
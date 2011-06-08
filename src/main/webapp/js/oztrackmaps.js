
function initialize() {

        var latlng = new google.maps.LatLng(-28.274398,133.775136);
        var myOptions = {
          zoom: 3,
          center: latlng,
          mapTypeId: google.maps.MapTypeId.SATELLITE
        };

        var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        map.enableKeyDragZoom();

        var infowindow = new google.maps.InfoWindow({
                content: "loading...",
                maxWidth:1
            });

        // image icon
        var imageUrl="http://google-maps-icons.googlecode.com/files/amphitheater-tourism.png";
        var image = new google.maps.MarkerImage(imageUrl,
                                                new google.maps.Size(32,37),
                                                new google.maps.Point(0,0),
                                                new google.maps.Point(0,0),
                                                new google.maps.Size(16,18));


        var projects = [
        ['Test Project 1',-15.3351,144.1757, 1, '<div class="infowindow"><a href="#">Test Project 1</a><br>Kennedy River</div>' ],
        ['Test Project 2',-14.3351,145.1757, 2, '<div class="infowindow"><a href="#">Test Project 2</a><br>Wenlock River</div>']
        ];

        for (var i = 0; i < projects.length; i++) {
            var site = projects[i];
            var siteLatLng =  new google.maps.LatLng(site[1], site[2]);
            var marker = new google.maps.Marker({
               position: siteLatLng,
               map:map,
               title:site[0],
               zIndex:site[3],
               html:site[4],
               icon:image
            });

            google.maps.event.addListener(marker, "click", function () {
                infowindow.setContent(this.html);
                infowindow.open(map, this);
            })

        }
 }

 function initializeSightingMap() {

    var australia = new google.maps.LatLng(-32,134);
    var centralAustralia = new google.maps.LatLng(-24.01, 135.01);
    var myOptions = {
                    zoom: 3,
                    center: australia,
                    mapTypeId: google.maps.MapTypeId.SATELLITE,
                    streetViewControl: false,
                    mapTypeControl: true,
                    mapTypeControlOptions: {
                            style: google.maps.MapTypeControlStyle.DEFAULT
                            },
                    zoomControl:true,
                    zoomControlOptions: {
                            style: google.maps.ZoomControlStyle.SMALL
                            },
                    animation: google.maps.Animation.BOUNCE

                    };

    var sightingMap = new google.maps.Map(document.getElementById("sightingMap"), myOptions);
    sightingMap.enableKeyDragZoom();

    var marker = new google.maps.Marker({
               position: centralAustralia,
               map:sightingMap,
               draggable: true
               //title:site[0],
               //zIndex:site[3],
               //html:site[4],
               //icon:image
    });

    $('#sightingLatitude').val(centralAustralia.lat());
    $('#sightingLongitude').val(centralAustralia.lng());

    google.maps.event.addListener(marker, 'drag', function() {
        var point = marker.getPosition();
 		sightingMap.setCenter(point);
 		$('#sightingLatitude').val(point.lat());
        $('#sightingLongitude').val(point.lng());
    });

    google.maps.event.addListener(sightingMap, 'dragend', function() {
        var point = sightingMap.getCenter();
        marker.setPosition(point);
 		$('#sightingLatitude').val(point.lat());
        $('#sightingLongitude').val(point.lng());
    });

    google.maps.event.addListener(sightingMap, 'zoom_changed', function() {
        var point = sightingMap.getCenter();
        marker.setPosition(point);
 		$('#sightingLatitude').val(point.lat());
        $('#sightingLongitude').val(point.lng());
    });



 }



$(document).ready(function(){
    //
});

$(document.getElementById('map_canvas')).ready(function() {
    initialize();
});

 function initialize() {
        var latlng = new google.maps.LatLng(-28.274398,133.775136);
        var myOptions = {
          zoom: 4,
          center: latlng,
          mapTypeId: google.maps.MapTypeId.SATELLITE
        };
        var map = new google.maps.Map(document.getElementById("map_canvas"),
            myOptions);
 }

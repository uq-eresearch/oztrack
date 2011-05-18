
$(document).ready(function(){

});

$(document.getElementById('map_canvas')).ready(function() {
    initialize();
});

 function initialize() {
        var latlng = new google.maps.LatLng(-28.274398,133.775136);
        var myOptions = {
          zoom: 3,
          center: latlng,
          mapTypeId: google.maps.MapTypeId.SATELLITE
        };
        var map = new google.maps.Map(document.getElementById("map_canvas"),
            myOptions);
 }

function login() {

    var user = $('#username').val();
    var passwd = $('#password').val();
    var errs = {username: '', password:''};
    var user = {username : user, password : passwd, errors: errs};
    $.ajax ({
        type: 'POST',
        url:'login',
        data:user,
        success: function(data, textStatus, jqXHR) {
            $('#loginMessage').html(textStatus);
        },
        error: function(xhr, textStatus, errorThrown) {
            $('#loginError').html('Authentication Failed: incorrect username or password');
        }
    });
    return false;
 }


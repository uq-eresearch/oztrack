
$(document).ready(function(){
     crumbs();
     accordian();
     setupDatepicker();
});

$(document.getElementById('map_canvas')).ready(function() {
    initialize();
});

/*$(document.getElementById('crumbs')).ready(function() {
    //crumbs();
});*/

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

 function crumbs() {

   //eg url http://localhost:8080/oztrack/register;jsessionid=4q9927jnlfrdosnt4ci95es
   //       ---------baseUrl-------------/thisPath;----------------------------------

    var thisUrl = $(location).attr('href');
    var baseUrl = thisUrl.substring(0,thisUrl.indexOf("/oztrack/")) + '/oztrack/';
    var thisPath = $(location).attr('pathname').replace("/oztrack/","");
    var homeUrl = $('#homeUrl').attr('href');
    var homeCrumb = '<a href="' + homeUrl + '">Home</a>';

    var breadcrumb = homeCrumb;

    switch (thisPath) {
        case "login":
            breadcrumb = breadcrumb + ' &rsaquo; <b>Login</b>';
            break;
        case "register":
            breadcrumb = breadcrumb + ' &rsaquo; <b>Register</b>';
            break;
        case "searchacoustic":
            breadcrumb = breadcrumb + ' &rsaquo; <b>Search Acoustic data</b>';
            break;
        case "projects":
            breadcrumb = breadcrumb + ' &rsaquo; <b>My Projects</b>';
            break;
        case "projectdetail":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectdetail","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <b>' + projectTitle + '</b>';
            break;
        case "datafileadd":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafileadd","projects") + '"> &rsaquo; My Projects</a> &rsaquo; ' + projectTitle
                                    + ' &rsaquo; <b> Add a Data File </b>';
            break;
        case "searchform":
           breadcrumb = breadcrumb + ' &rsaquo; <b>Analysis Tools</b>';
            break;
        default:
            break;
    }


    //alert("thisPath: " + thisPath + ' ' + projectTitle);

    $('#crumbs').html(breadcrumb);

 }

 function accordian () {
    $('.accordianHead').click(function() {
    		$(this).next().toggle('fast');
    		return false;
    	}).next().hide();

 }

 function setupDatepicker () {
     $('#fromDatepicker').datepicker();
     $('#toDatepicker').datepicker();

    $.datepicker.setDefaults({
        dateFormat:'dd/mm/yy'
    });

 }





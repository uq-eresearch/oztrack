
$(document).ready(function(){
     nav();
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
        case "alloztrackprojects":
            breadcrumb = breadcrumb + ' &rsaquo; <b>All Projects</b>';
            break;
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
        case "projectadd":
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectadd","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <b>Add a Project</b>';
            break;
        case "projectdetail":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectdetail","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <b>' + projectTitle + '</b>';
            break;
        case "datafiles":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafiles","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("datafiles","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <b> Data Files </b>';
            break;
        case "datafileadd":
          var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafileadd","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("datafileadd","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("datafileadd","datafiles") + '"> Data Files </a> &rsaquo; <b> Add </b> ';
            break;
          case "projectanimals":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectanimals","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("projectanimals","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <b> Animals </b>';
            break;
        case "projectreceivers":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectreceivers","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("projectreceivers","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <b> Receivers </b>';
            break;
       case "animalform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("animalform","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("animalform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("animalform","projectanimals") + '"> Animals </a> &rsaquo; <b> Edit </b> ';
            break;
       case "receiverform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("receiverform","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("receiverform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("receiverform","projectanimals") + '"> Receivers </a> &rsaquo; <b> Edit </b> ';
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

 function nav () {

   $('.menuParent').click(function() {
    		$(this).next().toggle('fast');
    		return false;
    	}).next().hide();

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





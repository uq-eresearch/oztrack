
$(document).ready(function(){
     nav();
     crumbs();
     accordian();
     setupDatepicker();


     if ($('#map_canvas').length) {
        initialize();
     }
     if ($('#sightingMap').length) {
        initializeSightingMap();
     }
});



 function crumbs() {

   //eg thisUrl http://localhost:8080/oztrack/register;jsessionid=4q9927jnlfrdosnt4ci95es
   //           ---------baseUrl-------------/thisPath;----------------------------------

    var thisUrl = $(location).attr('href');
    var baseUrl = thisUrl.substring(0,thisUrl.lastIndexOf('/'));
    var thisPath = thisUrl.substring(baseUrl.length,thisUrl.lastIndexOf(';')).replace('/','');
    var homeUrl = $('#homeUrl').attr('href');
    var homeCrumb = '<a href="' + homeUrl + '">Home</a>';
    var breadcrumb = homeCrumb;

    //alert("thisPath: " + thisPath);

    switch (thisPath) {
        case "alloztrackprojects":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">All Projects</span>';
            break;
        case "login":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Login</span>';
            break;
        case "register":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Register</span>';
            break;
        case "searchacoustic":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Search Acoustic data</span>';
            break;
        case "projects":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Tracking Projects</span>';
            break;
        case "projectadd":
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectadd","projects") + '"> &rsaquo; Tracking Projects</a> &rsaquo; <span class="aCrumb">Create New Project</span>';
            break;
        case "projectdetail":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectdetail","projects") + '"> &rsaquo; Tracking Projects</a> &rsaquo; <span class="aCrumb">' + projectTitle + '</span>';
            break;
        case "datafiles":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafiles","projects") + '"> &rsaquo; Tracking Projects</a> &rsaquo; <a href="' + thisUrl.replace("datafiles","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb"> Data Files </span>';
            break;
        case "datafileadd":
          var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafileadd","projects") + '"> &rsaquo; Tracking Projectss</a> &rsaquo; <a href="' + thisUrl.replace("datafileadd","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("datafileadd","datafiles") + '"> Data Files </a> &rsaquo; <span class="aCrumb"> Add </span> ';
            break;
          case "projectanimals":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectanimals","projects") + '"> &rsaquo; Tracking Projects</a> &rsaquo; <a href="' + thisUrl.replace("projectanimals","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb"> Animals </span>';
            break;
        case "projectreceivers":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectreceivers","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("projectreceivers","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb"> Receivers </span>';
            break;
       case "animalform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("animalform","projects") + '"> &rsaquo; Tracking Projects</a> &rsaquo; <a href="' + thisUrl.replace("animalform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("animalform","projectanimals") + '"> Animals </a> &rsaquo; <span class="aCrumb"> Edit </span> ';
            break;
       case "receiverform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("receiverform","projects") + '"> &rsaquo; Tracking Projects</a> &rsaquo; <a href="' + thisUrl.replace("receiverform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("receiverform","projectanimals") + '"> Receivers </a> &rsaquo; <span class="aCrumb"> Edit </span> ';
            break;
        case "searchform":
           breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Analysis Tools</span>';
            break;
        case "sighting":
            break;
        default:
            break;
    }

    //alert("breadcrumb: " + breadcrumb);

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
     $('#sightingDatepicker').datepicker();

    $.datepicker.setDefaults({
        dateFormat:'dd/mm/yy'
    });

 }








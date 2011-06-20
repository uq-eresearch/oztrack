
$(document).ready(function(){
     //nav();
     navigation();
     accordian();
     setupDatepicker();


     if ($('#map_canvas').length) {
        initialize();
     }
     if ($('#sightingMap').length) {
        initializeSightingMap();
     }
});



 function navigation() {

      $('.menuParent').click(function() {
            $(this).next().toggle('fast');
            return false;
        }).next().hide();

   //eg thisUrl http://localhost:8080/oztrack/register;jsessionid=4q9927jnlfrdosnt4ci95es
   //           ---------baseUrl-------------/thisPath;----------------------------------

    var thisUrl = $(location).attr('href');
    var baseUrl = thisUrl.substring(0,thisUrl.lastIndexOf('/'));
    //var thisPath = thisUrl.substring(baseUrl.length,thisUrl.lastIndexOf(';')).replace('/','');
    var thisPath = thisUrl.substring(baseUrl.length,thisUrl.length).replace('/','');

    if (thisPath.indexOf(";jsessionid=",0) != -1) {
        thisPath = thisPath.substring(0,thisPath.indexOf(";jsessionid="));
    }

    var homeUrl = $('#homeUrl').attr('href');
    var homeCrumb = '<a href="' + homeUrl + '">Home</a>';
    var breadcrumb = homeCrumb;

    //change navMenu all back to white
    $('#navMenu').find('li a').css('color','#ffffff');
    //alert("thisPath: " + thisPath);
    switch (thisPath) {
        case "home":
            breadcrumb = '<span class="aCrumb">Home</span>';
            $('#navHome').css('color','#f7a700');
            break;
        case "login":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Login</span>';
            //$('#navTrack').css('color','#f7a700');
            break;
        case "register":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Register</span>';
            $('#navHome').css('color','#f7a700');
            break;
        case "searchacoustic":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Search Acoustic data</span>';
            break;
        case "projects":
            breadcrumb = breadcrumb + ' &rsaquo; <a href="#">Animal Tracking</a> &rsaquo; <span class="aCrumb">Project List</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectadd":
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectadd","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <span class="aCrumb">Create New Project</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectdetail":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectdetail","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <span class="aCrumb">' + projectTitle + '</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "datafiles":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafiles","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("datafiles","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb"> Data Files </span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "datafileadd":
          var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafileadd","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("datafileadd","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("datafileadd","datafiles") + '"> Data Uploads </a> &rsaquo; <span class="aCrumb"> Add a Data File </span> ';
            $('#navTrack').css('color','#f7a700');
            break;
        case "datafiledetail":
          var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("datafiledetail","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("datafiledetail","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("datafiledetail","datafiles") + '"> Data Uploads </a> &rsaquo; <span class="aCrumb"> Data File Detail</span> ';
            $('#navTrack').css('color','#f7a700');
            break;
          case "projectanimals":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectanimals","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("projectanimals","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb"> Animals </span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectreceivers":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("projectreceivers","projects") + '"> &rsaquo; My Projects</a> &rsaquo; <a href="' + thisUrl.replace("projectreceivers","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb"> Receivers </span>';
            $('#navTrack').css('color','#f7a700');
            break;
       case "animalform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("animalform","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("animalform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("animalform","projectanimals") + '"> Animals </a> &rsaquo; <span class="aCrumb"> Edit </span> ';
            $('#navTrack').css('color','#f7a700');
            break;
       case "receiverform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("receiverform","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("receiverform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <a href="' + thisUrl.replace("receiverform","projectanimals") + '"> Receivers </a> &rsaquo; <span class="aCrumb"> Edit </span> ';
            $('#navTrack').css('color','#f7a700');
            break;
        case "searchform":
            var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb +  '<a href="' + thisUrl.replace("searchform","projects") + '"> &rsaquo; Animal Tracking</a> &rsaquo; <a href="' + thisUrl.replace("searchform","projectdetail") + '">' + projectTitle
                                    + '</a> &rsaquo; <span class="aCrumb">Analysis Tools</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "sighting":
            breadcrumb = breadcrumb + ' &rsaquo; <a href="#">Animal Sightings</a> &rsaquo; <span class="aCrumb">Report an Animal Sighting</span>';
            $('#navSighting').css('color','#f7a700');
            break;
        case "about":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">About</span>';
            $('#navAbout').css('color','#f7a700');
            break;
        case "contact":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Contact</span>';
            $('#navContact').css('color','#f7a700');
            break;
        default:
            break;
    }

    //alert("breadcrumb: " + breadcrumb);

    $('#crumbs').html(breadcrumb);

 }


 /*
 function nav () {

   $('.menuParent').click(function() {
    		$(this).next().toggle('fast');
    		return false;
    	}).next().hide();

 }
 */


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








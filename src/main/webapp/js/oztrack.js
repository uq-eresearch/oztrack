
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

 function crumbs() {


   //eg thisUrl http://localhost:8080/oztrack/register;jsessionid=4q9927jnlfrdosnt4ci95es
   //           ---------baseUrl-------------/thisPath;----------------------------------

    var thisUrl = $(location).attr('href');
    var baseUrl = thisUrl.substring(0,thisUrl.lastIndexOf('/'));
    var thisPath = thisUrl.substring(baseUrl.length,thisUrl.length).replace('/','');

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





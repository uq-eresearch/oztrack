
$(document).ready(function(){
 
    if (mapPage) {
        $("#leftMenu").hide();
    }
    
    if (projectPage) {
   	 $("#projectMenu").addClass('projectMenuActive');
   	 $("#projectMenu").removeClass('projectMenuHidden');
    } else {
   	 $("#projectMenu").addClass('projectMenuHidden');
   	 $("#projectMenu").removeClass('projectMenuActive');
    } 
	
	document.title='OzTrack';
     if ($('#titleText').length) {
        document.title = 'OzTrack ' + $('#titleText').val();
     }

     if ($('#sightingMap').length) {
        initializeSightingMap();
     }
     if ($('#homeMap').length) {
        initializeHomeMap();
     }
     if ($('#projectMap').length) {
        initializeProjectMap();
     }
     
     if ($('#coverageMap').length) {
    	 var bbWKT = $("#bbWKT").text();
         initializeCoverageMap(bbWKT);
      }


     navigation();
     $.datepicker.setDefaults({
         dateFormat: 'dd/mm/yy'
     });
     $( "#accordion" ).accordion();
     $( ".selector" ).accordion( "option", "autoHeight", false );
     $( ".selector" ).accordion( "option", "clearStyle", true );
//     $( ".selector" ).accordion( "option", "fillSpace", true );
         
 	$('#pageRefresh').click(function() {
	      location.reload();
 	});
 	
 	
 	var windowSizeArray = [ "width=200,height=200",
                            "width=300,height=400,scrollbars=yes" ];
 	$('a.newWindow').click(function (event){
 		 
         var url = $(this).attr("href");
         var windowName = "popUp";//$(this).attr("name");
         var windowSize = windowSizeArray[$(this).attr("rel")];

         window.open(url, windowName, "width=600,height=400,scrollbars=yes");

         event.preventDefault();

     });
     

});


// TODO: This function is deeply disturbing and needs to be removed.
// Put breadcrumbs into individual view files, just as plain markup.
// Remove duplication of style handling and weird replace calls, etc.
// Provide for a configurable base URL and use as basis for all links.
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
    
    if (thisPath.indexOf("?",0) != -1) {
        thisPath = thisPath.substring(0,thisPath.indexOf("?"));
    }

    var homeUrl = $('#homeUrl').attr('href');
    var homeCrumb = '<a href="' + homeUrl + '">Home</a>';
    var breadcrumb = homeCrumb;

    switch (thisPath) {
        case "home":
            breadcrumb = '<span class="aCrumb">Home</span>';
            $('#navHome').css('color','#f7a700');
            break;
        case "projectdetailext":
        	var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Project Detail: ' + projectTitle + '</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "login":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Login</span>';
            break;
        case "register":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Register</span>';
            $('#navHome').css('color','#f7a700');
            break;
        case "searchacoustic":
            breadcrumb = breadcrumb + ' &rsaquo; <span class="aCrumb">Search Acoustic data</span>';
            break;
        case "projects":
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="' + thisUrl + '">Animal Tracking</a>'
            	+ ' &rsaquo; <span class="aCrumb">Project List</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectadd":
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <span class="aCrumb">Create New Project</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectdetail":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <span class="aCrumb">Project Details</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "datafiles":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("datafiles","projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <span class="aCrumb">Data Uploads</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "datafileadd":
          var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("datafileadd","projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("datafileadd","datafiles") + '">Data Uploads</a>'
            	+ ' &rsaquo; <span class="aCrumb">Add a Data File</span> ';
            $('#navTrack').css('color','#f7a700');
            break;
        case "datafiledetail":
          var projectTitle = $('#projectTitle').html();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("datafiledetail", "projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("datafiledetail", "datafiles") + '">Data Uploads</a>'
            	+ ' &rsaquo; <span class="aCrumb">Data File Detail</span> ';
            $('#navTrack').css('color','#f7a700');
            break;
          case "projectanimals":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("projectanimals","projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <span class="aCrumb">Animals</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectreceivers":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("projectreceivers","projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <span class="aCrumb">Receivers</span>';
            $('#navTrack').css('color','#f7a700');
            break;
       case "animalform":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("animalform","projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("animalform","projectanimals") + '">Animals</a>'
            	+ ' &rsaquo; <span class="aCrumb">Edit</span> ';
            $('#navTrack').css('color','#f7a700');
            break;
       case "receiverform":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("receiverform","projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("receiverform","projectanimals") + '">Receivers</a>'
            	+ ' &rsaquo; <span class="aCrumb">Edit</span> ';
            $('#navTrack').css('color','#f7a700');
            break;
        case "searchform":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
                + ' &rsaquo; <a href="/projects">Animal Tracking</a>'
                + ' &rsaquo; <a href="' + thisUrl.replace("searchform", "projectdetail") + '">' + projectTitle + '</a>'
                + ' &rsaquo; <span class="aCrumb">View Raw Data</span>';
            $('#navTrack').css('color','#f7a700');
            break;
        case "projectmap":
            var projectTitle = $('#projectTitle').text();
            breadcrumb = breadcrumb
            	+ ' &rsaquo; <a href="/projects">Animal Tracking</a>'
            	+ ' &rsaquo; <a href="' + thisUrl.replace("projectmap", "projectdetail") + '">' + projectTitle + '</a>'
            	+ ' &rsaquo; <span class="aCrumb">Analysis Tools</span>';
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

    $('#crumbs').html(breadcrumb);

}

 function publishToDataSpace (id, username, action) {
	 
	 var loadingGraphicHtml = "Sending request ...";
	 $('#publicationStatus').html(loadingGraphicHtml);
	 
	 var url = "dataspace";
	 var params =  {project: id
             	  ,username: username
             	  ,action: action};

 	 var request = $.ajax({
		 url:url,
		 type: "POST",
		 data: params
	 });
	 
	 request.done(function(data) {
		 
		 var successHtml = "";
		 if (action == "publish") {
			 successHtml = "<b>Collection Manager record: </b>"
				 			 + "<br/>Published to " + data.dataSpaceAgentURL 
				 			 + "<br/>Last updated on " + data.dataSpaceAgentUpdateDate + ".<br/><br/>"
							 + "<b>Collection record: </b>"
				 			 + "<br/>Published to " + data.dataSpaceCollectionURL
				 			 + "<br/>Last updated on " + data.dataSpaceUpdateDate + ".";
		 
		 } else if (action == "delete") {
			 successHtml = "<b>Collection Manager record: </b>"
	 			 		 + "<br/>Unpublished on " + data.dataSpaceAgentUpdateDate + ".<br/><br/>"
	 			 		 + "<b>Collection record: </b>"
	 			 		 + "<br/>Unpublished on " + data.dataSpaceUpdateDate + ".";

		 }
		 $('#publicationStatus').html(successHtml);
	});

	request.fail(function(jqXHR, textStatus, data) {
		  alert( "Request failed: " + textStatus );
		});

}


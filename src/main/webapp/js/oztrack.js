
$(document).ready(function() {
 
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


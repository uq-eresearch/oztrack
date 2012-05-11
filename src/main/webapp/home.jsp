<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Home">
	<jsp:attribute name="breadcrumbs">
	    <span class="aCrumb">Home</span>
	</jsp:attribute>
	<jsp:attribute name="head">
        <script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
        <script type="text/javascript" src="js/openlayers/OpenLayers.js" /></script>
        <script type="text/javascript" src="js/homemap.js" /></script>
        <script type="text/javascript"> 
            mapPage = true;
            $(document).ready(function() {
                $('#navHome').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
	<jsp:body>
	    <div class="mapTool">
	        <div id="homeMapOptions">
	            <h1>Welcome to OzTrack Beta!</h1>
	            <p style="font-size:1.2em">
		            OzTrack is a free-to-use web-based platform for the analysis and 
		            visualisation of animal tracking data. It was developed for the Australian 
		            animal tracking community but can be used to determine, measure and plot 
		            home-ranges for animals anywhere in the world.
	            </p>
	            <div id="logos" style="margin: 75px 40px 0 40px;">
	                <a href="http://nectar.org.au/"><img src="images/nectar-logo.png" width="140px" height="32px"/></a>
	                <a href="http://ands.org.au/"><img src="images/ands-logo.png" width="90px" height="40px" style="margin-top: -8px;"/></a>
	                <a href="http://itee.uq.edu.au/~eresearch/"><img src="images/uq_logo.png" width="140px" height="40px"/></a>
	            </div>
	        </div>
	        <div id="homeMap"></div>
	        <div class="clearboth">&nbsp;</div>  
	    </div>
	</jsp:body>
</tags:page>
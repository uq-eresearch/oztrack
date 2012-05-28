<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Report Animal Sighting">
    <jsp:attribute name="head">
		<!--<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}"></script>-->
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
		<script type="text/javascript" src="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/keydragzoom/src/keydragzoom_packed.js"></script>
		<script type="text/javascript" src="<c:url value="/js/sightingmap.js"/>"></script>
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navHome').addClass('active');
                $('#sightingDatepicker').datepicker();
                initializeSightingMap();
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="active">Report Animal Sighting</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
		<h1>Report an Animal Sighting</h1>
		
		<form:form commandName="sighting" method="POST" enctype="multipart/form-data">
		
		<h2>Sighting</h2>
		
		<div>
		<label for="sightingDate">Date:</label>
		<form:input path="sightingDate" id="sightingDatepicker"/>
		<form:errors path="sightingDate" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="sightingTime">Time:</label>
		<form:input path="sightingTime" id="sightingTime"/><br>
		<form:errors path="sightingTime" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="localityDescription">Locality Description:</label>
		<form:input path="localityDescription" id="localityDescription"/><br>
		<form:errors path="localityDescription" cssClass="formErrors"/>
		</div>
		
		<div>
		<label>Latitude/Longitude:</label>
		<form:input path="latitude" id="sightingLatitude"/><b>/</b><form:input path="longitude" id="sightingLongitude"/>
		<form:errors path="latitude" cssClass="formErrors"/><br>
		<form:errors path="longitude" cssClass="formErrors"/>
		</div>
		
		<div>
		<label>&nbsp;</label>
		<div id="sightingMap" style=""></div>
		</div>
		
		<!--
		<span>
			<b>Location/Landmarks:</b> The name of the area - for example a town, suburb, beach, or river. Include details of any landmarks to help to identify the precise location eg. a jetty or street name. <br><br>
			<b>Region/Lat/Long:</b> Moving the icon in the map will update the Region/Latitude/Longitude field in the form. Zoom in to the location for more precise coordinates. You can enter the lat/longs directly into the field if preferred.
		</span>
		-->
		
		<h2>Animal</h2>
		
		<div>
		<label for="speciesCommonName">Species Common Name:</label>
		<form:input path="speciesCommonName" id="speciesCommonName"/><br>
		<form:errors path="speciesCommonName" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="speciesScientificName">Species Scientific Name:</label>
		<form:input path="speciesScientificName" id="speciesScientificName"/><br>
		<form:errors path="speciesScientificName" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="animalDescription">Describe the Animal(s):</label>
		<form:input path="animalDescription" id="animalDescription"/><br>
		<form:errors path="animalDescription" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="comments">Any other comments to add?</label>
		<form:input path="comments" id="comments"/><br>
		<form:errors path="comments" cssClass="formErrors"/>
		</div>
		<br>
		<div>
		<label for="imageFile">Upload an image:</label>
		<input type="file" id="imageFile" name="imageFile"/><br><br>
		<form:errors path="imageFile" cssClass="formErrors"/>
		</div>
		
		<h2>Contact</h2>
		
		<div>
		<label for="contactName">Contact Name:</label>
		<form:input path="contactName" id="contactName"/><br>
		<form:errors path="contactName" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="contactEmail">Contact Email:</label>
		<form:input path="contactEmail" id="contactEmail"/><br>
		<form:errors path="contactEmail" cssClass="formErrors"/>
		</div>
		
		<br>
		<div align="center"><input type="submit" value="Submit"/></div>
		
		</form:form>
    </jsp:body>
</tags:page>

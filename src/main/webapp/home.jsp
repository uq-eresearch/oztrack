<%@ include file="header.jsp" %>
<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js" /></script>
<script type="text/javascript" src="js/oztrackmaps.js" /></script>
<script type="text/javascript"> 
	mapPage = true;
</script>	

    <div class="mapTool">
        <div id="homeMapOptions">
        	<h1>Welcome to OzTrack Beta!</h1>
        
        	<p style="font-size:1.2em">OzTrack is a free-to-use web-based platform for the analysis and 
        	visualisation of animal tracking data. It was developed for the Australian 
        	animal tracking community but can be used to determine, measure and plot 
        	home-ranges for animals anywhere in the world.</p>
        	<!-- Projects marked as pins in the map. When user clicks on pin, the bubble 
        	opens up with project details including any pictures that have been uploaded. 
        	If the data is publically available then zoom in to show trajectory data only. 
        	If not publically available go to screen which shows contact details 
        	of the user group. Data not presently available for public viewing, 
        	contact user for further details. -->
        	
        
        </div>
        <div id="homeMap"></div>
        <div class="clearboth">&nbsp;</div>  
    </div>

<!-- 
<c:forEach items="${projectList}" var="project">

	    <div class="accordianHead">
	       <table class="projectListTableHeader">
 	       <tr>
 	         <td><a href="#"><c:out value="${project.title}"/></a></td>
 	         <td><c:out value="${project.spatialCoverageDescr}"/></td>
 	         <td>Created: 01/05/2011</td>
 	        </tr>
 	        </table>
 	    </div>


    <div class="accordianBody">

        <table class="projectListTable">
        <tr><td><b>Description:</b></td><td><c:out value="${project.description}"/></td></tr>
        <tr><td><b>Species:</b></td><td>Saltwater Crocodiles</td></tr>
        <tr><td><b>Custodian Details:</b></td><td><c:out value="${project.contactName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
        <tr><td><b>Custodian Details:</b></td><td><c:out value="${project.custodianName}"/><br><c:out value="${project.custodianUrl}"/></td></tr>
        <tr><td><b>Spatial Coverage:</b></td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
        <tr><td><b>Temporal Coverage:</b></td><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
        <tr><td><b>Publications:</b></td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
        <tr><td><b>Data Globally Available?</b>
                <td><c:choose>
                    <c:when test="${project.isGlobal == true}">Y</c:when>
                    <c:otherwise>N</c:otherwise>
                </c:choose></td>
         </td></tr>

        </table>
    </div>

</c:forEach>
 -->

<%@ include file="footer.jsp" %>



<%@ include file="header.jsp" %>
<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js" /></script>
<script type="text/javascript" src="js/oztrackmaps.js" /></script>

    <h1>Welcome to OzTrack Beta!</h1>

    <div class="mapTool">
        <div id="homeMap"></div>
        <div id="homeMapOptions"></div>
        <div class="clearboth">&nbsp;</div>
    </div>

<br>

<br>

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


<%@ include file="footer.jsp" %>



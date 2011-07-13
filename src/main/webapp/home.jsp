

<jsp:include page="header.jsp">
    <jsp:param name="title" value="Home"/>
    <jsp:param name="jsIncludes" value="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}" />
    <jsp:param name="jsIncludes" value="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/keydragzoom/src/keydragzoom_packed.js" />
    <jsp:param name="jsIncludes" value="js/openlayers/OpenLayers.js" />
    <jsp:param name="jsIncludes" value="js/oztrackmaps.js" />
</jsp:include>

<!--
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
    <script src='http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAjpkAC9ePGem0lIq5XcMiuhR_wWLPFku8Ix9i2SXYRVK3e45q1BQUd_beF8dtzKET_EteAjPdGDwqpQ'></script>
-->

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


<jsp:include page="footer.jsp" />



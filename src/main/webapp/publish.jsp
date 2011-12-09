<%@ include file="header.jsp" %>


<h1>Metadata Publication</h1>

<c:choose>
<c:when test="${empty project.firstDetectionDate}">

	<p>No data has been uploaded for this project yet, so <b>no metadata record will be syndicated </b>to the <a href="http://dataspace.uq.edu.au">UQ DataSpace</a> 
	Collections Registry & subsequently on to the <a href="http://ands.org.au">Australian National Data Service</a>.</p>

</c:when>
<c:otherwise>

	<c:choose>
	<c:when test ="${empty project.dataSpaceUpdateDate}">
	
		<p>When you click 'Publish', your project metadata shown below is syndicated across to the <a href="http://dataspace.uq.edu.au">UQ DataSpace</a> 
		Collections Registry & subsequently on to the <a href="http://ands.org.au">Australian National Data Service</a>.</p>
		<p>Click 'Publish DataSpace Collection' to submit the data shown below.</p>
	
	</c:when>
	<c:otherwise>

		<p>When you click 'Update', the collection entry for this project in the the <a href="http://dataspace.uq.edu.au">UQ DataSpace</a> 
		Collections Registry & subsequently at the <a href="http://ands.org.au">Australian National Data Service</a> will be 
		updated with the data below.</p>
	
	</c:otherwise>
	</c:choose>
	
</c:otherwise>
</c:choose>

<table class="projectListTable">

<tr><td class="projectFieldName">Collection Title:</td>
	
	<td id="projectTitle"><c:out value="${project.title}"/></td></tr>

<tr><td class="projectFieldName">Collection Description:</td>

	<td><c:out value="${project.description}"/>
	&nbsp;
	This collection consists of <c:out value="${fn:length(dataFileList)}"/> datafile(s) 
	containing <c:out value="${project.detectionCount}"/> detections describing the movements of 
	<c:out value="${fn:length(projectAnimalsList)}"/> animals.
	</td></tr>

<tr><td class="projectFieldName">Collection URL:</td>
	
	<td>http://oztrack.org/projectdescr?id=<c:out value="${project.id}"/></td></tr>

<tr><td class="projectFieldName">Species:</td><td><c:out value="${project.speciesCommonName}"/>
	<i><br><c:out value="${project.speciesScientificName}"/></i>
</td></tr>

<tr><td class="projectFieldName">Collection Manager:</td>
	
    <td><c:out value="${project.contactGivenName}"/>&nbsp;<c:out value="${project.contactFamilyName}"/>
    <br><c:out value="${project.contactEmail}"/><br><c:out value="${project.contactUrl}"/></td></tr>

<tr><td class="projectFieldName">Temporal Coverage:</td><td>

	<c:choose>
	<c:when test="${empty project.firstDetectionDate}">
		No data has been uploaded for this project yet.
	</c:when>
	<c:otherwise>
		<fmt:formatDate pattern="${shortDateFormatPattern}" value="${project.firstDetectionDate}"/> 
		to 
		<fmt:formatDate pattern="${shortDateFormatPattern}" value="${project.lastDetectionDate}"/>
	</c:otherwise>
	</c:choose>
	</td></tr>
	
<tr><td class="projectFieldName">Spatial Coverage:</td>

	<td><c:out value="${project.spatialCoverageDescr}"/><br/><c:out value="${project.boundingBox}"/>
	</td></tr>

<tr><td class="projectFieldName">Rights Statement:</td>

	<td><c:out value="${project.rightsStatement}"/></td></tr>


<tr><td class="projectFieldName">Access:</td><td>
	
	<c:choose><c:when test="${project.isGlobal}">
		The data in the project is available in OzTrack for the public to use.
	</c:when>
	<c:otherwise>
		The data in this project is only available to users on the OzTrack system whom have been granted access. Contact
		the Collection Manager regarding permission and procedures for accessing the data.
	</c:otherwise>
	</c:choose>
	
</td></tr>


<tr><td class="projectFieldName">Metadata Publication Status:</td><td id="publicationStatus">
		<c:choose>
		<c:when test ="${empty project.dataSpaceUpdateDate}">
			Your project metadata has not yet been published externally.
			<c:set var="publishButtonText" value="Publish Metadata to UQ DataSpace"/> 
		</c:when>
		<c:otherwise>
			Your project metadata has been published and was last updated on 
			<fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
			<c:set var="publishButtonText" value="Update UQ DataSpace Collection Registry"/> 
		</c:otherwise>
		</c:choose>
</td></tr>

<tr><td class="projectFieldName"></td><td>
	<c:if test="${!empty project.firstDetectionDate}">
		<a class="oztrackButton" href="#" onclick='publishToDataSpace(<c:out value="${project.id}"/>,"<c:out value="${currentUser.username}"/>"); return false;'><c:out value="${publishButtonText}"/></a>
		&nbsp;&nbsp;
	</c:if>
	<a class="oztrackButton" href="<c:url value="projectadd"><c:param name="update" value="${true}"/><c:param name="id" value="${project.id}"/></c:url>">Edit</a>
	&nbsp;&nbsp;
	<a class="oztrackButton" href="<c:url value="projectdetail"/>">Cancel</a>
	<br><br></td></tr>
</table>



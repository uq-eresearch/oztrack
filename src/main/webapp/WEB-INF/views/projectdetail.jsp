<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="dd/MM/yyyy"/>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<c:set var="shortDateFormatPattern" value="MMMM yyyy"/>
<tags:page title="${project.title}">
    <jsp:attribute name="head">
		<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
		<script type="text/javascript" src="<c:url value="/js/openlayers/OpenLayers.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/coveragemap.js"/>"></script>
        <script type="text/javascript"> 
            projectPage = true;
            $(document).ready(function() {
                $('#navTrack').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <span class="aCrumb">${project.title}</span>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		
		<div id="coverageMap" style="width:240px;height:200px; float:right"></div>
		
		<div style="float:left; width:420px;">
		
		<h2 style="margin-top: 0;">Data Summary</h2>
		
		<c:choose>
		 
		 <c:when test="${(empty dataFileList)}">
			 <p>
			 There is no data uploaded for this project yet. You might like to <a href="<c:url value='/datafileadd'>
		         <c:param name="project_id" value="${project.id}"/>
		     </c:url>">upload a datafile.
			 </a>
			 </p>
		 </c:when>
		 <c:otherwise>	 	 	
			<table class="projectListTable">
			<tr>
			    <td class="projectFieldName">Datafile Count:</td>
				<td>
				    <a href="<c:url value="/datafiles">
			           <c:param name="project_id" value="${project.id}"/>
			        </c:url>"><c:out value="${fn:length(dataFileList)}"/></a>
		        </td>
		    </tr>
			<tr><td class="projectFieldName">Detection Count:</td>
							<td><a href="<c:url value="/searchform"><c:param name="project_id" value="${project.id}"/></c:url>"><c:out value="${project.detectionCount}"/></a></td></tr>
			<tr><td class="projectFieldName">Detection Date Range:</td><td><fmt:formatDate pattern="${dateFormatPattern}" value="${project.firstDetectionDate}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${project.lastDetectionDate}"/></td></tr>
			<tr><td class="projectFieldName">Animals:</td><td>
									<c:forEach items="${projectAnimalsList}" var="animal">
											<a href="<c:url value="/animalform"><c:param name="animal_id" value="${animal.id}"/></c:url>"><c:out value="${animal.animalName}"/></a>,
									  </c:forEach>
									  <a href="<c:url value="/projectanimals"><c:param name="id" value="${project.id}"/></c:url>">View All</a>	
				</td>
			</tr>
			
			<c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}">
				<tr><td class="projectFieldName">Receivers:</td><td><a href="<c:url value="/projectreceivers"><c:param name="project_id" value="${project.id}"/></c:url>">View List</a></td></tr>
			</c:if>
			</table>
		  </c:otherwise>
		</c:choose>
		
		</div>
		
		<div style="float:left">  
		<h2>Project Metadata</h2>
		<table class="projectListTable">
		<tr><td class="projectFieldName">Title:</td><td><c:out value="${project.title}"/></td></tr>
		<tr><td class="projectFieldName">Description:</td><td><c:out value="${project.description}"/></td></tr>
		<tr><td class="projectFieldName">Project Type:</td><td><c:out value="${project.projectType.displayName}"/></td></tr>
		<tr><td class="projectFieldName">Species:</td><td><c:out value="${project.speciesCommonName}"/>
			<c:if test="${!empty project.speciesScientificName}"><i><br><c:out value="${project.speciesScientificName}"/></i></c:if>
		</td></tr>
		<tr><td class="projectFieldName">Temporal Coverage:</td><td>
			<c:choose>
			<c:when test="${empty project.firstDetectionDate}">
				No data has been uploaded for this project yet.
			</c:when>
			<c:otherwise>
				<fmt:formatDate pattern="${shortDateFormatPattern}" value="${project.firstDetectionDate}"/> to <fmt:formatDate pattern="${shortDateFormatPattern}" value="${project.lastDetectionDate}"/>
			</c:otherwise>
			</c:choose>
			</td></tr>
		<tr><td class="projectFieldName">Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/>
		
		</td></tr>
		
		<tr>
		    <td class="projectFieldName">Contact:</td>
		    <td>
		        <c:out value="${project.dataSpaceAgent.firstName}"/>&nbsp;<c:out value="${project.dataSpaceAgent.lastName}"/><br>
		        <a href="mailto:<c:out value="${project.dataSpaceAgent.email}"/>"><c:out value="${project.dataSpaceAgent.email}"/></a>
		    </td>
		</tr>
		<tr><td class="projectFieldName">Contact Organisation:</td><td><c:out value="${project.dataSpaceAgent.organisation}"/></td></tr>
		
		<tr><td class="projectFieldName">Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
		<tr><td class="projectFieldName">Rights Statement:</td><td><c:out value="${project.rightsStatement}"/></td></tr>
		<tr><td class="projectFieldName">Access Rights :</td><td>
			
			<c:choose><c:when test="${project.isGlobal}">
				The data in the project is available in OzTrack for the public to use.
			</c:when>
			<c:otherwise>
				The data in this project is only available to users on the OzTrack system whom have been granted access.
			</c:otherwise>
			</c:choose>
			
		</td></tr>
		
		
		<tr><td class="projectFieldName">Metadata Publication Status:</td><td>
				<c:choose>
				<c:when test ="${empty project.dataSpaceUpdateDate}">
					This project metadata has not yet been published externally.
					<c:set var="publishButtonText" value="Publish Metadata to UQ DataSpace"/> 
				</c:when>
				<c:otherwise>
					This project metadata has been published and was last updated on 
					<fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
					<c:set var="publishButtonText" value="Update UQ DataSpace Collection Registry"/> 
				</c:otherwise>
				</c:choose>
		</td></tr>
		
		<tr><td class="projectFieldName"></td><td>
			<a class="oztrackButton" href="<c:url value="/projectadd"><c:param name="update" value="${true}"/><c:param name="id" value="${project.id}"/></c:url>">Edit Project Metadata</a>
			&nbsp;&nbsp;
			<a class="oztrackButton" href="<c:url value="/publish"><c:param name="id" value="${project.id}"/></c:url>"><c:out value="${publishButtonText}"/></a>
		
			<br><br></td></tr>
		
		</table>
		</div>
		
		<span id="bbWKT"><c:out value="${project.boundingBox}"/></span>
    </jsp:body>
</tags:page>

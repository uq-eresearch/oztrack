<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="shortDateFormatPattern" value="MMMM yyyy"/>
<tags:page title="${project.title}">
    <jsp:attribute name="head">
		<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
		<script type="text/javascript" src="<c:url value="/js/openlayers/OpenLayers.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/coveragemap.js"/>"></script>
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navHome').addClass('active');
                initializeCoverageMap($("#bbWKT").text());
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="active">${project.title}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
		<span id="projectTitle" style="display:none"><c:out value="${project.title}"/></span>
		
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		<h2>Project Details</h2>
		
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
		<tr><td class="projectFieldName">Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/><br/>
		<div id="coverageMap" style="width:240px;height:200px;"></div>
		</td></tr>
		
		<tr>
		    <td class="projectFieldName">Contact:</td>
		    <td>
		        <c:out value="${project.dataSpaceAgent.fullName}"/><br>
		        <a href="mailto:<c:out value="${project.dataSpaceAgent.email}"/>"><c:out value="${project.dataSpaceAgent.email}"/></a>
		    </td>
		</tr>
		<tr><td class="projectFieldName">Contact Organisation:</td><td><c:out value="${project.dataSpaceAgent.organisation}"/></td></tr>
		<tr><td class="projectFieldName">Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
		<tr><td class="projectFieldName">Rights Statement:</td><td><c:out value="${project.rightsStatement}"/></td></tr>
		<tr><td class="projectFieldName">Access :</td><td>
			
			<c:choose><c:when test="${project.isGlobal}">
				The data in the project is available in OzTrack for the public to use.
			</c:when>
			<c:otherwise>
				The data in this project is only available to users on the OzTrack system whom have been granted access.
			</c:otherwise>
			</c:choose>
			
		</td></tr>
		</table>
		
		<span id="bbWKT"><c:out value="${project.boundingBox}"/></span>
		<p><a href="<c:url value="/"/>">Return to Home page</a></p>
    </jsp:body>
</tags:page>

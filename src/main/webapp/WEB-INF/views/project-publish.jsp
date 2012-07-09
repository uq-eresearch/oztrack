<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<c:set var="shortDateFormatPattern" value="MMMM yyyy"/>
<tags:page title="${project.title}: Metadata Publication">
    <jsp:attribute name="head">
        <script type="text/javascript">
            function publishToDataSpace(id, username, action) {
                var loadingGraphicHtml = "Sending request ...";
                $('#publicationStatus').html(loadingGraphicHtml);
                 
                var url = "/projects/${project.id}/publish";
                var params =  {
                    project: id,
                    username: username,
                    action: action
                };
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
                    }
                    else if (action == "delete") {
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
            $(document).ready(function() {
                $('#navTrack').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">Metadata Publication</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
		<h1>Metadata Publication</h1>
		
		<c:choose>
		<c:when test="${empty project.firstDetectionDate}">
		
			<p>No data has been uploaded for this project yet, so <b>no metadata record will be syndicated </b>to the <a href="http://dataspace.uq.edu.au">UQ DataSpace</a> 
			Collections Registry and subsequently on to the <a href="http://ands.org.au">Australian National Data Service</a>.</p>
		
		</c:when>
		<c:otherwise>
		
			<c:choose>
			<c:when test ="${empty project.dataSpaceUpdateDate}">
			
				<p>When you click 'Publish', your project metadata shown below is syndicated across to the <a href="http://dataspace.uq.edu.au">UQ DataSpace</a> 
				Collections Registry and subsequently on to the <a href="http://ands.org.au">Australian National Data Service</a>.</p>
				<p>Click 'Publish DataSpace Collection' to submit the data shown below.</p>
			
			</c:when>
			<c:otherwise>
		
				<p>When you click 'Update', the collection entry for this project in the the <a href="http://dataspace.uq.edu.au">UQ DataSpace</a> 
				Collections Registry and subsequently at the <a href="http://ands.org.au">Australian National Data Service</a> will be 
				updated with the data below.</p>
			
			</c:otherwise>
			</c:choose>
			
		</c:otherwise>
		</c:choose>
		
		<table class="entityTable">
		
		<tr><th>Collection Title:</th>
			
			<td id="projectTitle"><c:out value="${project.title}"/></td></tr>
		
		<tr><th>Collection Description:</th>
		
			<td><c:out value="${project.description}"/>
			&nbsp;
			<!-- This collection consists of <c:out value="${fn:length(dataFileList)}"/> datafile(s) 
			containing <c:out value="${project.detectionCount}"/> detections describing the movements of 
			<c:out value="${fn:length(projectAnimalsList)}"/> animals. -->
			</td></tr>
		
		<tr><th>Collection URL:</th>
			
			<td>http://oztrack.org/projectdescr?id=<c:out value="${project.id}"/></td></tr>
		
		<tr><th>Species:</th><td><c:out value="${project.speciesCommonName}"/>
			<i><br><c:out value="${project.speciesScientificName}"/></i>
		</td></tr>
		
		<tr><th>Collection Manager:</th>
			
		    <td><c:out value="${project.dataSpaceAgent.fullName}"/>
		    <br><c:out value="${project.dataSpaceAgent.email}"/><br></td></tr>
		
		<tr><th>Collection Manager Description:</th>
			
		    <td><c:out value="${project.dataSpaceAgent.dataSpaceAgentDescription}"/>
			<br></td></tr>
		
		<tr><th>Temporal Coverage:</th><td>
		
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
			
		<tr><th>Spatial Coverage:</th>
		
			<td><c:out value="${project.spatialCoverageDescr}"/><br/><c:out value="${project.boundingBox}"/>
			</td></tr>
		
		<tr><th>Rights Statement:</th>
		
			<td><c:out value="${project.rightsStatement}"/></td></tr>
		
		
		<tr><th>Access:</th><td>
			
			<c:choose><c:when test="${project.isGlobal}">
				The data in the project is available in OzTrack for the public to use.
			</c:when>
			<c:otherwise>
				The data in this project is only available to users on the OzTrack system whom have been granted access. Contact
				the Collection Manager regarding permission and procedures for accessing the data.
			</c:otherwise>
			</c:choose>
			
		</td></tr>
		
		<tr><th>DataSpace Metadata Publication Status:</th><td id="publicationStatus">
		
				<c:choose>
				<c:when test ="${empty project.dataSpaceAgent.dataSpaceAgentURI}">
					<b>Collection Manager record: </b><br/>
					 Not published.
				</c:when>
				<c:otherwise>
					<b>Collection Manager record: </b><br/>
					Published to <c:out value="${dataSpaceURL}"/>agents/<c:out value = "${project.dataSpaceAgent.dataSpaceAgentURI}"/> <br/>
					Last updated on	<fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceAgent.dataSpaceAgentUpdateDate}"/>.
				</c:otherwise>
				</c:choose>
				<br/><br/>
				<c:choose>
				<c:when test ="${empty project.dataSpaceURI}">
					<b>Collection record: </b><br/>
					Not published.
					<c:set var="publishButtonText" value="Publish Metadata to UQ DataSpace"/> 
				</c:when>
				<c:otherwise>
					<b>Collection record: </b><br/>
					Published to <c:out value="${dataSpaceURL}"/>collections/<c:out value = "${project.dataSpaceURI}"/> <br/> 
					Last updated on <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
					<c:set var="publishButtonText" value="Update UQ DataSpace Collection Registry"/> 
				</c:otherwise>
				</c:choose>
				<br/>
		</td></tr>
		</table>
		
		<div style="font-size: 0.9em;">
			<a href="<c:url value="/projects/${project.id}"/>">Cancel</a>
            &nbsp;
			<c:if test="${!empty project.firstDetectionDate}">
				<a class="oztrackButton" href="#" onclick='publishToDataSpace(<c:out value="${project.id}"/>,"<c:out value="${currentUser.username}"/>","publish"); return false;'><c:out value="${publishButtonText}"/></a>
			</c:if>
            &nbsp;
			<c:if test="${!empty project.dataSpaceURI}">
				<a class="oztrackButton" href="#" onclick='publishToDataSpace(<c:out value="${project.id}"/>,"<c:out value="${currentUser.username}"/>","delete"); return false;'>Delete from UQ DataSpace</a>
			</c:if>
        </div>
    </jsp:body>
</tags:page>
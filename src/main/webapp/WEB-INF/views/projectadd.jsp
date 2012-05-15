<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<tags:page>
    <jsp:attribute name="title">
        <c:choose>
        <c:when test="${param.update}">
            Update Project Metadata
        </c:when>
        <c:otherwise>
            Create a New Project
        </c:otherwise>
        </c:choose>
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
            	$('#navTrack').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a id="homeUrl" href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="/projects">Animal Tracking</a>
        &rsaquo; <span class="aCrumb">Create New Project</span>
    </jsp:attribute>
    <jsp:body>
		<c:choose>
		<c:when test="${param.update}">
			<h1>Update Project Metadata</h1>
		</c:when>
		<c:otherwise>
			<h1>Create a New Project</h1>
		</c:otherwise>
		</c:choose>
		
		
		<p>The information collected here will be syndicated to the University of Queensland's data collection registry, DataSpace, 
		subsequently to the Australian National Data Service, ANDS. A link will be made available to complete the syndication after 
		data has been uploaded to OzTrack, and you have the opportunity to edit this information before syndication.</p>
		
		<form:form commandName="project" method="POST" name="project"  enctype="multipart/form-data">
		
		<div class="formSubheader">Data Contact</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Contact:</b><br> This person is the contact for the data and becomes the Agent specified in the ANDS Collection Registry. 
			</span></a>
			</div>
		
		<c:set var="dataSpaceAgent" value="${currentUser}"/>
		
		<c:if test="${param.update}">
			<c:set var="dataSpaceAgent" value="${project.dataSpaceAgent}"/>
		</c:if>	
		
		<div>
		<label>Name:</label>
		<div><c:out value="${dataSpaceAgent.fullName}"/>&nbsp;</div>
		</div>
		
		<div>
		<label>Organisation:</label>
		<div><c:out value="${dataSpaceAgent.organisation}"/>&nbsp;</div>
		</div>
		
		<div>
		<label>Description:</label>
		<div><c:out value="${dataSpaceAgent.dataSpaceAgentDescription}"/>&nbsp;</div>
		</div>
		
		<div>
		<label>Email:</label>
		<div><c:out value="${dataSpaceAgent.email}"/>&nbsp;</div>
		</div>
		
		<!-- 
		<c:if test="${param.update}">
		<div>
		<label>&nbsp;</label>
		<div><a href="#">Change the user prescribed as the contact for this project ... </a></div>
		</div>
		</c:if>
		 -->
		
		 
		<div class="formSubheader">Project Metadata</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Title:</b> A short title (less than 50 characters if possible) to identify your project in OzTrack.
			</span></a>
			</div>
			
		<div>
		<label for="title">*Title:</label>
		<form:textarea path="title" rows="1" cols="40" id="title"/>
		<form:errors path="title" cssClass="formErrors"/>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Description:</b><br> Required by ANDS. 
			</span></a>
			</div>
		
		<div>
		<label for="description">*Description:</label>
		<form:textarea path="description" rows="5" cols="40" id="description"/>
		<form:errors path="description" cssClass="formErrors"/>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Project Type:</b><br> What kind of telemetry device was used to track your animal? This 
										   will determine the format of the datafiles you upload, and the 
										   types of analysis available to this project's dataset. 
			</span></a>
			</div>
		
		<div>
		<label for="projectType">Project Type:</label>
		
		<form:select path="projectType">
		    <form:option value="GPS">GPS Based Telemetry</form:option>
		    <!--<form:option value="PASSIVE_ACOUSTIC">Passive Acoustic Telemetry</form:option>
		    <form:option value="ACTIVE_ACOUSTIC">Active Acoustic Telemetry</form:option>
		    <form:option value="ARGOS">ARGOS Telemetry</form:option>
		    <form:option value="RADIO">Radio Telemetry</form:option> -->
		</form:select>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Location Description:</b><br> The general area of the study, eg. country, state, town. 
			</span></a>
			</div>
		
		<div>
		<label for="spatialCoverageDescr">Location Description:</label>
		<form:input path="spatialCoverageDescr" id="spatialCoverageDescr"/>
		<form:errors path="spatialCoverageDescr" cssClass="formErrors"/>
		</div>
		
		<div class="formSubheader">Species</div>
		
		<div>
		<label for="speciesCommonName">Common Name:</label>
		<form:input path="speciesCommonName" id="speciesCommonName"/>
		<form:errors path="speciesCommonName" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="speciesScientificName">Scientific Name:</label>
		<form:input path="speciesScientificName" id="speciesScientificName"/>
		<form:errors path="speciesScientificName" cssClass="formErrors"/>
		</div>
		
		
		
		<div class="formSubheader">Image</div>
		
		<div>
		<label for="imageFile">Upload an Image file:</label>
		<input type="file" name="imageFile"/><br><br>
		<form:errors path="imageFile" cssClass="formErrors"/>
		</div>
		
		<div class="formSubheader">Publications</div>
		
		<div>
		<label for="publicationTitle">Publication Title:</label>
		<form:textarea path="publicationTitle" rows="2" cols="40" id="publicationTitle"/>
		<form:errors path="publicationTitle" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="publicationUrl">Publication URL:</label>
		<form:input path="publicationUrl" id="publicationUrl"/>
		<form:errors path="publicationUrl" cssClass="formErrors"/>
		</div>
		
		
		<div class="formSubheader">Data Availability</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Availability:</b><br> Project data is currently only available to users with Admin access to a project, but can
		be made available to all users.
			</span></a>
			</div>
		
		<div>
		<label>Access Rights:</label><form:checkbox cssClass="checkbox" path="isGlobal" id="isGlobal"/>
		The data in this project is to be made publicly available via OzTrack.
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="images/help.png" border="0">
			<span><b>Rights Statement:</b><br>This should reflect any restrictions around the access rights and use of your data.
			</span></a>
			</div>
		
		<div>
		<label>Rights Statement:</label>
		<form:textarea path="rightsStatement" rows="3" cols="40" />
		<form:errors path="rightsStatement" cssClass="formErrors"/>
		</div>
		
		<c:choose>
		<c:when test="${param.update}">
		
				<div class="help">
				<a class=info href="#"><img src="images/help.png" border="0">
				<span><b>Metadata to ANDS:</b><br> 	Project metadata on OzTrack is publicly available, and users are encouraged 
				to publish their metadata as a collection record in the Australian National Data Service. 
				</span></a>
				</div>
			
			<div>
			<label style="height:6ex;">Metadata:</label>
				<c:choose>
				<c:when test ="${empty project.dataSpaceUpdateDate}">
					Your project metadata has not yet been published to ANDS. 
					<a href="<c:url value="publish"><c:param name="id" value="${project.id}"/></c:url>">Publish to ANDS now</a>.
				</c:when>
				<c:otherwise>
					Your project metadata has been published and was last updated on 
					<fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
					You can <a href="#">update the record</a>.
					
				</c:otherwise>
				</c:choose>
			</div>
			
			<div>
			<label></label>
			<input type="submit" value="Update Project"  class="oztrackButton" />
			</div>
			
		</c:when>
		<c:otherwise>
		
			<div>
			<label></label>
			<div><input type="submit" value="Create OzTrack Project" class="oztrackButton" /></div>
			</div>
		
		</c:otherwise>
		</c:choose>
		
		</form:form>
    </jsp:body>
</tags:page>
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
        <c:when test="${project.id != null}">
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
            	$('#navTrack').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <span class="active">Create New Project</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <div class="sidebarMenu">
            <ul>
                <li><a href="<c:url value="/projects"/>">Project List</a></li>
            </ul>
        </div>
    </jsp:attribute>
    <jsp:body>
		<c:choose>
		<c:when test="${project.id != null}">
			<h1>Update Project Metadata</h1>
		</c:when>
		<c:otherwise>
			<h1>Create a New Project</h1>
		</c:otherwise>
		</c:choose>
		
		
		<p>The information collected here will be syndicated to the University of Queensland's data collection registry, DataSpace, 
		subsequently to the Australian National Data Service, ANDS. A link will be made available to complete the syndication after 
		data has been uploaded to OzTrack, and you have the opportunity to edit this information before syndication.</p>
		
        <c:choose>
            <c:when test="${project.id != null}">
                <c:set var="method" value="PUT"/>
                <c:set var="action" value="/projects/${project.id}"/>
            </c:when>
            <c:otherwise>
                <c:set var="method" value="POST"/>
                <c:set var="action" value="/projects"/>
            </c:otherwise>
        </c:choose>
		<form:form
            method="${method}" action="${action}"
            commandName="project" name="project"  enctype="multipart/form-data">
		
		<h2>Data Contact</h2>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Contact:</b><br> This person is the contact for the data and becomes the Agent specified in the ANDS Collection Registry. 
			</span></a>
			</div>
		
		<c:set var="dataSpaceAgent" value="${currentUser}"/>
		
		<c:if test="${project.id != null}">
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
		
		<h2>Project Metadata</h2>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Title:</b> A short title (less than 50 characters if possible) to identify your project in OzTrack.
			</span></a>
			</div>
			
		<div>
		<label for="title">*Title:</label>
		<form:textarea path="title" rows="1" cols="40" id="title"/>
		<form:errors path="title" cssClass="formErrors"/>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Description:</b><br> Required by ANDS. 
			</span></a>
			</div>
		
		<div>
		<label for="description">*Description:</label>
		<form:textarea path="description" rows="5" cols="40" id="description"/>
		<form:errors path="description" cssClass="formErrors"/>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Project Type:</b><br> What kind of telemetry device was used to track your animal? This 
										   will determine the format of the datafiles you upload, and the 
										   types of analysis available to this project's dataset. 
			</span></a>
			</div>
		
		<div>
		<label for="projectType">Project Type:</label>
		
		<form:select path="projectType">
		    <form:option value="GPS">GPS Based Telemetry</form:option>
		</form:select>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Location Description:</b><br> The general area of the study, eg. country, state, town. 
			</span></a>
			</div>
		
		<div>
		<label for="spatialCoverageDescr">Location Description:</label>
		<form:input path="spatialCoverageDescr" id="spatialCoverageDescr"/>
		<form:errors path="spatialCoverageDescr" cssClass="formErrors"/>
		</div>
		
		<h2>Species</h2>
		
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
		
		<h2>Publications</h2>
		
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
		
		
		<h2>Data Availability</h2>
		
		<table class="form">
        <col style="width: 180px;" />
        <col style="width: 360px;" />
        <col style="width: 40px;" />
        <tr>
            <th class="form-label">
		        Access Rights:
            </th>
            <td class="form-field">
                <form:radiobutton id="isGlobalTrue" cssClass="checkbox" path="isGlobal" value="true"/>
                <label for="isGlobalTrue" style="font-weight: bold; color: green;">Open Access</label>
                <div style="margin: 0.5em 0;">
                    Data in this project will be made publicly available via OzTrack.
                </div>
                <form:radiobutton id="isGlobalFalse" cssClass="checkbox" path="isGlobal" value="false"/>
                <label for="isGlobalFalse" style="font-weight: bold; color: red;">Restricted Access</label>
                <div style="margin: 0.5em 0;">
                    Data in this project will only be accessible to you.
                    However, note that metadata including title, description, location, and animal species
                    are made publicly available for all projects in OzTrack.
                </div>
            </td>
			<td class="form-help">
			</td>
	    </tr>
        <tr>
    		<th class="form-label">
                <label>Rights Statement:</label>
            </th>
            <td class="form-field">
        		<form:textarea path="rightsStatement" rows="3" cols="40" />
        		<form:errors path="rightsStatement" cssClass="formErrors"/>
            </td>
            <td class="form-help">
                <a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
                <span><b>Rights Statement:</b><br>This should reflect any restrictions around the access rights and use of your data.</span>
                </a>
            </td>
        </tr>
		<c:if test="${project.id != null}">
        <tr>
			<th class="form-label">
                <label>Metadata:</label>
            </th>
            <td class="form-field">
				<c:choose>
				<c:when test ="${empty project.dataSpaceUpdateDate}">
					Your project metadata has not yet been published to ANDS. 
					<a href="<c:url value="/projects/${project.id}/publish"/>">Publish to ANDS now</a>.
				</c:when>
				<c:otherwise>
					Your project metadata has been published and was last updated on 
					<fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
					You can <a href="#">update the record</a>.
				</c:otherwise>
				</c:choose>
            </td>
			<td class="form-help">
				<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
				<span>
                    <b>Metadata to ANDS:</b><br>
                    Project metadata on OzTrack is publicly available, and users are encouraged to
                    publish their metadata as a collection record in the Australian National Data Service. 
				</span>
                </a>
			</td>
        </tr>
        </c:if>
        </table>
		<div>
		    <input class="oztrackButton" type="submit" value="${(project.id != null) ? 'Update Project' : 'Create OzTrack Project'}" />
		</div>
		</form:form>
    </jsp:body>
</tags:page>

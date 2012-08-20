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
            class="form-horizontal form-bordered"
            method="${method}" action="${action}"
            commandName="project" name="project" enctype="multipart/form-data">
    		<fieldset>
    		    <legend>Data Contact</legend>
        		<c:set var="dataSpaceAgent" value="${currentUser}"/>
        		<c:if test="${project.id != null}">
        			<c:set var="dataSpaceAgent" value="${project.dataSpaceAgent}"/>
        		</c:if>	
        		<div class="control-group">
            		<label class="control-label">Name</label>
                    <div class="controls">
            		    <input type="text" disabled="disabled" value="<c:out value="${dataSpaceAgent.fullName}"/>" />
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="<c:url value="/img/help.png"/>" border="0">
                                <span>
                                    <b>Contact:</b><br>
                                    <br>
                                    This person is the contact for the data and becomes the Agent specified in the ANDS Collection Registry. 
                                </span>
                            </a>
                        </div>
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label">Organisation</label>
            		<div class="controls">
                        <input type="text" disabled="disabled" value="<c:out value="${dataSpaceAgent.organisation}"/>" />
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label">Description</label>
            		<div class="controls">
                        <input type="text" disabled="disabled" value="<c:out value="${dataSpaceAgent.dataSpaceAgentDescription}"/>" />
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label">Email</label>
            		<div class="controls">
                        <input type="text" disabled="disabled" value="<c:out value="${dataSpaceAgent.email}"/>" />
                    </div>
        		</div>
    		</fieldset>
            <fieldset>
		        <legend>Project Metadata</legend>
        		<div class="control-group">
        		    <label class="control-label" for="title">Title</label>
                    <div class="controls">
                		<form:textarea path="title" rows="1" cols="40" id="title"/>
            			<div class="help-inline">
                			<a class=info href="#">
                                <img src="<c:url value="/img/help.png"/>" border="0">
                    			<span>
                                    <b>Title:</b><br>
                                    <br>
                                    A short title (less than 50 characters if possible) to identify your project in OzTrack.
                    			</span>
                            </a>
            			</div>
                        <form:errors path="title" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label" for="description">Description</label>
                    <div class="controls">
                		<form:textarea path="description" rows="5" cols="40" id="description"/>
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="<c:url value="/img/help.png"/>" border="0">
                                <span>
                                    <b>Description:</b><br>
                                    <br>
                                    Required by ANDS. 
                                </span>
                            </a>
                        </div>
                        <form:errors path="description" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
		        <div class="control-group">
		            <label class="control-label" for="projectType">Project Type</label>
		            <div class="controls">
                		<form:select path="projectType">
                		    <form:option value="GPS">GPS Based Telemetry</form:option>
                		</form:select>
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="<c:url value="/img/help.png"/>" border="0">
                                <span>
                                    <b>Project Type:</b><br>
                                    <br>
                                    What kind of telemetry device was used to track your animal? This 
                                    will determine the format of the datafiles you upload, and the 
                                    types of analysis available to this project's dataset. 
                                </span>
                            </a>
                        </div>
                    </div>
		        </div>
        		<div class="control-group">
            		<label class="control-label" for="spatialCoverageDescr">Location</label>
                    <div class="controls">
                		<form:input path="spatialCoverageDescr" id="spatialCoverageDescr"/>
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="<c:url value="/img/help.png"/>" border="0">
                                <span>
                                    <b>Location Description:</b><br>
                                    <br>
                                    The general area of the study, eg. country, state, town. 
                                </span>
                            </a>
                        </div>
                        <form:errors path="spatialCoverageDescr" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
            </fieldset>
            <fieldset>
		       <legend>Species</legend>
        		<div class="control-group">
            		<label class="control-label" for="speciesCommonName">Common Name</label>
                    <div class="controls">
                		<form:input path="speciesCommonName" id="speciesCommonName"/>
                        <form:errors path="speciesCommonName" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label" for="speciesScientificName">Scientific Name</label>
                    <div class="controls">
                		<form:input path="speciesScientificName" id="speciesScientificName"/>
                        <form:errors path="speciesScientificName" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
            </fieldset>
		    <fieldset>
		        <legend>Publications</legend>
        		<div class="control-group">
            		<label class="control-label" for="publicationTitle">Publication Title</label>
                    <div class="controls">
                		<form:textarea path="publicationTitle" rows="2" cols="40" id="publicationTitle"/>
                        <form:errors path="publicationTitle" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label" for="publicationUrl">Publication URL</label>
                    <div class="controls">
                		<form:input path="publicationUrl" id="publicationUrl"/>
                        <form:errors path="publicationUrl" element="div" cssClass="help-block formErrors"/>
                    </div>
        		</div>
		    </fieldset>
		    <fieldset>
		        <legend>Data Availability</legend>
		        <div class="control-group">
                    <label class="control-label" for="publicationUrl">Access Rights</label>
                    <div class="controls">
                        <label for="isGlobalTrue" style="font-weight: bold; color: green;">
                            <form:radiobutton id="isGlobalTrue" cssStyle="margin: 2px 0 5px 0;" path="isGlobal" value="true"/>
                            Open Access
                        </label>
                        <div style="margin: 0.5em 0;">
                            Data in this project will be made publicly available via OzTrack.
                        </div>
                        <label for="isGlobalFalse" style="font-weight: bold; color: red;">
                            <form:radiobutton id="isGlobalFalse" cssStyle="margin: 2px 0 5px 0;" path="isGlobal" value="false"/>
                            Restricted Access
                        </label>
                        <div style="margin: 0.5em 0;">
                            Data in this project will only be accessible to you.
                            However, note that metadata including title, description, location, and animal species
                            are made publicly available for all projects in OzTrack.
                        </div>
                    </div>
	           </div>
               <div class="control-group">
            		<label class="control-label" for="publicationUrl">Rights Statement</label>
                    <div class="controls">
                		<form:textarea path="rightsStatement" rows="3" cols="40" />
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span><b>Rights Statement:</b><br>This should reflect any restrictions around the access rights and use of your data.</span>
                            </a>
                        </div>
                        <form:errors path="rightsStatement" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
        		<c:if test="${project.id != null}">
                <div class="control-group">
                    <label class="control-label">Metadata</label>
                    <div class="controls">
        				<c:choose>
        				<c:when test ="${empty project.dataSpaceUpdateDate}">
        					<p>Your project metadata has not yet been published to ANDS.</p>
        					<a href="<c:url value="/projects/${project.id}/publish"/>">Publish to ANDS now</a>.
        				</c:when>
        				<c:otherwise>
        					<p>Your project metadata has been published and was last updated on 
        					<fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.</p>
        					You can <a href="#">update the record</a>.
        				</c:otherwise>
        				</c:choose>
            			<div class="help-inline">
            				<a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
            				<span>
                                <b>Metadata to ANDS:</b><br>
                                Project metadata on OzTrack is publicly available, and users are encouraged to
                                publish their metadata as a collection record in the Australian National Data Service. 
            				</span>
                            </a>
            			</div>
                    </div>
                </div>
                </c:if>
            </fieldset>
    		<div class="form-actions">
    		    <input class="btn btn-primary" type="submit" value="${(project.id != null) ? 'Update Project' : 'Create OzTrack Project'}" />
    		</div>
		</form:form>
    </jsp:body>
</tags:page>

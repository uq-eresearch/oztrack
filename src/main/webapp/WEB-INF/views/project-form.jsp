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
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/style.css"/>" type="text/css">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/google.css"/>" type="text/css">
        <script src="http://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/proj4js/proj4js-compressed.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/LoadingPanel.js"></script>
        <script type="text/javascript" src="<c:url value="/js/srs-selector.js"/>"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                srsSelector = createSrsSelector({
                    onSrsSelected: function(id) {
                        jQuery('#srsIdentifier').val(id);
                    },
                    srsList: [
                        <c:forEach items= "${srsList}" var="srs" varStatus="status">
                        {
                            id: '<c:out value="${srs.identifier}"/>',
                            title: '<c:out value="${srs.title}"/>',
                            bounds: [
                                <c:out value="${srs.bounds.envelopeInternal.minX}"/>,
                                <c:out value="${srs.bounds.envelopeInternal.minY}"/>,
                                <c:out value="${srs.bounds.envelopeInternal.maxX}"/>,
                                <c:out value="${srs.bounds.envelopeInternal.maxY}"/>
                            ]
                        }<c:if test="${not status.last}">,</c:if>
                        </c:forEach>
                    ]
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        <c:choose>
        <c:when test="${project.id != null}">
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">Update Project</span>
        </c:when>
        <c:otherwise>
        &rsaquo; <span class="active">Create New Project</span>
        </c:otherwise>
        </c:choose>
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
            <h1>Update Project</h1>
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
                <div class="legend">Data Contact</div>
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
                    <label class="control-label">Email</label>
                    <div class="controls">
                        <input type="text" disabled="disabled" value="<c:out value="${dataSpaceAgent.email}"/>" />
                    </div>
                </div>
                <c:if test="${not empty dataSpaceAgent.dataSpaceAgentDescription}">
                <div class="control-group">
                    <label class="control-label">Description</label>
                    <div class="controls">
                        <textarea style="width: 400px; height: 100px;" disabled="disabled"
                            ><c:out value="${dataSpaceAgent.dataSpaceAgentDescription}"/></textarea>
                    </div>
                </div>
                </c:if>
            </fieldset>
            <fieldset>
                <div class="legend">Project Metadata</div>
                <div class="control-group">
                    <label class="control-label" for="title">Title</label>
                    <div class="controls">
                        <form:input path="title" id="title" cssClass="input-xxlarge"/>
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
                        <form:textarea path="description" id="description" cssStyle="width: 400px; height: 100px;"/>
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
               <div class="legend">Species</div>
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
                <div class="legend">Spatial Reference System</div>
                <p class="help-block" style="margin: 18px 0;">
                    A <strong>Spatial Reference System (SRS)</strong> specifies how the coordinates used to represent
                    spatial data are interpreted as real-world locations on the Earth's surface.
                    The system you select here will be used by the analysis features of OzTrack,
                    such as home range calculators, and will affect the accuracy of results.
                    Click the link below to select an Australian SRS based on the location of your tracking data.
                    If in doubt, enter <tt>EPSG:3577</tt> for the
                    <a href="http://spatialreference.org/ref/epsg/3577/">Australian Albers Equal Area Projection</a>,
                    an Australia-wide system suitable for geoscience and statistical mapping.
                </p>
                <div class="control-group" style="margin-bottom: 9px;">
                    <label class="control-label" for="srsIdentifier">SRS Code</label>
                    <div class="controls">
                        <form:input id="srsIdentifier" path="srsIdentifier" type="text" class="input-medium"/>
                        <form:errors path="srsIdentifier" element="div" cssClass="help-block formErrors"/>
                        <div class="help-block">
                            <a href="javascript:void(0)" onclick="srsSelector.showDialog();">Select Australian SRS</a><br>
                            <a href="javascript:void(0)" onclick="window.open('http://spatialreference.org/ref/', 'popup', 'width=800,height=600,scrollbars=yes');">Search for international SRS codes</a>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset>
                <div class="legend">Publications</div>
                <div class="control-group">
                    <label class="control-label" for="publicationTitle">Publication Title</label>
                    <div class="controls">
                        <form:input path="publicationTitle" id="publicationTitle" cssClass="input-xxlarge"/>
                        <form:errors path="publicationTitle" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="publicationUrl">Publication URL</label>
                    <div class="controls">
                        <form:input path="publicationUrl" id="publicationUrl" cssClass="input-xxlarge"/>
                        <form:errors path="publicationUrl" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
            </fieldset>
            <fieldset>
                <div class="legend">Data Availability</div>
                <div class="control-group">
                    <label class="control-label" for="publicationUrl">Access Rights</label>
                    <div class="controls">
                        <label for="isGlobalTrue" class="radio">
                            <form:radiobutton id="isGlobalTrue" path="isGlobal" value="true" onclick="$('#data-licences-control-group').fadeIn();"/>
                            <span style="font-weight: bold; color: green;">Open Access</span>
                            <div style="margin: 0.5em 0;">
                                Data in this project will be made publicly available via OzTrack.
                            </div>
                        </label>
                        <label for="isGlobalFalse" class="radio">
                            <form:radiobutton id="isGlobalFalse" path="isGlobal" value="false" onclick="$('#data-licences-control-group').fadeOut();"/>
                            <span style="font-weight: bold; color: red;">Restricted Access</span>
                            <div style="margin: 0.5em 0;">
                                Data in this project will only be accessible to you.
                                However, note that metadata including title, description, location, and animal species
                                are made publicly available for all projects in OzTrack.
                            </div>
                        </label>
                    </div>
                </div>
                <div class="control-group" id="data-licences-control-group" style="<c:if test="${!project.global}">display: none;</c:if>">
                    <label class="control-label" for="dataLicence">Data Licence</label>
                    <div class="controls">
                        <form:errors path="dataLicence" element="div" cssClass="help-block formErrors" cssStyle="margin: 5px 0 1em 0;"/>
                        <c:forEach var="dataLicence" items="${dataLicences}">
                        <label class="radio">
                            <input name="dataLicenceId" type="radio" value="${dataLicence.id}" <c:if test="${project.dataLicence.id == dataLicence.id}">checked="checked"</c:if>/>
                            <img src="${dataLicence.imageUrl}" />
                            <div style="margin: 0.5em 0px 0.5em 0px; font-weight: bold; color: #555;">
                                ${dataLicence.title}
                            </div>
                            <div style="margin: 0.5em 0px 1.5em 0px;">
                                ${dataLicence.description}
                                <a href="${dataLicence.infoUrl}">More information</a>
                            </div>
                        </label>
                        </c:forEach>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="publicationUrl">Rights Statement</label>
                    <div class="controls">
                        <form:textarea path="rightsStatement" cssStyle="width: 400px; height: 100px;"/>
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span><b>Rights Statement:</b><br>This should reflect any restrictions around the access rights and use of your data.</span>
                            </a>
                        </div>
                        <form:errors path="rightsStatement" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="${(project.id != null) ? 'Update Project' : 'Create Project'}" />
                <c:choose>
                <c:when test="${project.id != null}">
                <a class="btn" href="<c:url value="/projects/${project.id}"/>">Cancel</a>
                </c:when>
                <c:otherwise>
                <a class="btn" href="<c:url value="/projects"/>">Cancel</a>
                </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </jsp:body>
</tags:page>

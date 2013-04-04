<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="yyyy-MM-dd"/>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm:ss"/>
<c:set var="monthYearDateFormatPattern" value="MMMM yyyy"/>
<tags:page title="${project.title}: Publish Metadata">
    <jsp:attribute name="description">
        Publish metadata for the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            function publishToDataSpace(id, action) {
                var loadingGraphicHtml = "Sending request ...";
                $('#publicationStatus').html(loadingGraphicHtml);

                var url = "/projects/${project.id}/publish";
                var params =  {
                    project: id,
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
                $('#projectMenuDetails').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <span class="active">Publish Metadata</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
        <sec:authorize access="hasPermission(#project, 'write')">
        <div class="sidebar-actions">
            <div class="sidebar-actions-title">Manage Project</div>
            <ul class="icons sidebar-actions-list">
                <li class="edit-project"><a href="${pageContext.request.contextPath}/projects/${project.id}/edit">Edit project</a></li>
            </ul>
        </div>
        </sec:authorize>
    </jsp:attribute>
    <jsp:body>
        <h1>Metadata Publication</h1>

        <c:choose>
        <c:when test="${empty projectDetectionDateRange}">

            <p>No data have been uploaded for this project yet, so <b>no metadata record will be syndicated </b>to the <a target="_blank" href="http://dataspace.uq.edu.au">UQ DataSpace</a>
            Collections Registry and subsequently on to the <a target="_blank" href="http://ands.org.au">Australian National Data Service</a>.</p>

        </c:when>
        <c:otherwise>

            <c:choose>
            <c:when test ="${empty project.dataSpaceUpdateDate}">

                <p>When you click 'Publish', your project metadata shown below are syndicated across to the <a target="_blank" href="http://dataspace.uq.edu.au">UQ DataSpace</a>
                Collections Registry and subsequently on to the <a target="_blank" href="http://ands.org.au">Australian National Data Service</a>.</p>
                <p>Click 'Publish DataSpace Collection' to submit the data shown below.</p>

            </c:when>
            <c:otherwise>

                <p>When you click 'Update', the collection entry for this project in the the <a target="_blank" href="http://dataspace.uq.edu.au">UQ DataSpace</a>
                Collections Registry and subsequently at the <a target="_blank" href="http://ands.org.au">Australian National Data Service</a> will be
                updated with the data below.</p>

            </c:otherwise>
            </c:choose>

        </c:otherwise>
        </c:choose>

        <table class="entityTable">

        <tr><th>Collection Title:</th>

            <td id="projectTitle"><c:out value="${project.title}"/></td></tr>

        <tr><th>Collection Description:</th>

        <td>
            <c:out value="${project.description}"/></td>
        </tr>

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
            <c:when test="${empty projectDetectionDateRange}">
                No data have been uploaded for this project yet.
            </c:when>
            <c:otherwise>
                <fmt:formatDate pattern="${monthYearDateFormatPattern}" value="${projectDetectionDateRange.minimum}"/>
                to
                <fmt:formatDate pattern="${monthYearDateFormatPattern}" value="${projectDetectionDateRange.maximum}"/>
            </c:otherwise>
            </c:choose>
            </td></tr>

        <tr><th>Spatial Coverage:</th>

            <td><c:out value="${project.spatialCoverageDescr}"/><br/><c:out value="${projectBoundingBox}"/>
            </td></tr>

        <tr><th>Rights Statement:</th>

            <td><c:out value="${project.rightsStatement}"/></td></tr>


        <tr><th>Access:</th><td>

            <c:choose>
            <c:when test="${project.access == 'OPEN'}">
                The data in the project are available in OzTrack for the public to use.
            </c:when>
            <c:when test="${project.access == 'EMBARGO'}">
                The data in the project are covered by an embargo period,
                ending <fmt:formatDate pattern="${dateFormatPattern}" value="${project.embargoDate}"/>,
                and are currently only available to users on the OzTrack system whom have been granted access.
                Contact the Collection Manager regarding permission and procedures for accessing the data.
            </c:when>
            <c:otherwise>
                The data in this project are only available to users on the OzTrack system whom have been granted access.
                Contact the Collection Manager regarding permission and procedures for accessing the data.
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
                    Published to <c:out value="${dataSpaceUrl}"/>agents/<c:out value = "${project.dataSpaceAgent.dataSpaceAgentURI}"/> <br/>
                    Last updated on <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceAgent.dataSpaceAgentUpdateDate}"/>.
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
                    Published to <c:out value="${dataSpaceUrl}"/>collections/<c:out value = "${project.dataSpaceURI}"/> <br/>
                    Last updated on <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
                    <c:set var="publishButtonText" value="Update UQ DataSpace Collection Registry"/>
                </c:otherwise>
                </c:choose>
                <br/>
        </td></tr>
        </table>

        <div>
            <c:if test="${!empty projectDetectionDateRange}">
                <a class="btn btn-primary" href="#" onclick="publishToDataSpace(<c:out value="${project.id}"/>, 'publish'); return false;"><c:out value="${publishButtonText}"/></a>
                &nbsp;
            </c:if>
            <c:if test="${!empty project.dataSpaceURI}">
                <a class="btn btn-primary" href="#" onclick="publishToDataSpace(<c:out value="${project.id}"/>, 'delete'); return false;">Delete from UQ DataSpace</a>
                &nbsp;
            </c:if>
            <a class="btn" href="${pageContext.request.contextPath}/projects/${project.id}">Cancel</a>
        </div>
    </jsp:body>
</tags:page>

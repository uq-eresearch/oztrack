<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="yyyy-MM-dd"/>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm:ss"/>
<c:set var="monthYearDateFormatPattern" value="MMMM yyyy"/>
<tags:page title="${project.title}">
    <jsp:attribute name="description">
        <c:choose>
        <c:when test="${not empty project.description}">
            ${project.description}
        </c:when>
        <c:otherwise>
            View details for the ${project.title} project.
        </c:otherwise>
        </c:choose>
    </jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/optimised/openlayers.css" type="text/css">
        <style type="text/css">
            #coverageMap {
                float: right;
                width: 240px;
                height: 200px;
            }
            #projectData {
                float: left;
                width: 420px;
            }
            #projectDetails {
                float: left;
            }
            #content.narrow #projectDetails {
                width: 100%;
            }
            #content.wide #projectDetails {
                width: 690px;
            }
            #searchOutput {
                margin: 20px 0;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script src="${pageContext.request.scheme}://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/optimised/openlayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/coverage.js"></script>
        <script type="text/javascript">
            function addProjectUser() {
                jQuery('#projectUserError').hide();
                var userFullName = jQuery('#addProjectUserName').val();
                var userLabel = jQuery('#addProjectUserLabel').val();
                var userId = jQuery('#addProjectUserId').val();
                var role = jQuery('#addProjectUserRole').val();
                jQuery.ajax({
                    url: '${pageContext.request.contextPath}/projects/${project.id}/users',
                    type: 'POST',
                    data: {
                        'user-id': userId,
                        'role': role
                    },
                    success: function(data, textStatus, jqXHR) {
                        jQuery('#addProjectUserName').val('');
                        jQuery('#addProjectUserLabel').val('');
                        jQuery('#addProjectUserId').val('');
                        addProjectUserNode(userId, userFullName, userLabel, role);
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        var message = jQuery(jqXHR.responseText).find('error').text() || 'Error processing request';
                        jQuery('#projectUserError').text(message).fadeIn();
                    }
                });
            }
            function addProjectUserNode(userId, userFullName, userLabel, role) {
                jQuery('#' + role + 'ProjectUsersNone').hide();
                var projectUsersList = jQuery('#' + role + 'ProjectUsersList').show();
                jQuery('<li>')
                    .attr('id', role + 'ProjectUser-' + userId)
                    .addClass(role)
                    .append(
                        jQuery('<span>')
                            .attr('title', userLabel)
                            .append(userFullName)
                    )
                    <sec:authorize access="hasPermission(#project, 'manage')">
                    .append(' [')
                    .append(
                        jQuery('<a>')
                            .attr('href', 'javascript:void(0)')
                            .attr('style', 'font-size: 0.85em;')
                            .attr('onclick', 'deleteProjectUser(' + userId + ', \'' + role + '\', \'' + userFullName + '\');')
                            .append('remove')
                    )
                    .append(']')
                    </sec:authorize>
                    .hide().appendTo(projectUsersList).fadeIn();
            }
            function deleteProjectUser(userId, role, userFullName) {
                jQuery('#projectUserError').hide();
                if (!confirm('Are you sure you want to remove ' + userFullName + ' from this project?')) {
                    return;
                }
                jQuery.ajax({
                    url: '${pageContext.request.contextPath}/projects/${project.id}/users/' + userId,
                    type: 'POST',
                    data: {
                        '_method': 'DELETE'
                    },
                    success: function(data, textStatus, jqXHR) {
                        deleteProjectUserNode(userId, role);
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        var message = jQuery(jqXHR.responseText).find('error').text() || 'Error processing request';
                        jQuery('#projectUserError').text(message).fadeIn();
                    }
                });
            }
            function deleteProjectUserNode(userId, role) {
                var projectUsersNone = jQuery('#' + role + 'ProjectUsersNone');
                var projectUsersList = jQuery('#' + role + 'ProjectUsersList');
                if (projectUsersList.children().length == 1) {
                    projectUsersList.hide();
                    projectUsersNone.fadeIn();
                }
                jQuery('#' + role + 'ProjectUser-' + userId).remove();
            }
            jQuery(document).ready(function() {
                jQuery('#navTrack').addClass('active');
                jQuery('#projectMenuDetails').addClass('active');
                <c:if test="${not empty projectBoundingBox}">
                var coverageMap = createCoverageMap('coverageMap', '<c:out value="${projectBoundingBox}"/>');
                </c:if>
                <c:forEach items="${roles}" var="role">
                    <c:choose>
                    <c:when test="${empty projectUsersByRole[role]}">
                jQuery('#${role.identifier}ProjectUsersNone').show();
                    </c:when>
                    <c:otherwise>
                jQuery('#${role.identifier}ProjectUsersList').show();
                        <c:forEach items="${projectUsersByRole[role]}" var="projectUser">
                addProjectUserNode(${projectUser.user.id}, '${projectUser.user.fullName}', '${projectUser.user.fullName} (${projectUser.user.username})', '${role.identifier}');
                        </c:forEach>
                    </c:otherwise>
                    </c:choose>
                </c:forEach>
                jQuery('#addProjectUserName').autocomplete({
                    source: '${pageContext.request.contextPath}/users/search',
                    minLength: 2,
                    select: function(event, ui) {
                        jQuery('#addProjectUserId').val(ui.item ? ui.item.id : '');
                        jQuery('#addProjectUserLabel').val(ui.item ? ui.item.label : '');
                    }
                });
            });
        </script>
        <script type="text/javascript">
            $('#searchTernLink').click(function(e) {
                e.preventDefault();
                $.ajax({
                    url: 'http://portal.tern.org.au/ternapi/search',
                    data: $('#searchTernForm').serialize(),
                    dataType: "jsonp",
                    success: function(data, textStatus, jqXHR) {
                        var items =
                            $.isArray(data.response.item) ? data.response.item : // array of results
                            data.response.item ? [data.response.item] :          // single result => convert to array
                            [];                                                  // no results => create empty array
                        var output = $('#searchOutput').empty();
                        if (items.length > 0) {
                            output.append($('<ol>').append($.map(items, function(item) {
                                return $('<li>').append([
                                    $('<a>')
                                        .attr('href', item.link)
                                        .attr('title', item.description)
                                        .text(item.title),
                                    $('<span>').text(' (' + item.temporal + ')')
                                ]);
                            })));
                        }
                        else {
                            output.append($('<p>').text('No results found'));
                        }
                    }
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <span class="active">${project.title}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
        <tags:project-actions project="${project}"/>
        <tags:project-licence project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>

        <c:if test="${not empty projectBoundingBox}">
        <div id="coverageMap"></div>
        </c:if>

        <sec:authorize access="hasPermission(#project, 'read')">
        <div id="projectData">
        <h2 style="margin-top: 0;">Data Summary</h2>
        <c:choose>
        <c:when test="${(empty dataFileList)}">
             <p>
                 There are no data uploaded for this project yet.
                 <sec:authorize access="hasPermission(#project, 'write')">
                 <a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles/new">Upload a data file</a>.
                 </sec:authorize>
             </p>
        </c:when>
        <c:otherwise>
            <table class="entityTable">
            <col style="width: 120px;" />
            <col style="width: 550px;" />
            <sec:authorize access="hasPermission(#project, 'write')">
            <tr>
                <th>Data File Count:</th>
                <td><a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles"><c:out value="${fn:length(dataFileList)}"/></a></td>
            </tr>
            </sec:authorize>
            <tr>
                <th>Date Range:</th>
                <td><fmt:formatDate pattern="${dateFormatPattern}" value="${projectDetectionDateRange.minimum}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${projectDetectionDateRange.maximum}"/></td>
            </tr>
            <tr>
                <th>Detection Count:</th>
                <td><a href="${pageContext.request.contextPath}/projects/${project.id}/search"><c:out value="${projectDetectionCount}"/></a></td>
            </tr>
            <tr>
                <th>Animals:</th>
                <td>
                    <c:forEach items="${projectAnimalsList}" var="animal">
                    <a href="${pageContext.request.contextPath}/animals/${animal.id}"><c:out value="${animal.animalName}"/></a>,
                    </c:forEach>
                    <a href="${pageContext.request.contextPath}/projects/${project.id}/animals">View All</a>
                </td>
            </tr>
            </table>
        </c:otherwise>
        </c:choose>
        </div>
        </sec:authorize>

        <div id="projectDetails">
        <h2 style="margin-top: 0;">Project Details</h2>
        <table class="entityTable">
        <col style="width: 120px;" />
        <col style="width: 550px;" />
        <tr>
            <th>Title:</th>
            <td><c:out value="${project.title}"/></td>
        </tr>
        <tr>
            <th>Description:</th>
            <td><c:out value="${project.description}"/></td>
        </tr>
        <tr>
            <th>Species:</th>
            <td>
                <c:out value="${project.speciesCommonName}"/>
                <c:if test="${!empty project.speciesScientificName}"><i><br><c:out value="${project.speciesScientificName}"/></i></c:if>
          </td>
        </tr>
        <tr>
            <th>Temporal Coverage:</th>
            <td>
                <c:choose>
                <c:when test="${empty projectDetectionDateRange}">
                    No data have been uploaded for this project yet.
                </c:when>
                <c:otherwise>
                    <fmt:formatDate pattern="${monthYearDateFormatPattern}" value="${projectDetectionDateRange.minimum}"/> to
                    <fmt:formatDate pattern="${monthYearDateFormatPattern}" value="${projectDetectionDateRange.maximum}"/>
                </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <c:if test="${not empty project.srsIdentifier}">
        <tr>
            <th>Spatial Reference System:</th>
            <td><a href="http://spatialreference.org/ref/?search=<c:out value="${project.srsIdentifier}"/>"><c:out value="${project.srsIdentifier}"/></a></td>
        </tr>
        </c:if>
        <tr>
            <th>Spatial Coverage:</th>
            <td><c:out value="${project.spatialCoverageDescr}"/></td>
        </tr>
        <tr>
            <th>Contact:</th>
            <td>
                <c:out value="${project.dataSpaceAgent.firstName}"/>&nbsp;<c:out value="${project.dataSpaceAgent.lastName}"/><br>
                <a href="mailto:<c:out value="${project.dataSpaceAgent.email}"/>"><c:out value="${project.dataSpaceAgent.email}"/></a>
            </td>
        </tr>
        <tr>
            <th>Organisation:</th>
            <td><c:out value="${project.dataSpaceAgent.organisation}"/></td>
        </tr>
        <c:if test="${not empty project.publications}">
        <tr>
            <th>Publications:</th>
            <td>
                <ul class="unstyled">
                <c:forEach var="publication" items="${project.publications}">
                <li><a href="<c:out value="${publication.url}"/>"><c:out value="${publication.title}"/></a></li>
                </c:forEach>
                </ul>
            </td>
        </tr>
        </c:if>
        
        <tr>
            <th>Access Rights:</th>
            <td>
            <c:choose>
            <c:when test="${project.access == 'OPEN'}">
                The data in the project are available in OzTrack for the public to use.
            </c:when>
            <c:when test="${project.access == 'EMBARGO'}">
                The data in the project are covered by an embargo period,
                ending <fmt:formatDate pattern="${dateFormatPattern}" value="${project.embargoDate}"/>,
                and are currently only available to users on the OzTrack system whom have been granted access.
            </c:when>
            <c:otherwise>
                The data in this project are only available to users on the OzTrack system whom have been granted access.
            </c:otherwise>
            </c:choose>
            </td>
        </tr>

        <c:if test="${(project.access == 'OPEN') and (project.dataLicence != null)}">
        <tr>
            <th>Data Licence:</th>
            <td>
                <p>
                    <img src="${project.dataLicence.imageUrl}" />
                </p>
                <p>
                    <span style="font-weight: bold;">${project.dataLicence.title}</span>
                </p>
                <p>
                    ${project.dataLicence.description}
                    <a href="${project.dataLicence.infoUrl}">More information</a>
                </p>
            </td>
        </tr>
        </c:if>
        
        <c:if test="${not empty project.rightsStatement}">
        <tr>
            <th>Rights Statement:</th>
            <td><c:out value="${project.rightsStatement}"/></td>
        </tr>
        </c:if>

        <sec:authorize access="hasPermission(#project, 'manage')">
        <tr>
            <th>Publication Status:</th>
            <td>
                <c:choose>
                <c:when test ="${empty project.dataSpaceUpdateDate}">
                    This project metadata has not yet been published externally.
                </c:when>
                <c:otherwise>
                    This project metadata has been published and was last updated on
                    <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
                </c:otherwise>
                </c:choose>
            </td>
        </tr>
        </sec:authorize>

        </table>
        </div>

        <div style="clear: both;"></div>

        <sec:authorize access="hasPermission(#project, 'manage')">
        <h2>User Roles</h2>
        <table style="width: 100%; margin: 0;">
        <c:forEach items="${roles}" var="role">
        <col style="width: ${100.0 / fn:length(roles)}%;" />
        </c:forEach>
        <tr>
            <c:forEach items="${roles}" var="role">
            <th style="border-bottom: 1px solid #e6e6c0; text-align: left; padding: 4px;">
                ${role.pluralTitle}
                <div class="help-popover" title="${role.pluralTitle}">
                    ${role.explanation}
                </div>
            </th>
            </c:forEach>
        </tr>
        <tr>
            <c:forEach items="${roles}" var="role">
            <td>
                <div id="${role.identifier}ProjectUsersNone" style="margin: 0.3em 4px; color: #999; display: none;">
                    No users with ${role.title} role
                </div>
                <ul id="${role.identifier}ProjectUsersList" class="icons" style="display: none;">
                </ul>
            </td>
            </c:forEach>
        </tr>
        </table>
        <form
            onsubmit="return false;"
            class="form-inline"
            style="padding: 10px; background-color: #f6f6e6; border: 0px solid #e6e6c0;
                -webkit-border-radius: 0px; -moz-border-radius: 0px; -ms-border-radius: 0px; -o-border-radius: 0px; border-radius: 0px;">
            <span style="line-height: 1.8em; margin-right: 10px;">Assign a new user to the project:</span>
            <input id="addProjectUserId" type="hidden" />
            <input id="addProjectUserLabel" type="hidden" />
            <input id="addProjectUserName" type="text" style="width: 200px;" placeholder="Type name to search" />
            <select id="addProjectUserRole" style="width: auto;">
                <c:forEach items="${roles}" var="role">
                <option value="${role.identifier}">${role.title}</option>
                </c:forEach>
            </select>
            <button class="btn" id="addProjectUserButton" onclick="addProjectUser();">Add</button>
        </form>
        <div id="projectUserError" class="alert alert-error" style="margin-top: -12px; display: none;"></div>
        </sec:authorize>

        <h2 style="margin-top: 0;">Search Related Sites</h2>

        <div class="buttons">
            <a id="searchTernLink" href="#"><img class="img-polaroid" src="${pageContext.request.contextPath}/img/tern-logo.gif" /></a>
        </div>
        <form id="searchTernForm" class="form-horizontal form-bordered" style="display: none; padding-bottom: 9px;">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="term">Term</label>
                    <div class="controls">
                        <input id="term" class="input-xxlarge" name="term" type="text" placeholder="Keyword or *:*" value="${project.title}"/>
                        <div class="help-block">Example: australia</div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="temporal">Temporal</label>
                    <div class="controls">
                        <input id="temporal" class="input-xxlarge" name="temporal" type="text" placeholder="YYYY-YYYY" />
                        <div class="help-block">Example: 2000-2010</div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="g">Geometry</label>
                    <div class="controls">
                        <input id="g" class="input-xxlarge" name="g" type="text" placeholder="WKT" />
                        <div class="help-block">Example: POLYGON((138.50 -27.25,141.498 -21.32,146.8 -28.34,140.88 -30.63,138.5 -27.25))</div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="b">Bounding box</label>
                    <div class="controls">
                        <input id="b" class="input-xxlarge" name="b" type="text" placeholder="northlimit,westlimit,southlimit,eastlimit" />
                        <div class="help-block">Example: -35.42,138.77,-37.89,130.51</div>
                    </div>
                </div>
                <input type="hidden" name="w" value="1" />
                <input type="hidden" name="count" value="10" />
                <input type="hidden" name="format" value="json" />
            </fieldset>
        </form>
        <div id="searchOutput">
        </div>
    </jsp:body>
</tags:page>

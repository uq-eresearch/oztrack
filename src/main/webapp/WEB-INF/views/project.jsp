<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dataSpaceEnabled"><%= OzTrackApplication.getApplicationContext().isDataSpaceEnabled() %></c:set>
<c:set var="dateFormatPattern" value="yyyy-MM-dd"/>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm:ss"/>
<c:set var="canEditImages">
<sec:authorize access="hasPermission(#project, 'write')">true</sec:authorize>
</c:set>
<c:set var="showThumbnails">
<c:choose>
<c:when test="${fn:length(project.images) > 0}">true</c:when>
<c:otherwise>${canEditImages}</c:otherwise>
</c:choose>
</c:set>
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
                width: 220px;
                height: 180px;
                margin-top: 11px; /* to match h2 */
            }
            .searchButton {
                display: inline-block;
                padding: 2px 9px;
            }
            .searchButton img {
                max-height: 36px;
            }
            .searchForm {
                display: none;
                margin: 18px 0;
                padding-bottom: 1px;
            }
            .searchOutput {
                display: none;
                margin-bottom: 18px;
            }
            .searchOutput h3 {
                font-size: 1em;
                padding-bottom: 4px;
                color: #333;
                border-bottom: 1px solid #c7c7c7;
            }
            #thumbnails li.span2 {height: 110px;}
            #thumbnails li.span1 {height: 46px;}
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
            projectImages = [
                <c:forEach var="projectImage" items="${project.images}" varStatus="projectImageStatus">
                {
                    imageUrl: '${pageContext.request.contextPath}/projects/${project.id}/images/${projectImage.id}',
                    fileUrl: '${pageContext.request.contextPath}/projects/${project.id}/images/${projectImage.id}/file',
                    thumbnailUrl: '${pageContext.request.contextPath}/projects/${project.id}/images/${projectImage.id}/thumbnail'
                }<c:if test="${!projectImageStatus.last}">,</c:if>
                </c:forEach>
            ];
            function initProjectImages() {
                var maxThumbnails = 15;
                var numBlocksRequired = Math.min(maxThumbnails, projectImages.length + ${canEditImages ? 1 : 0});
                var numMajorProjectImages =
                    (numBlocksRequired <= 6) ? 4
                    : (numBlocksRequired <= 9) ? 3
                    : (numBlocksRequired <= 12) ? 2
                    : (numBlocksRequired <= 15) ? 1
                    : 0;
                var majorProjectImageSpan = 2;
                var majorProjectImageHeight = 100;
                var minorProjectImageSpan = 1;
                var minorProjectImageHeight = 36;
                var tallAddImagePadding = 36;
                $.each(projectImages, function(i, projectImage) {
                    var projectImageSpan = (i < numMajorProjectImages) ? majorProjectImageSpan : minorProjectImageSpan;
                    var projectImageHeight = (i < numMajorProjectImages) ? majorProjectImageHeight : minorProjectImageHeight;
                    var li = $('<li>')
                        .addClass('span' + projectImageSpan)
                        .append($('<a>')
                            .addClass('thumbnail')
                            .css('height', projectImageHeight + 'px')
                            .attr('href', projectImage.fileUrl)
                            .append($('<img>')
                                .css('max-height', projectImageHeight + 'px')
                                .attr('src', projectImage.thumbnailUrl)
                            )
                        );
                    $('#thumbnails').append(li);
                    <c:if test="${canEditImages}">
                    var deleteLink = $('<a>')
                        .css('display', 'inline-block')
                        .css('width', '16px')
                        .css('height', '16px')
                        .css('line-height', '16px')
                        .css('padding', '1px 2px')
                        .click(function(e) {
                            e.preventDefault();
                            OzTrack.deleteEntity(
                                projectImage.imageUrl,
                                '${pageContext.request.contextPath}/projects/${project.id}',
                                'Are you sure you want to delete this image?'
                            );
                        })
                        .append($('<img>').attr('src', '${pageContext.request.contextPath}/img/delete.png'));
                    li.append(deleteLink)
                    deleteLink.position({my: "right top", at: "right top", of: li});
                    </c:if>
                });
                <c:if test="${canEditImages}">
                if (projectImages.length < maxThumbnails) {
                    var addImageHeight =
                        (projectImages.length == numMajorProjectImages)
                        ? ((majorProjectImageHeight - tallAddImagePadding + 4) + 'px')
                        : (projectImages.length < numMajorProjectImages)
                        ? (majorProjectImageHeight + 'px')
                        : (minorProjectImageHeight + 'px');
                    $('#thumbnails').append($('<li>')
                        .addClass('span' + ((projectImages.length < numMajorProjectImages) ? majorProjectImageSpan : minorProjectImageSpan))
                        .css('height', addImageHeight)
                        .append($('<a>')
                            .addClass('thumbnail')
                            .css('text-align', 'center')
                            .css('height', addImageHeight)
                            .css('font-size', (projectImages.length < numMajorProjectImages) ? '' : 'smaller')
                            .css('line-height', ((projectImages.length < numMajorProjectImages) ? majorProjectImageHeight + 'px' : '16px'))
                            .css('padding-top', ((projectImages.length == numMajorProjectImages) ? (tallAddImagePadding + 'px') : ''))
                            .click(function(e) {e.preventDefault(); addImage();})  
                            .text('Add image')
                        )
                    );
                }
                </c:if>
            }
            function addImage() {
                $('#addImageForm').dialog({
                    title: 'Add image',
                    modal: true,
                    resizable: false,
                    create: function(event, ui) {
                        $(event.target).closest('.ui-dialog').find('.ui-dialog-titlebar-close').text('×');
                    },
                    buttons: {
                        'Upload': function() {
                            $('#addImageForm').submit();
                        }
                    }
                });
            }
            jQuery(document).ready(function() {
                jQuery('#navTrack').addClass('active');
                jQuery('#projectMenuDetails').addClass('active');
                initProjectImages();
                coverageMap = new OzTrack.CoverageMap('coverageMap', {
                    wkt: '${projectBoundingBox}',
                    crosses180: ${project.crosses180}
                });
                <sec:authorize access="hasPermission(#project, 'read')">
                $('#coverageMap').append($('<div>')
                    .css('display', 'inline-block')
                    .css('z-index', '10000000')
                    .css('padding', '8px')
                    .attr('id', 'viewTracksButtonDiv')
                    .append($('<a>')
                        .attr('id', 'viewTracksButton')
                        .addClass('btn')
                        .attr('href', '${pageContext.request.contextPath}/projects/${project.id}/analysis')
                        .append('View tracks')
                    )
                );
                $('#viewTracksButtonDiv').position({my: 'center', at: 'center', of: '#coverageMap'}).fadeIn();
                </sec:authorize>
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
            function searchTern() {
                $.ajax({
                    url: 'http://portal.tern.org.au/ternapi/search',
                    data: $('#searchTernForm').serialize(),
                    dataType: "jsonp",
                    success: function(data, textStatus, jqXHR) {
                        var items =
                            $.isArray(data.response.item) ? data.response.item : // array of results
                            data.response.item ? [data.response.item] :          // single result => convert to array
                            [];                                                  // no results => create empty array
                        var output = $('#searchTernOutput').empty();
                        output.append($('<h3>').append('Search results'));
                        if (items.length > 0) {
                            output.append($('<ol>').append($.map(items, function(item) {
                                var a = $('<a>')
                                    .attr('target', '_blank')
                                    .attr('href', item.link)
                                    .text(item.title);
                                if (item.description) {
                                    a.attr('title', item.description);
                                }
                                var li = $('<li>').append(a);
                                if (item.temporal) {
                                    li.append($('<span>').text(' (' + item.temporal + ')'));
                                }
                                return li;
                            })));
                        }
                        else {
                            output.append($('<p>').text('No results found'));
                        }
                        $('#searchTernForm').append(output.fadeIn());
                        $('body').animate({scrollTop: output.offset().top});
                    }
                });
            }
            function searchAla() {
                $.ajax({
                    url: 'http://bie.ala.org.au/search.json',
                    data: $('#searchAlaForm').serialize(),
                    dataType: "jsonp",
                    success: function(data, textStatus, jqXHR) {
                        var results = data.searchResults.results;
                        var output = $('#searchAlaOutput').empty();
                        output.append($('<h3>').append('Search results'));
                        if (results.length > 0) {
                            output.append($('<ol>').append($.map(results, function(result) {
                                var a = $('<a>')
                                    .attr('target', '_blank')
                                    .attr('href', 'http://bie.ala.org.au/species/' + result.guid)
                                    .text(result.name);
                                if (result.description) {
                                    a.attr('title', result.description);
                                }
                                return $('<li>').append(a);
                            })));
                        }
                        else {
                            output.append($('<p>').text('No results found'));
                        }
                        $('#searchAlaForm').append(output.fadeIn());
                        $('body').animate({scrollTop: output.offset().top});
                    }
                });
            }
            $('#searchTernForm').submit(function(e) {
                e.preventDefault();
                searchTern();
            });
            $('#searchAlaForm').submit(function(e) {
                e.preventDefault();
                searchAla();
            });
            $('#searchTernButton').click(function(e) {
                $('.searchForm').not('#searchTernForm').hide();
                if ($('#searchTernForm:visible').size() != 0) {
                    $('#searchTernForm').fadeOut();
                }
                else {
                    $('#searchTernForm').fadeIn();
                    searchTern();
                }
            });
            $('#searchAlaButton').click(function(e) {
                $('.searchForm').not('#searchAlaForm').hide();
                if ($('#searchAlaForm:visible').size() != 0) {
                    $('#searchAlaForm').fadeOut();
                }
                else {
                    $('#searchAlaForm').fadeIn();
                    searchAla();
                }
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
        
        <c:if test="${not empty project.description}">
        <div class="row">
        <div class="span9">
        <p>
            <c:out value="${project.description}"/>
        </p>
        </div> <!-- .span9 -->
        </div> <!-- .row -->
        </c:if>
        
        <c:if test="${showThumbnails}">
        <div class="row">
        <div class="span9">
        <ul id="thumbnails" class="thumbnails" style="margin-bottom: 0;">
        </ul>
        </div> <!-- .span3 -->
        </div> <!-- .row -->
        </c:if>

        <c:if test="${canEditImages}">
        <form
            id="addImageForm"
            class="form-vertical"
            style="display: none;"
            method="POST"
            action="${pageContext.request.contextPath}/projects/${project.id}/images"
            enctype="multipart/form-data">
            <fieldset>
                <div class="control-group">
                    <div class="controls">
                        <input name="file" type="file" />
                    </div>
                </div>
            </fieldset>
        </form>
        </c:if>

        <div class="row">
        
        <div class="span6">

        <h2>Project Summary</h2>

        <div class="row">

        <c:set var="generalInfoSpan">
        <sec:authorize access="hasPermission(#project, 'read')">3</sec:authorize>
        <sec:authorize access="!hasPermission(#project, 'read')">6</sec:authorize>
        </c:set>
        <div class="span${generalInfoSpan}">
        <dl>
            <dt>Species</dt>
            <dd>
                <p>
                    <c:if test="${!empty project.speciesScientificName}">
                    <i><c:out value="${project.speciesScientificName}"/></i>
                    <c:if test="${!empty project.speciesCommonName}">
                    <br/>
                    </c:if>
                    </c:if>
                    <c:if test="${!empty project.speciesCommonName}">
                    <c:out value="${project.speciesCommonName}"/>
                    </c:if>
                </p>
            </dd>
            <dt>Location</dt>
            <dd>
                <p><c:out value="${project.spatialCoverageDescr}"/></p>
            </dd>
            <c:if test="${not empty projectDetectionDateRange}">
            <dt>Date Range</dt>
            <dd>
                <p><fmt:formatDate pattern="${dateFormatPattern}" value="${projectDetectionDateRange.minimum}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${projectDetectionDateRange.maximum}"/></p>
            </dd>
            </c:if>
        </dl>
        </div> <!-- .span3|.span6 -->

        <sec:authorize access="hasPermission(#project, 'read')">
        <div class="span3">
        <dl>
            <c:if test="${not empty projectAnimalsList}">
            <dt>Animals</dt>
            <dd>
                <p><a href="${pageContext.request.contextPath}/projects/${project.id}/animals">${fn:length(projectAnimalsList)} animals</a></p>
            </dd>
            </c:if>
            <dt>Detections</dt>
            <dd>
                <p><a href="${pageContext.request.contextPath}/projects/${project.id}/search">${projectDetectionCount} detections</a></p>
            </dd>
            <c:if test="${not empty project.srsIdentifier}">
            <dt>Spatial Reference System</dt>
            <dd>
                <c:url var="srsHref" value="http://spatialreference.org/ref/">
                    <c:param name="search">${project.srsIdentifier}</c:param>
                </c:url>
                <p><a target="_blank" href="${srsHref}"><c:out value="${project.srsIdentifier}"/></a></p>
            </dd>
            </c:if>
        </dl>
        </div> <!-- .span3 -->
        </sec:authorize>
        
        </div> <!-- .row -->

        </div> <!-- .span6 -->

        <div class="span3">
        <div id="coverageMap"></div>
        </div> <!--  .span3 -->

        </div> <!-- .row -->

        <c:if test="${not empty project.publications}">
        <div class="row">
        <div class="span9">
        <dl>
            <dt>Publications</dt>
            <dd>
            <ol>
                <c:forEach var="publication" items="${project.publications}">
                <li>
                    <c:out value="${publication.reference}"/>
                    <c:if test="${not empty publication.url}">
                    [<a target="_blank" href="<c:out value="${publication.url}"/>">Link</a>]
                    </c:if>
                </li>
                </c:forEach>
            </ol>
            </dd>
        </dl>
        </div> <!-- .span6 -->
        </div> <!-- .row -->
        </c:if>

        <h2>Data Access</h2>

        <c:set var="dataAccessRowClass">
        <c:choose>
        <c:when test="${project.access == 'OPEN'}">
        project-access-open
        </c:when>
        <c:when test="${project.access == 'EMBARGO'}">
        project-access-embargo
        </c:when>
        <c:otherwise>
        project-access-closed
        </c:otherwise>
        </c:choose>
        </c:set>
        <div class="row ${dataAccessRowClass}" style="margin-left: 0; padding: 0;">

        <div class="span6" style="width: 449px; margin: 7px 0px 0px 10px;">
        <c:choose>
        <c:when test="${project.access == 'OPEN'}">
        <p class="project-access-open-title">Open Access</p>
        <p>
            The data in this project are available for public use.
        </p>
        </c:when>
        <c:when test="${project.access == 'EMBARGO'}">
        <p class="project-access-embargo-title">Delayed Open Access</p>
        <p>
            The data in this project are under embargo until
            <fmt:formatDate pattern="${dateFormatPattern}" value="${project.embargoDate}"/>.
        </p>
        </c:when>
        <c:otherwise>
        <p class="project-access-closed-title">Closed Access</p>
        <p>
            The data in this project are not publicly available.
        </p>
        </c:otherwise>
        </c:choose>
        <c:if test="${not empty project.rightsStatement}">
        <p><c:out value="${project.rightsStatement}"/></p>
        </c:if>
        <sec:authorize access="hasPermission(#project, 'manage')">
        <c:if test="${dataSpaceEnabled}">
        <dt>Publication Status</dt>
        <dd>
            <p>
            <c:choose>
            <c:when test ="${empty project.dataSpaceUpdateDate}">
                This project metadata has not yet been published externally.
            </c:when>
            <c:otherwise>
                This project metadata has been published and was last updated on
                <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
            </c:otherwise>
            </c:choose>
            </p>
        </dd>
        </c:if>
        </sec:authorize>
        </div> <!--  .span6 -->
        <div class="span3" style="width: 209px; margin: 7px 8px 0px 20px;">
        <dl>
            <dt>Contact</dt>
            <dd>
                <ul class="unstyled">
                    <li><c:out value="${project.dataSpaceAgent.firstName}"/>&nbsp;<c:out value="${project.dataSpaceAgent.lastName}"/></li>
                    <li><c:out value="${project.dataSpaceAgent.institution.title}"/></li>
                    <li><a href="mailto:<c:out value="${project.dataSpaceAgent.email}"/>"><c:out value="${project.dataSpaceAgent.email}"/></a></li>
                </ul>
            </dd>
        </dl>
        </div> <!-- .span3 -->
        </div> <!-- .row -->

        <sec:authorize access="hasPermission(#project, 'manage')">
        <div class="row" style="margin-top: 11px; margin-bottom: 6px;">
            <c:forEach items="${roles}" var="role">
            <div class="span3" style="border-bottom: 1px solid #c7c7c7; font-weight: bold;">
                ${role.pluralTitle}
                <div class="help-popover" title="${role.pluralTitle}">
                    ${role.explanation}
                </div>
            </div>
            </c:forEach>
        </div>
        <div class="row">
            <c:forEach items="${roles}" var="role">
            <div class="span3">
                <p id="${role.identifier}ProjectUsersNone" style="margin: 0.3em 0; color: #999; display: none;">
                    No users with ${role.title} role
                </p>
                <ul id="${role.identifier}ProjectUsersList" class="icons" style="display: none;">
                </ul>
            </div>
            </c:forEach>
        </div>
        <form
            onsubmit="return false;"
            class="form-inline add-project-user-form"
            style="padding: 10px 12px;">
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

        <h2>Search Related Sites</h2>

        <p>Click the buttons below to find datasets on related websites.</p>

        <div id="searchButtons">
            <button id="searchTernButton" class="btn searchButton"
                title="Terrestrial Ecosystem Research Network (TERN)"
                ><img src="${pageContext.request.contextPath}/img/tern-logo.gif" /></button>
            <button id="searchAlaButton" class="btn searchButton"
                title="Atlas of Living Australia (ALA)"
                ><img src="${pageContext.request.contextPath}/img/ala-logo.png" /></button>
        </div>
        <c:set var="defaultSearchQuery" value="${not empty project.speciesScientificName ? project.speciesScientificName : project.speciesCommonName}"/>
        <form id="searchTernForm" class="searchForm form-horizontal form-bordered">
            <fieldset>
                <div class="legend">
                    <button type="button" class="close" onclick="$('.searchForm').fadeOut();">×</button>
                    Search TERN
                </div>
                <div class="control-group">
                    <label class="control-label" for="term">Term</label>
                    <div class="controls">
                        <input id="term" class="input-xxlarge" name="term" type="text" placeholder="e.g. ${defaultSearchQuery}" value="${defaultSearchQuery}"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="temporal">Years</label>
                    <div class="controls">
                        <input id="temporal" class="input-xxlarge" name="temporal" type="text" placeholder="e.g. 2000-2010" />
                    </div>
                </div>
                <div class="control-group" style="display: none">
                    <label class="control-label" for="g">Geometry</label>
                    <div class="controls">
                        <input id="g" class="input-xxlarge" name="g" type="text" />
                        <div class="help-inline">
                            <div class="help-popover" title="Geometry">
                                <p>Geometry expressed in Well-Known Text (WKT) format.</p>
                                <p>Example: POLYGON((138.50 -27.25,141.498 -21.32,146.8 -28.34,140.88 -30.63,138.5 -27.25))</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="b">Bounding box</label>
                    <div class="controls">
                        <input id="b" class="input-xxlarge" name="b" type="text" placeholder="e.g. -35.42,138.77,-37.89,130.51" />
                        <div class="help-inline">
                            <div class="help-popover" title="Bounding box">
                                <p>
                                    Geographical bounding box specifying limits for latitude/longitude
                                    in the format <i>north,west,south,east</i>.
                                </p>
                                <p>Example: -35.42,138.77,-37.89,130.51</p>
                            </div>
                        </div>
                    </div>
                </div>
                <input type="hidden" name="w" value="1" />
                <input type="hidden" name="count" value="10" />
                <input type="hidden" name="format" value="json" />
            </fieldset>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Search</button>
            </div>
            <div id="searchTernOutput" class="searchOutput">
            </div>
        </form>
        <form id="searchAlaForm" class="searchForm form-horizontal form-bordered">
            <fieldset>
                <div class="legend">
                    <button type="button" class="close" onclick="$('.searchForm').fadeOut();">×</button>
                    Search ALA
                </div>
                <div class="control-group">
                    <label class="control-label" for="q">Species</label>
                    <div class="controls">
                        <input id="q" class="input-xxlarge" name="q" type="text" placeholder="e.g. ${defaultSearchQuery}" value="${defaultSearchQuery}"/>
                    </div>
                </div>
                <input type="hidden" name="pageSize" value="10" />
                <input type="hidden" name="idxType" value="TAXON" />
            </fieldset>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Search</button>
            </div>
            <div id="searchAlaOutput" class="searchOutput">
            </div>
        </form>
    </jsp:body>
</tags:page>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm:ss"/>
<tags:page title="${dataFile.project.title}: Data File ${dataFile.userGivenFileName}">
    <jsp:attribute name="description">
        Data file ${dataFile.userGivenFileName} uploaded to the ${dataFile.project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            function pollDataFile(dataFile) {
                if (dataFile) {
                    $('#numPositionFixes').text(dataFile.numPositionFixes || '');
                    $('#status').text(dataFile.status || 'UNKNOWN');
                    $('#statusMessage').text(dataFile.statusMessage || '');
                    if ((dataFile.status == 'NEW') || (dataFile.status == 'PROCESSING')) {
                        $('#status')
                            .append(' ')
                            .append($('<img>').attr('src', '${pageContext.request.contextPath}/img/ui-anim_basic_16x16.gif'));
                        setTimeout(pollDataFile, 1000);
                    }
                }
                else {
                    $.get(
                        '${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles/${dataFile.id}',
                        function(dataFile) {
                            pollDataFile(dataFile);
                        },
                        'json'
                    );
                }
            }
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#dataFileActionsView').addClass('active');
                pollDataFile({
                    numPositionFixes: ${dataFileDetectionCount},
                    status: '${not empty dataFile.status ? dataFile.status : ""}',
                    statusMessage: '${not empty dataFile.statusMessage ? dataFile.statusMessage : ""}'
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${dataFile.project.id}">${dataFile.project.title}</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles">Data Files</a>
        &rsaquo; <span class="active">${dataFile.userGivenFileName}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${dataFile.project}"/>
        <sec:authorize access="hasPermission(#dataFile.project, 'write')">
        <div class="sidebar-actions">
            <div class="sidebar-actions-title">Manage Data File</div>
            <ul class="icons sidebar-actions-list">
                <li id="dataFileActionsView" class="view-file"><a href="${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles/${dataFile.id}">View data file</a></li>
                <li id="dataActionsCreateFile" class="create-file"><a href="${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles/new">Upload data file</a></li>
                <li class="view-files"><a href="${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles">View data files</a></li>
                <li class="delete-file"><a href="javascript:void(OzTrack.deleteEntity('${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles/${dataFile.id}', '${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles', 'Are you sure you want to delete this data file?'));">Delete data file</a></li>
            </ul>
        </div>
        </sec:authorize>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${dataFile.project.title}"/></h1>
        <h2>Data File</h2>

        <table class="entityTable">
        <tr>
            <th>File Provided:</th>
            <td><c:out value="${dataFile.userGivenFileName}"/></td>
        </tr>
        <tr>
            <th>Description:</th>
            <td><c:out value="${dataFile.fileDescription}"/></td>
        </tr>
        <tr>
            <th>Uploaded:</th>
            <td><fmt:formatDate pattern="${dateTimeFormatPattern}" value="${dataFile.createDate}"/> by <c:out value="${dataFile.createUser.fullName}"/></td>
        </tr>
        <tr>
            <th>Processing Status:</th>
            <td id="status"></td>
        </tr>
        <tr>
            <th>Processing Messages:</th>
            <td id="statusMessage"></td>
        </tr>
        <tr>
            <th>Detection Count:</th>
            <td id="numPositionFixes"></td>
        </tr>
        </table>
    </jsp:body>
</tags:page>

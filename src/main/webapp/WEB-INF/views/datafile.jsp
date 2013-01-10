<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<tags:page title="${dataFile.project.title}: Data File ${dataFile.userGivenFileName}">
    <jsp:attribute name="description">
        Data file ${dataFile.userGivenFileName} uploaded to the ${dataFile.project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuSearch').addClass('active');
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
                <li class="delete"><a href="javascript:void(deleteEntity('${pageContext.request.contextPath}/datafiles/${dataFile.id}', '${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles', 'Are you sure you want to delete this data file?'));">Delete data file</a></li>
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
            <th>Content Type:</th>
            <td><c:out value="${dataFile.contentType}"/></td>
        </tr>
        <tr>
            <th>Uploaded:</th>
            <td><fmt:formatDate pattern="${dateTimeFormatPattern}" value="${dataFile.createDate}"/> by <c:out value="${dataFile.createUser.fullName}"/></td>
        </tr>
        <tr>
            <th>Detection Count:</th>
            <td><c:out value="${dataFileDetectionCount}"/></td>
        </tr>
        <tr>
            <th>Processing Status:</th>
            <td>
                <c:out value="${dataFile.status}"/>
                <c:if test="${(dataFile.status == 'NEW') || (dataFile.status == 'PROCESSING')}">
                (<a href="javascript:void(0)" onclick="window.location.reload(true);">refresh</a>)
                </c:if>
            </td>
        </tr>
        <tr>
            <th>Processing Messages:</th>
            <td><c:out value="${dataFile.statusMessage}"/></td>
        </tr>
        </table>
    </jsp:body>
</tags:page>

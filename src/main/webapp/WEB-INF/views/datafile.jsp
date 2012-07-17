<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<tags:page title="${dataFile.project.title}: Data File Detail">
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
        &rsaquo; <a href="<c:url value="/projects/${dataFile.project.id}"/>">${dataFile.project.title}</a>
        &rsaquo; <a href="<c:url value="/projects/${dataFile.project.id}/datafiles"/>">Data Uploads</a>
        &rsaquo; <span class="active">Data File Detail</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${dataFile.project}"/>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${dataFile.project.title}"/></h1>
		<h2>Data File Detail</h2>
		
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
            <td><c:out value="${dataFile.detectionCount}"/></td>
        </tr>
		<tr>
            <th>Processing Status:</th>
		    <td><c:out value="${dataFile.status}"/></td>
		</tr>
		<tr>
            <th>Processing Messages:</th>
            <td><c:out value="${dataFile.statusMessage}"/></td>
        </tr>
		</table>

        <sec:authorize access="hasPermission(#dataFile.project, 'write')">
        <div class="actions">
        <h2>Manage Data File</h2>
        <ul class="actions">
            <li class="delete"><a href="javascript:void(deleteEntity('<c:url value="/datafiles/${dataFile.id}"/>', '<c:url value="/projects/${dataFile.project.id}/datafiles"/>', 'Are you sure you want to delete this data file?'));">Delete data file</a></li>
        </ul>
        </div>
        </sec:authorize>
    </jsp:body>
</tags:page>

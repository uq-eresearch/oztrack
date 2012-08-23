<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Animal Details">
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
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}/animals"/>">Animals</a>
        &rsaquo; <span class="active">${animal.animalName}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Animal Details</h2>
        <table class="entityTable">
            <tr>
                <th>Name:</th>
                <td>${animal.animalName}</td>
            </tr>
            <tr>
                <th>Description:</th>
                <td>${animal.animalDescription}</td>
            </tr>
            <tr>
                <th>Species:</th>
                <td>${animal.speciesName}</td>
            </tr>
            <c:if test="${not empty animal.projectAnimalId}">
            <tr>
                <th>Animal Id:</th>
                <td>${animal.projectAnimalId}</td>
            </tr>
            </c:if>
            <c:if test="${not empty animal.transmitterTypeCode}">
            <tr>
                <th>Transmitter Type Code:</th>
                <td>${animal.transmitterTypeCode}</td>
            </tr>
            </c:if>
            <c:if test="${not empty animal.transmitterId}">
            <tr>
                <th>Transmitter Id:</th>
                <td>${animal.transmitterId}</td>
            </tr>
            </c:if>
            <c:if test="${not empty animal.pingIntervalSeconds}">
            <tr>
                <th>Ping Interval Seconds:</th>
                <td>${animal.pingIntervalSeconds}</td>
            </tr>
            </c:if>
        </table>
        <sec:authorize access="hasPermission(#animal.project, 'write')">
        <div class="actions">
        <h2>Manage Animal</h2>
        <ul>
            <li class="edit"><a href="<c:url value="/animals/${animal.id}/edit"/>">Edit animal</a></li>
            <c:if test="${empty animal.positionFixes}">
            <li class="delete"><a href="javascript:void(deleteEntity('<c:url value="/animals/${animal.id}"/>', '<c:url value="/projects/${project.id}/animals"/>', 'Are you sure you want to delete this animal?'));">Delete animal</a></li>
            </c:if>
        </ul>
        </div>
        </sec:authorize>
    </jsp:body>
</tags:page>

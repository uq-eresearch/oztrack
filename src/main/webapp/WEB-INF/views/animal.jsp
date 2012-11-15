<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: '${animal.animalName}'">
    <jsp:attribute name="description">
        Animal '${animal.animalName}' in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuAnimals').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}/animals">Animals</a>
        &rsaquo; <span class="active">${animal.animalName}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Animal Details</h2>
        <table class="entityTable">
            <sec:authorize access="hasPermission(#animal.project, 'write')">
            <tr>
                <th>Animal ID:</th>
                <td>${animal.projectAnimalId}</td>
            </tr>
            </sec:authorize>
            <tr>
                <th>Name:</th>
                <td>${animal.animalName}</td>
            </tr>
            <tr>
                <th>Species:</th>
                <td>${animal.speciesName}</td>
            </tr>
            <tr>
                <th>Description:</th>
                <td>${animal.animalDescription}</td>
            </tr>
            <tr>
                <th>Colour:</th>
                <td><div style="width: 18px; height: 18px; background-color: ${animal.colour};"></div></td>
            </tr>
        </table>
        <sec:authorize access="hasPermission(#animal.project, 'write')">
        <c:if test="${not empty animal.createDescription}">
        <p style="color: #666;">
            ${animal.createDescription}
        </p>
        </c:if>
        </sec:authorize>
        <sec:authorize access="hasPermission(#animal.project, 'write')">
        <div class="actions">
        <h2>Manage Animal</h2>
        <ul class="icons">
            <li class="edit"><a href="${pageContext.request.contextPath}/animals/${animal.id}/edit">Edit animal</a></li>
            <c:if test="${empty animal.positionFixes}">
            <li class="delete"><a href="javascript:void(deleteEntity('${pageContext.request.contextPath}/animals/${animal.id}', '${pageContext.request.contextPath}/projects/${project.id}/animals', 'Are you sure you want to delete this animal?'));">Delete animal</a></li>
            </c:if>
        </ul>
        </div>
        </sec:authorize>
    </jsp:body>
</tags:page>

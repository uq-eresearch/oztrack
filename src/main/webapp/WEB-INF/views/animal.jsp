<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: '${animal.animalName}'">
    <jsp:attribute name="description">
        Animal '${animal.animalName}' in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
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
        <sec:authorize access="hasPermission(#animal.project, 'write')">
        <div class="sidebar-actions">
            <div class="sidebar-actions-title">Manage Animal</div>
            <ul class="icons sidebar-actions-list">
                <li class="edit-animal"><a href="${pageContext.request.contextPath}/animals/${animal.id}/edit">Edit animal</a></li>
                <c:if test="${empty animal.positionFixes}">
                <li class="delete-animal"><a href="javascript:void(deleteEntity('${pageContext.request.contextPath}/animals/${animal.id}', '${pageContext.request.contextPath}/projects/${project.id}/animals', 'Are you sure you want to delete this animal?'));">Delete animal</a></li>
                </c:if>
            </ul>
        </div>
        </sec:authorize>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Animal Details</h2>
        <table>
            <tr>
                <td style="padding: 5px 5px 0 0; vertical-align: top;">
                    <div style="width: 18px; height: 18px; background-color: ${animal.colour};"></div>
                </td>
                <td style="padding: 5px 5px 0 5px; vertical-align: top;">
                    <p>
                        <span style="font-weight: bold; color: #333;"><c:out value="${animal.animalName}"/></span>
                    </p>
                </td>
            </tr>
        </table>
        <c:if test="${not empty project.speciesCommonName}">
        <p>
            <c:out value="${project.speciesCommonName}"/>
            <c:if test="${not empty project.speciesScientificName}">
            (<i>${project.speciesScientificName}</i>)
            </c:if>
        </p>
        </c:if>
        <c:if test="${not empty animal.animalDescription}">
        <p>
            <c:out value="${animal.animalDescription}"/>
        </p>
        </c:if>
        <sec:authorize access="hasPermission(#animal.project, 'write')">
        <c:if test="${not empty animal.createDescription}">
        <p style="color: #666;">
            ${animal.createDescription}
        </p>
        </c:if>
        </sec:authorize>
    </jsp:body>
</tags:page>

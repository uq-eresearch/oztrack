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
                $('#animalActionsView').addClass('active');
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
        <tags:animal-actions animal="${animal}"/>
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
        <tags:search-results positionFixPage="${positionFixPage}" individualAnimal="true"/>
    </jsp:body>
</tags:page>

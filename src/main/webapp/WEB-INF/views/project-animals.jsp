<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Animals">
    <jsp:attribute name="description">
        Listing of animals tracked in the ${project.title} project.
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
        &rsaquo; <span class="active">Animals</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Animals</h2>
        <c:if test="${empty projectAnimalsList}">
        <p>
            There are currently no animals in this project.
        </p>
        <sec:authorize access="hasPermission(#project, 'write')">
        <p>
            Animals are created by <a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles/new">uploading a data file</a>.
        </p>
        </sec:authorize>
        </c:if>
        <table>
            <tbody>
            <c:forEach items="${projectAnimalsList}" var="animal">
                <tr>
                    <td style="padding: 5px 5px 0 5px; vertical-align: top;">
                        <div style="width: 18px; height: 18px; background-color: ${animal.colour};"></div>
                    </td>
                    <td style="padding: 5px 5px 0 5px; vertical-align: top;">
                        <p>
                            <a href="${pageContext.request.contextPath}/animals/${animal.id}" style="text-decoration: none; font-weight: bold; color: #333;"><c:out value="${animal.animalName}"/></a>
                            <c:if test="${not empty animal.speciesName}">
                            <span style="padding-left: 10px; font-style: italic;">
                                <c:out value="${animal.speciesName}"/>
                            </span>
                            </c:if>
                            <sec:authorize access="hasPermission(#project, 'write')">
                            <span style="padding-left: 10px; font-style: italic;">
                                <a href="${pageContext.request.contextPath}/animals/${animal.id}/edit"><img src="${pageContext.request.contextPath}/img/page_white_edit.png" /></a>
                                <c:if test="${empty animal.positionFixes}">
                                <a href="javascript:void(0);" onclick="deleteEntity(
                                    '${pageContext.request.contextPath}/animals/${animal.id}',
                                    '${pageContext.request.contextPath}/projects/${project.id}/animals',
                                    'Are you sure you want to delete this animal?'
                                    );"><img src="${pageContext.request.contextPath}/img/page_white_delete.png" /></a>
                                </c:if>
                            </span>
                            </sec:authorize>
                        </p>
                        <c:if test="${not empty animal.animalDescription}">
                        <p>
                            <c:out value="${animal.animalDescription}"/>
                        </p>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </jsp:body>
</tags:page>

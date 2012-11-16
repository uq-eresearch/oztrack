<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<div class="sidebarMenu">
    <ul>
        <li id="projectMenuDetails"><a href="${pageContext.request.contextPath}/projects/${project.id}">Project Details</a></li>
        <sec:authorize access="#project.global or hasPermission(#project, 'read')">
        <c:if test="${not empty project.dataFiles}">
        <li id="projectMenuAnalysis"><a href="${pageContext.request.contextPath}/projects/${project.id}/analysis">View Tracks</a></li>
        </c:if>
        <c:if test="${not empty project.animals}">
        <li id="projectMenuAnimals"><a href="${pageContext.request.contextPath}/projects/${project.id}/animals">View Animals</a></li>
        </c:if>
        <c:if test="${not empty project.dataFiles}">
        <li id="projectMenuSearch"><a href="${pageContext.request.contextPath}/projects/${project.id}/search">View Data</a></li>
        </c:if>
        </sec:authorize>
        <sec:authorize access="hasPermission(#project, 'write')">
        <li id="projectMenuUploads"><a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles">Add Data</a></li>
        <c:if test="${not empty project.dataFiles}">
        <li id="projectMenuCleanse"><a href="${pageContext.request.contextPath}/projects/${project.id}/cleanse">Edit Data</a></li>
        </c:if>
        </sec:authorize>
    </ul>
</div>

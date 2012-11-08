<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<div class="sidebarMenu">
    <ul>
        <li id="projectMenuDetails"><a href="<c:url value="/projects/${project.id}"/>">Project Details</a></li>
        <sec:authorize access="#project.global or hasPermission(#project, 'read')">
        <c:if test="${not empty project.dataFiles}">
        <li id="projectMenuAnalysis"><a href="<c:url value="/projects/${project.id}/analysis"/>">View Tracks</a></li>
        <li id="projectMenuAnimals"><a href="<c:url value="/projects/${project.id}/animals"/>">View Animals</a></li>
        <li id="projectMenuSearch"><a href="<c:url value="/projects/${project.id}/search"/>">View Data</a></li>
        </c:if>
        </sec:authorize>
        <sec:authorize access="hasPermission(#project, 'write')">
        <li id="projectMenuUploads"><a href="<c:url value="/projects/${project.id}/datafiles"/>">Add Data</a></li>
        <li id="projectMenuCleanse"><a href="<c:url value="/projects/${project.id}/cleanse"/>">Edit Data</a></li>
        </sec:authorize>
    </ul>
</div>

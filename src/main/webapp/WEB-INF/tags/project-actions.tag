<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<sec:authorize access="hasPermission(#project, 'write')">
<div class="sidebar-actions">
    <div class="sidebar-actions-title">Manage Project</div>
    <ul class="icons sidebar-actions-list">
        <li class="create"><a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles/new">Upload data file</a></li>
        <c:if test="${not empty project.dataFiles}">
        <li class="edit"><a href="${pageContext.request.contextPath}/projects/${project.id}/cleanse">Edit tracks</a></li>
        </c:if>
        <li class="edit"><a href="${pageContext.request.contextPath}/projects/${project.id}/edit">Edit project</a></li>
        <sec:authorize access="hasPermission(#project, 'manage')">
        <li class="publish"><a href="${pageContext.request.contextPath}/projects/${project.id}/publish">Publish project</a></li>
        <c:if test="${empty project.dataSpaceURI}">
        <li class="delete"><a href="javascript:void(deleteEntity('${pageContext.request.contextPath}/projects/${project.id}', '${pageContext.request.contextPath}/projects', 'Are you sure you want to delete this project?'));">Delete project</a></li>
        </c:if>
        </sec:authorize>
    </ul>
</div>
</sec:authorize>
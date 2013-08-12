<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<%@ attribute name="itemsOnly" type="java.lang.Boolean" required="false" %>
<c:if test="${itemsOnly == null || itemsOnly == false}">
<div class="sidebar-actions">
    <div class="sidebar-actions-title">Browse Project</div>
    <ul class="icons sidebar-actions-list">
</c:if>
        <li id="projectMenuDetails" class="view-project"><a href="${pageContext.request.contextPath}/projects/${project.id}">Project summary</a></li>
        <sec:authorize access="hasPermission(#project, 'read')">
        <c:if test="${not empty project.dataFiles}">
        <li id="projectMenuAnalysis" class="view-track"><a href="${pageContext.request.contextPath}/projects/${project.id}/analysis">Tracks and analysis</a></li>
        </c:if>
        <c:if test="${not empty project.animals}">
        <li id="projectMenuAnimals" class="view-animal"><a href="${pageContext.request.contextPath}/projects/${project.id}/animals">Animal details</a></li>
        </c:if>
        <c:if test="${not empty project.dataFiles}">
        <li id="projectMenuSearch" class="view-data"><a href="${pageContext.request.contextPath}/projects/${project.id}/search">Raw data</a></li>
        </c:if>
        </sec:authorize>
<c:if test="${itemsOnly == null || itemsOnly == false}">
    </ul>
</div>
</c:if>
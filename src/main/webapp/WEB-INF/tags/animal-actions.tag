<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="animal" type="org.oztrack.data.model.Animal" required="true" %>
<sec:authorize access="hasPermission(#animal.project, 'write')">
<div class="sidebar-actions">
    <div class="sidebar-actions-title">Manage Animal</div>
    <ul class="icons sidebar-actions-list">
        <li id="animalActionsView" class="view-animal"><a href="${pageContext.request.contextPath}/animals/${animal.id}">View animal</a></li>
        <li id="animalActionsEdit" class="edit-animal"><a href="${pageContext.request.contextPath}/animals/${animal.id}/edit">Edit animal</a></li>
        <c:if test="${empty animal.positionFixes}">
        <li class="delete-animal"><a href="javascript:void(OzTrack.deleteEntity('${pageContext.request.contextPath}/animals/${animal.id}', '${pageContext.request.contextPath}/projects/${animal.project.id}/animals', 'Are you sure you want to delete this animal?'));">Delete animal</a></li>
        </c:if>
    </ul>
</div>
</sec:authorize>
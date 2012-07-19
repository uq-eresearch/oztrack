<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="projects" type="java.util.List" required="true" %>
<%@ attribute name="adjective" type="java.lang.String" required="true" %>
<c:choose>
<c:when test="${empty projects}">
    <p>There are currently no ${adjective} project<c:if test="${fn:length(projects) != 1}">s</c:if> in OzTrack.</p>
</c:when>
<c:otherwise>
    <p>There are currently <c:out value="${fn:length(projects)}"/> ${adjective} project<c:if test="${fn:length(projects) != 1}">s</c:if> in OzTrack.</p>
    <table class="dataTable">
    <col style="width: 250px;" />
    <col style="width: 150px;" />
    <col style="width: 125px;" />
    <col style="width: 100px;" />
    <tr>
        <th>Title</th>
        <th>Spatial Coverage</th>
        <th>Project Type</th>
        <th>Created Date</th>
    </tr>
    <c:forEach items="${projects}" var="project">
    <tr>
        <td><a href="<c:url value="/projects/${project.id}"/>"><c:out value="${project.title}"/></a></td>
        <td><c:out value="${project.spatialCoverageDescr}"/></td>
        <td><c:out value="${project.projectType.displayName}"/></td>
        <td><fmt:formatDate value="${project.createDate}" type="date" dateStyle="long"/></td>
    </tr>
    </c:forEach>
    </table>
</c:otherwise>
</c:choose>
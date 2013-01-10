<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="projects" type="java.util.List" required="true" %>
<%@ attribute name="adjective" type="java.lang.String" required="true" %>
<c:choose>
<c:when test="${empty projects}">
    <p>
        There are currently no ${adjective} projects in OzTrack.
    </p>
</c:when>
<c:otherwise>
    <p>
        There are currently <c:out value="${fn:length(projects)}"/> ${adjective} projects in OzTrack.
    </p>
    <table class="table table-bordered table-condensed">
        <col style="width: 250px;" />
        <col style="width: 150px;" />
        <col style="width: 125px;" />
        <col style="width: 100px;" />
        <thead>
            <tr>
                <th>Title</th>
                <th>Spatial Coverage</th>
                <th>Created Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${projects}" var="project">
            <tr>
                <td><a href="${pageContext.request.contextPath}/projects/${project.id}"><c:out value="${project.title}"/></a></td>
                <td><c:out value="${project.spatialCoverageDescr}"/></td>
                <td><fmt:formatDate value="${project.createDate}" type="date" dateStyle="long"/></td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
</c:otherwise>
</c:choose>
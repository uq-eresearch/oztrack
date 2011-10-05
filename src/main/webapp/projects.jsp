<%@ include file="header.jsp" %>


<h1>Projects</h1>
Select a project to work with from the list below.

<h2>My Projects</h2>

<table class="dataTable">
   <tr>
    <th>Title</th>
    <th>Spatial Coverage</th>
    <th>Project Type</th>
    <th>Created Date</th>
    <th>User role</th>
   </tr>

<c:forEach items="${userProjectList}" var="project">
<tr>
    <td><a href="<c:url value="projectmap"><c:param name="project_id" value="${project.pk.project.id}"/></c:url>">
            <c:out value="${project.pk.project.title}"/></a></td>
    <td><c:out value="${project.pk.project.spatialCoverageDescr}"/></td>
    <td><c:out value="${project.pk.project.projectType.displayName}"/></td>
    <td><fmt:formatDate value="${project.pk.project.createDate}" type="date" dateStyle="long"/></td>
    <td><c:out value="${project.role}"/></td>
</tr>
</c:forEach>
</table>

<%@ include file="footer.jsp" %>

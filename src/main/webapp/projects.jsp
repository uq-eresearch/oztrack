<%@ include file="header.jsp" %>

<h1>Projects</h1>

<c:choose>
 <c:when test="${(empty userProjectList)}">
	<p>You have no projects to work with yet. You might like to <a href="<c:url value='projectadd'/>">add a project.</a></p>
 </c:when>
 <c:otherwise>
    <p>You have access to <c:out value="${fn:length(userProjectList)}"/> project(s). <br>
    Select a project to work with from the list below, or <a href="<c:url value='projectadd'/>">create a new project.</a></p>

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
	    <td><a href="<c:url value="projectdetail"><c:param name="project_id" value="${project.pk.project.id}"/></c:url>">
	            <c:out value="${project.pk.project.title}"/></a></td>
	    <td><c:out value="${project.pk.project.spatialCoverageDescr}"/></td>
	    <td><c:out value="${project.pk.project.projectType.displayName}"/></td>
	    <td><fmt:formatDate value="${project.pk.project.createDate}" type="date" dateStyle="long"/></td>
	    <td><c:out value="${project.role}"/></td>
	</tr>
	</c:forEach>
	</table>
 </c:otherwise>
</c:choose>
    

<%@ include file="footer.jsp" %>

<%@ include file="header.jsp" %>


<h1>Projects</h1>

<table class="infoTable">
    <tr>
        <th>Project Title</th>
        <th>Contact Name</th>
        <th>Role</th>
    </tr>
    <c:forEach items="${userProjectList}" var="thisUserProject">
        <tr>
            <td><a href="<c:url value="projectdetail"><c:param name="project_id" value="${thisUserProject.pk.project.id}"/></c:url>">
                <c:out value="${thisUserProject.pk.project.title}"/>
                </a></td>
            <td><c:out value="${thisUserProject.pk.project.contactName}"/></td>
            <td><c:out value="${thisUserProject.role}"/></td>
		</tr>
    </c:forEach>
</table>


<%@ include file="footer.jsp" %>

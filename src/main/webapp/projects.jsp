<%@ include file="header.jsp" %>


<h1>Projects</h1>
Select a project to work with from the list below.

<h2>My Projects</h2>
<c:forEach items="${userProjectList}" var="project">

       <table class="projectListTableHeader">
       <tr>

          <td><a href="<c:url value="projectdetail"><c:param name="project_id" value="${project.pk.project.id}"/></c:url>">
                <c:out value="${project.pk.project.title}"/>
                </a></td>
         <td><c:out value="${project.pk.project.spatialCoverageDescr}"/></td>
         <td><b>Type:</b><br><c:out value="${project.pk.project.projectType.displayName}"/></td>
         <td><b>Created:</b><br><fmt:formatDate value="${project.pk.project.createDate}" type="both" timeStyle="long" dateStyle="long"/></td>
         <td><b>Role: </b><br><c:out value="${project.role}"/></td>
        </tr>
        </table>

</c:forEach>

<h2>Globally Available Projects</h2>

<%@ include file="footer.jsp" %>

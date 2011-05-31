<%@ include file="header.jsp" %>


<h1>My Projects</h1>


<c:forEach items="${userProjectList}" var="project">

       <table class="projectListTableHeader">
       <tr>

          <td><a href="<c:url value="projectdetail"><c:param name="project_id" value="${project.pk.project.id}"/></c:url>">
                <c:out value="${project.pk.project.title}"/>
                </a></td>
         <td><c:out value="${project.pk.project.spatialCoverageDescr}"/></td>
         <td><b>Created:</b> 01/05/2011</td>
         <td><b>Role: </b><c:out value="${project.role}"/></td>
        </tr>
        </table>

</c:forEach>

<%@ include file="footer.jsp" %>

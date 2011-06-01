<%@ include file="header.jsp" %>


<h1>Data Files</h1>

<p><a class="oztrackButton" href="<c:url value="datafileadd"><c:param name="project_id" value="${project.id}"/></c:url>">Add a Datafile</a>
</p>

<p><c:out value="${errorStr}"/></p>

<table class="dataTable">

    <tr>
        <th>File Name</th>
        <th>Description</th>
        <th>File Status</th>
    </tr>

    <c:forEach items="${project.dataFiles}" var="dataFile">
        <tr>
            <td><c:out value="${dataFile.userGivenFileName}"/></td>
            <td>
                <c:choose>
                 <c:when test="${dataFile.status=='NEW'}">
                    <c:out value="${dataFile.fileDescription}"/>
                 </c:when>
                 <c:otherwise>
                    <a href="<c:url value="datafiledetail"><c:param name="datafile_id" value="${dataFile.id}"/></c:url>"><c:out value="${dataFile.fileDescription}"/></a>
                 </c:otherwise>
                </c:choose>
            </td>
            <td><c:out value="${dataFile.status}"/></td>
		</tr>
    </c:forEach>
</table>




<%@ include file="footer.jsp" %>
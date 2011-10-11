<%@ include file="header.jsp" %>
<script type="text/javascript">projectPage = true;</script>


<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Data Uploads</h2>

<p><a class="oztrackButton" href="<c:url value="datafileadd"><c:param name="project_id" value="${project.id}"/></c:url>">Add a Datafile</a>
</p>

<p><c:out value="${errorStr}"/></p>
<c:out value="${now}"/>

<table class="dataTable">

    <tr>
        <th>File Name</th>
        <th>Detection Date Range</th>
        <th>Upload Date</th>
        <th>File Status</th>
    </tr>

    <c:forEach items="${project.dataFiles}" var="dataFile">
        <tr>
            <td><a href="<c:url value="datafiledetail"><c:param name="datafile_id" value="${dataFile.id}"/></c:url>"><c:out value="${dataFile.userGivenFileName}"/></a></td>
            <td><fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.firstDetectionDate}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.lastDetectionDate}"/></td>
            <td><fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.createDate}"/>
            <td><c:out value="${dataFile.status}"/></td>
		</tr>
    </c:forEach>
</table>


<%@ include file="footer.jsp" %>
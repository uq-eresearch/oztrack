<%@ include file="header.jsp" %>
<script type="text/javascript">projectPage = true;</script>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Data File Detail</h2>

<table class="projectListTable">



<tr><td><b>File Provided:</b></td><td><c:out value="${dataFile.userGivenFileName}"/></td></tr>
<tr><td><b>Description:</b></td><td><c:out value="${dataFile.fileDescription}"/></td></tr>
<tr><td><b>Content Type:</b></td><td><c:out value="${dataFile.contentType}"/></td></tr>
<tr><td><b>Uploaded:</b></td><td><fmt:formatDate value="${dataFile.createDate}" type="both" timeStyle="long" dateStyle="long"/> by <c:out value="${dataFile.createUser.fullName}"/></td></tr>
<tr><td><b>Observation Count:</b></td><td><c:out value="${dataFile.numberDetections}"/></td></tr>
<tr><td><b>File Processing Status:</b></td>
    <td>
        <c:out value="${dataFile.status}"/>
        <c:choose>
             <c:when test="${dataFile.status=='FAILED'}">
                &nbsp;&nbsp;<a href="<c:url value="datafileadd"><c:param name="datafile_id" value="${dataFile.id}"/><c:param name="project_id" value="${dataFile.project.id}"/></c:url>">Retry</a>
             </c:when>
        </c:choose>
    </td>
</tr>

<tr><td><b>File Processing Messages:</b></td><td><c:out value="${dataFile.statusMessage}"/></td></tr>

</table>


<table>
<c:forEach items="${rawAcousticDetectionsList}" var="detection">
        <tr>
            <td><c:out value="${detection.datetime}"/></td>
            <td><c:out value="${detection.animalid}"/></td>
            <td><c:out value="${detection.sensor1}"/></td>
            <td><c:out value="${detection.units1}"/></td>
            <td><c:out value="${detection.receiverid}"/></td>
		</tr>
</c:forEach>
</table>

</center>

<%@ include file="footer.jsp" %>
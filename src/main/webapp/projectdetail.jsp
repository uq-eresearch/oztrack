<%@ include file="header.jsp" %>

<h1>Project Details</h1>


<table class="infoTable">

<tr><td>Title:</td><td id="projectTitle"><c:out value="${project.title}"/></td></tr>
<tr><td>Description:</td><td><c:out value="${project.description}"/></td></tr>
<tr><td>Contact Details:</td><td><c:out value="${project.contactName}"/> <c:out value="${project.contactUrl}"/></td></tr>
<tr><td>Custodian Details:</td><td><c:out value="${project.custodianName}"/>,<c:out value="${project.custodianUrl}"/></td></tr>
<tr><td>Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
<tr><td>Temporal Coverage:</td><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
<tr><td>Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
</table>

<h1>Data Files</h1>

<p><a href="<c:url value="datafileadd"><c:param name="project_id" value="${project.id}"/></c:url>">Add a Datafile</a>
</p>

<p><c:out value="${errorStr}"/></p>

<table class="infoTable">

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
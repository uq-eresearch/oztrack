<%@ include file="header.jsp" %>


<h1>Acoustic</h1>

<div class="dataTableDiv">

<div class="dataTableNav">
<c:out value="${offset+1}"/> to <c:out value="${offset+nbrObjectsThisPage}"/> of <c:out value="${totalCount}"/> results.
<br>

<c:choose>
 <c:when test="${offset > 0}">
    <a href="<c:url value="searchacoustic"><c:param name="offset" value="${0}"/></c:url>">&lt;&lt;</a>
    &nbsp;&nbsp;
    <a href="<c:url value="searchacoustic"><c:param name="offset" value="${offset-nbrObjectsPerPage}"/></c:url>">&lt;</a>
 </c:when>
 <c:otherwise>&lt;&lt;&nbsp;&nbsp;&lt;</c:otherwise>
</c:choose>
&nbsp;&nbsp;

<c:choose>
 <c:when test="${offset < totalCount - (totalCount % nbrObjectsPerPage)}">
    <a href="<c:url value="searchacoustic"><c:param name="offset" value="${offset+nbrObjectsThisPage}"/></c:url>">&gt;</a>
    &nbsp;&nbsp;
    <a href="<c:url value="searchacoustic"><c:param name="offset" value="${totalCount - (totalCount % nbrObjectsPerPage)}"/></c:url>">&gt;&gt;</a>
 </c:when>
 <c:otherwise>&gt;&nbsp;&nbsp;&gt;&gt;</c:otherwise>
</c:choose>

</div>

<table class="dataTable">

<tr>
    <th>Date/Time</th>
    <th>Animal</th>
    <th>Receiver</th>
    <th>Sensor 1</th>
    <th>Units 1</th>
    <th>Sensor 2</th>
    <th>Units 2</th>
    <th>DataFile Upload</th>
</tr>
<c:forEach items="${acousticDetectionsList}" var="detection">
<tr>
    <td><c:out value="${detection.detectionTime}"/></td>
    <td><c:out value="${detection.animal.projectAnimalId}"/></td>
    <td><c:out value="${detection.receiverDeployment.originalId}"/></td>
    <td><c:out value="${detection.sensor1Value}"/></td>
    <td><c:out value="${detection.sensor1Units}"/></td>
    <td><c:out value="${detection.sensor2Value}"/></td>
    <td><c:out value="${detection.sensor2Units}"/></td>
    <td><c:out value="${detection.dataFile.uploadDate}"/></td>
</tr>
</c:forEach>
</table>
</div>
<br>

<%@ include file="footer.jsp" %>

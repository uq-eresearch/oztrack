<%@ include file="header.jsp" %>

<h1><br/>Data File Detail</h1><br/>

<table class="infoTable">

<tr>
<td>File Provided:</td>
<td><c:out value="${dataFile.userGivenFileName}"/></td>
</tr>

<tr>
<td>Description:</td>
<td><c:out value="${dataFile.fileDescription}"/></td>
</tr>

<tr>
<td>Content Type:</td>
<td><c:out value="${dataFile.contentType}"/></td>
</tr>

<tr>
<td>Uploaded :</td>
<td><c:out value="${dataFile.uploadDate}"/> by <c:out value="${dataFile.uploadUser}"/></td>
</tr>


<tr>
<td>Number Detections:</td>
<td><c:out value="${dataFile.numberRawDetections}"/></td>
</tr>

<tr>
<td>File Processing Status:</td>
<td><c:out value="${dataFile.status}"/></td>
</tr>

<tr>
<td>File Processing Messages:</td>
<td><c:out value="${dataFile.statusMessage}"/></td>
</tr>

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
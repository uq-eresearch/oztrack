<%@ include file="header.jsp" %>



<div class="accordianHead"><a href="#">Section1</a></div>
<div class="accordianBody">Section1Content</div>

<br>
<br>

<form:form commandName="searchQuery" method="POST" name="searchQuery">

<span class="formHeader">Search</span>

    <div>
    <label for="projectAnimalId">Animal Id:</label>
    <form:input path="projectAnimalId" id="projectAnimalId"/>
    <form:errors path="projectAnimalId" cssClass="formErrors"/>
    </div>

    <div>
    <label for="receiverOriginalId">Receiver Id:</label>
    <form:input path="receiverOriginalId" id="receiverOriginalId"/>
    <form:errors path="receiverOriginalId" cssClass="formErrors"/>
    </div>

    <div>
    <label for="fromDate">Date From:</label>
    <form:input path="fromDate" id="fromDatepicker"/>
    <form:errors path="fromDate" cssClass="formErrors"/>
    </div>

    <div>
    <label for="toDate">Date To:</label>
    <form:input path="toDate" id="toDatepicker"/>
    <form:errors path="toDate" cssClass="formErrors"/>
    </div>



    <div align="center"><input type="submit" value="Search"/></div>




</form:form>

<p><c:out value="${sql}"/></p>

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

<%@ include file="footer.jsp" %>

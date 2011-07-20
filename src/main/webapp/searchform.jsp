<%@ include file="header.jsp" %>


<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Search Data</h2>

<form:form commandName="searchQuery" method="POST" name="searchQuery">


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

    <div>
    <label for="sortField">Sort by:</label>
    <form:select path="sortField">
        <form:option value="Animal"/>
        <form:option value="Receiver"/>
        <form:option value="Detection Time"/>
    </form:select>
    </div>

    <div>

    <label for="radiobuttonTable">Output Type:</label>
    <br> <br>
    <form:radiobutton cssClass="radiobutton" id="radiobuttonDiv0" path="outputType" value="raw"/>Raw Data<br>
    <form:radiobutton cssClass="radiobutton" id="radiobuttonDiv1" path="outputType" value="events"/>Extract Events<br>
    <form:radiobutton cssClass="radiobutton" id="radiobuttonDiv2" path="outputType" value="profile"/>Hourly Profile<br>
    <form:radiobutton cssClass="radiobutton" id="radiobuttonDiv3" path="outputType" value="kml"/>Google Earth KML<br>
    </div>


    <div><input type="submit" value="Search"/></div>

</form:form>

<c:if test="${acousticDetectionsList != null}">

    <br>
    <p align="center"><a href="#">Paginate Functionality</a> | <a class="oztrackButton" href="#">Export to File</a>
    </p><br>

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
        <td><fmt:formatDate value="${detection.detectionTime}" type="both" pattern="dd-MM-yyyy H:m:s"/></td>
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
</c:if>

<%@ include file="footer.jsp" %>

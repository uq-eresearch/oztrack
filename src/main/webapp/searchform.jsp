<%@ include file="header.jsp" %>


<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Search Project Data</h2>

animalList
<c:forEach items="${animalsNotInSearchList}" var="animal1">
    <c:out value="${animal1.projectAnimalId} - ${animal1.animalName}"/>
</c:forEach>


<br>searchQuery animalList
<c:forEach items="${searchQuery.animalList}" var="animal2">
    <c:out value="${animal2.projectAnimalId} - ${animal2.animalName}"/>
</c:forEach>


<form:form commandName="searchQuery" method="POST" name="searchQuery">


    <div>

    <label for="fromDate">Date From:</label>
    <form:input path="fromDate" id="fromDatepicker" cssClass="shortInputBox"/>

    <label for="toDate" class="shortInputLabel">Date To:</label>
    <form:input path="toDate" id="toDatepicker" cssClass="shortInputBox"/>

    </div>


    <div>
    <label>Animal:</label>
    <table><tr>
    <td>
        <select id="animalListAll" multiple="true" class="shortInputBox">
            <c:forEach items="${animalsNotInSearchList}" var="animal1">
                <c:set var="label" value="${animal1.projectAnimalId} - ${animal1.animalName}"/>
                <option value="${animal1.id}" label="${label}"></option>
            </c:forEach>
        </select>
    </td>
    <td id="addRemoveLinks">
        <a href="#" id="selectAdd">Add &gt;</a> <br><br>
        <a href="#" id="selectRemove">&lt; Remove</a>
    </td>
    <td>
        <form:select id="animalListSelect" path="animalList" multiple="true" cssClass="shortInputBox">
            <c:forEach items="${searchQuery.animalList}" var="animal2">
                <c:set var="label" value="${animal2.projectAnimalId} - ${animal2.animalName}"/>
                <option value="${animal2.id}" label="${label}"/>
            </c:forEach>
        </form:select>
    </td></tr></table>
    </div>



    <c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}">
        <div>
        <label for="receiverOriginalId">Receiver Id:</label>
        <form:input path="receiverOriginalId" id="receiverOriginalId"/>
        <form:errors path="receiverOriginalId" cssClass="formErrors"/>
        </div>
    </c:if>


    <div>
    <label for="sortField">Sort by:</label>
    <form:select path="sortField">
        <form:option value="Animal"/>
         <c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}"><form:option value="Receiver"/> </c:if>
        <form:option value="Detection Time"/>
    </form:select>
    </div>

    <div align="center"><input type="submit"  value="Search"/></div>

</form:form>


<div class="dataTableNav">
<div style="float:left;"><b>
Displaying <c:out value="${offset+1}"/> to <c:out value="${offset+nbrObjectsThisPage}"/> of <c:out value="${totalCount}"/> records.
</b></div>
<div style="float:right">

<c:choose>
 <c:when test="${offset > 0}">
    <a href="<c:url value="searchform"><c:param name="offset" value="${0}"/></c:url>">&lt;&lt;</a>
    &nbsp;&nbsp;
    <a href="<c:url value="searchform"><c:param name="offset" value="${offset-nbrObjectsPerPage}"/></c:url>">&lt;</a>
 </c:when>
 <c:otherwise>&lt;&lt;&nbsp;&nbsp;&lt;</c:otherwise>
</c:choose>
&nbsp;&nbsp;

<c:choose>
 <c:when test="${offset < totalCount - (totalCount % nbrObjectsPerPage)}">
    <a href="<c:url value="searchform"><c:param name="offset" value="${offset+nbrObjectsThisPage}"/></c:url>">&gt;</a>
    &nbsp;&nbsp;
    <a href="<c:url value="searchform"><c:param name="offset" value="${totalCount - (totalCount % nbrObjectsPerPage)}"/></c:url>">&gt;&gt;</a>
 </c:when>
 <c:otherwise>&gt;&nbsp;&nbsp;&gt;&gt;</c:otherwise>
</c:choose>
</div>
</div>
<br>

<c:if test="${acousticDetectionsList != null}">

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
        <td><c:out value="${detection.animal.projectAnimalId}"/> </td>
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

<c:if test="${positionFixList != null}">

    <table class="dataTable">
    <tr>
        <th>Date/Time</th>
        <th>Animal Id</th>
        <th>Animal Name</th>
        <th>Latitude</th>
        <th>Longitude</th>
      <!--
        <th>Sensor 1</th>
        <th>Units 1</th>
        <th>Sensor 2</th>
        <th>Units 2</th>
      -->
        <th>DataFile Upload</th>
    </tr>
    <c:forEach items="${positionFixList}" var="detection">
    <tr>
        <td><fmt:formatDate value="${detection.detectionTime}" type="both" pattern="dd-MM-yyyy H:m:s"/></td>
        <td><c:out value="${detection.animal.projectAnimalId}"/></td>
        <td><a href="<c:url value="animalform"><c:param name="animal_id" value="${detection.animal.id}"/></c:url>">
                <c:out value="${detection.animal.animalName}"/></a></td>
        <td><c:out value="${detection.latitude}"/></td>
        <td><c:out value="${detection.longitude}"/></td>
        <!--
        <td><c:out value="${detection.sensor1Value}"/></td>
        <td><c:out value="${detection.sensor1Units}"/></td>
        <td><c:out value="${detection.sensor2Value}"/></td>
        <td><c:out value="${detection.sensor2Units}"/></td>
        -->
        <td><a href="<c:url value="datafiledetail"><c:param name="datafile_id" value="${detection.dataFile.id}"/></c:url>">
            <c:out value="${detection.dataFile.createDate}"/></a></td>
    </tr>
    </c:forEach>
    </table>

 </c:if>
<%@ include file="footer.jsp" %>

<%@ include file="header.jsp" %>

<h2>Animals</h2>
<p><b>Project Title: </b><span id="projectTitle"><c:out value="${project.title}"/></span></p>

<table class="infoTable">

    <tr>
        <th>Animal ID</th>
        <th>Name</th>
        <th>Description</th>
        <th>Species</th>
        <th>Transmitter Type</th>
        <th>Transmitter ID</th>
        <th>Sensor Transmitter ID</th>
       <!-- <th>Transmitter Deploy Date</th> -->
        <th>Ping Interval (seconds)</th>
        <th> </th>
    </tr>

    <c:forEach items="${animalList}" var="animal">
        <tr>
            <td><c:out value="${animal.projectAnimalId}"/></td>
            <td><c:out value="${animal.animalName}"/></td>
            <td><c:out value="${animal.animalDescription}"/></td>
            <td><c:out value="${animal.speciesName}"/></td>
            <td><c:out value="${animal.transmitterTypeCode}"/></td>
            <td><c:out value="${animal.transmitterId}"/></td>
            <td><c:out value="${animal.sensorTransmitterId}"/></td>

            <!--<td><c:out value="${animal.transmitterDeployDate}"/></td>-->

            <td><c:out value="${animal.pingIntervalSeconds}"/></td>
            <td>
		        <a href="<c:url value="animalform"><c:param name="animal_id" value="${animal.id}"/></c:url>">
                Edit
                </a>
            </td>
		</tr>
    </c:forEach>
</table>


<%@ include file="footer.jsp" %>
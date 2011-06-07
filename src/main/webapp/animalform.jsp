<%@ include file="header.jsp" %>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Update Animal Details</h2>

<form:form commandName="animal" method="POST" name="animal">



<div>
<label for="projectAnimalId">Animal Id:</label>
<form:input path="projectAnimalId" id="projectAnimalId"/>
<form:errors path="projectAnimalId" cssClass="formErrors"/>
</div>

<div>
<label for="animalName">Name:</label>
<form:input path="animalName" id="animalName"/>
<form:errors path="animalName" cssClass="formErrors"/>
</div>

<div>
<label for="animalDescription">Description:</label>
<form:input path="animalDescription" id="animalDescription"/>
<form:errors path="animalDescription" cssClass="formErrors"/>
</div>

<div>
<label for="speciesName">Species:</label>
<form:input path="speciesName" id="speciesName"/>
<form:errors path="speciesName" cssClass="formErrors"/>
</div>

<div>
<label for="transmitterTypeCode">Transmitter Type Code:</label>
<form:input path="transmitterTypeCode" id="transmitterTypeCode"/>
<form:errors path="transmitterTypeCode" cssClass="formErrors"/>
</div>

<div>
<label for="transmitterId">transmitterId:</label>
<form:input path="transmitterId" id="transmitterId"/>
<form:errors path="transmitterId" cssClass="formErrors"/>
</div>

<div>
<label for="sensorTransmitterId">sensorTransmitterId:</label>
<form:input path="sensorTransmitterId" id="sensorTransmitterId"/>
<form:errors path="sensorTransmitterId" cssClass="formErrors"/>
</div>

<div>
<label for="pingIntervalSeconds">pingIntervalSeconds:</label>
<form:input path="pingIntervalSeconds" id="pingIntervalSeconds"/>
<form:errors path="pingIntervalSeconds" cssClass="formErrors"/>
</div>

<!--
<div>
<label for="transmitterDeployDate">transmitterDeployDate:</label>
<form:input path="transmitterDeployDate" id="transmitterDeployDate"/>
<form:errors path="transmitterDeployDate" cssClass="formErrors"/>
</div>
-->

<br>
<div align="center"><input type="submit" value="Update"/></div>

</form:form>








<%@ include file="footer.jsp" %>

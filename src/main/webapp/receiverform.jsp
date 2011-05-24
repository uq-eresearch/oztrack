<%@ include file="header.jsp" %>

<form:form commandName="receiverDeployment" method="POST" name="receiverDeployment">

<span class="formHeader">Update Receiver Details</span>

<div>
<label for="originalId">Receiver Id:</label>
<form:input path="originalId" id="originalId"/>
<form:errors path="originalId" cssClass="formErrors"/>
</div>

<div>
<label for="receiverName">Name:</label>
<form:input path="receiverName" id="receiverName"/>
<form:errors path="receiverName" cssClass="formErrors"/>
</div>

<div>
<label for="receiverDescription">Description:</label>
<form:input path="receiverDescription" id="receiverDescription"/>
<form:errors path="receiverDescription" cssClass="formErrors"/>
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
<div align="center"><input type="submit" value="Update this animal's details"/></div>

</form:form>








<%@ include file="footer.jsp" %>

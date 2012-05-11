<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${animal.project.title}: Update Animal Details">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
	        projectPage = true;
            $(document).ready(function() {
            	$('#navTrack').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a id="homeUrl" href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="/projects">Animal Tracking</a>
        &rsaquo; <a href="/projectdetail?project_id=${animal.project.id}">${animal.project.title}</a>
        &rsaquo; <a href="/projectanimals?project_id=${animal.project.id}">Animals</a>
        &rsaquo; <span class="aCrumb">Edit</span> 
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${animal.project.title}"/></h1>
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
			<br>
			<div align="center"><input type="submit" value="Update"/></div>
		</form:form>
    </jsp:body>
</tags:page>
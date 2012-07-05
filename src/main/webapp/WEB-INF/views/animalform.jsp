<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Update Animal Details">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
            	$('#navTrack').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}/animals"/>">Animals</a>
        &rsaquo; <a href="<c:url value="/animals/${animal.id}"/>">${animal.animalName}</a>
        &rsaquo; <span class="active">Edit</span> 
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		<h2>Update Animal Details</h2>
		<form:form action="/animals/${animal.id}" commandName="animal" method="PUT" name="animal">
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
				<label for="pingIntervalSeconds">pingIntervalSeconds:</label>
				<form:input path="pingIntervalSeconds" id="pingIntervalSeconds"/>
				<form:errors path="pingIntervalSeconds" cssClass="formErrors"/>
			</div>
			<br>
			<div align="center">
                <a href="<c:url value="/animals/${animal.id}"/>">Cancel</a>
                &nbsp;
                &nbsp;
                <input type="submit" value="Update"/>
            </div>
		</form:form>
    </jsp:body>
</tags:page>

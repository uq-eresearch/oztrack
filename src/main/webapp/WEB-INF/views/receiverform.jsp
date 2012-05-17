<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Update Receiver Details">
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
        &rsaquo; <a href="<c:url value="/projectdetail?id=${project.id}"/>">${project.title}</a>
        &rsaquo; <a href="<c:url value="/projectreceivers?project_id=${project.id}"/>">Receivers</a>
        &rsaquo; <span class="active">Update Receiver Details</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		<h2>Update Receiver Details</h2>
		
		<form:form commandName="receiverDeployment" method="POST" name="receiverDeployment">
		
		
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
		
		
		<br>
		<div align="center"><input type="submit" value="Update"/></div>
		
		</form:form>
    </jsp:body>
</tags:page>

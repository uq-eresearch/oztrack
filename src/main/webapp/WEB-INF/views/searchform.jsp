<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="dd/MM/yyyy"/>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<tags:page title="${project.title}: View Raw Data">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#fromDate').datepicker();
                $('#toDate').datepicker();
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projectdetail?id=${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">View Raw Data</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		<h2>Search Project Data</h2>
		
		<form:form commandName="searchQuery" method="POST" name="searchQuery">
		    <div>
			    <label for="fromDate">Date From:</label>
			    <form:input path="fromDate" id="fromDate" cssClass="shortInputBox"/>
			    <label for="toDate" class="shortInputLabel">Date To:</label>
			    <form:input path="toDate" id="toDate" cssClass="shortInputBox"/>
		    </div>
		    <div>
			    <label>Animal:</label>
			    <form:select id="animalListSelect" path="animalList" items="${projectAnimalsList}" itemLabel="animalName" itemValue="id" multiple="true" cssClass="shortInputBox"/>
			    <form:errors path="animalList"/>
		    </div>
		    <div>
			    <label for="sortField">Sort by:</label>
			    <form:select path="sortField">
			        <form:option value="Animal"/>
			         <c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}"><form:option value="Receiver"/> </c:if>
			        <form:option value="Detection Time"/>
			    </form:select>
		    </div>
		    <div align="center">
		        <input type="submit"  value="Search"/>
		    </div>
		</form:form>
		
		
		<div class="dataTableNav">
		<div style="float:left;">
		    <b>Displaying <c:out value="${offset+1}"/> to <c:out value="${offset+nbrObjectsThisPage}"/> of <c:out value="${totalCount}"/> records.</b>
		</div>
		<div style="float:right">
		    <c:choose>
		     <c:when test="${offset > 0}">
		        <a href="<c:url value="/searchform">
		            <c:param name="project_id" value="${searchQuery.project.id}"/>
		            <c:param name="offset" value="${0}"/>
		        </c:url>">&lt;&lt;</a>
		        &nbsp;&nbsp;
		        <a href="<c:url value="/searchform">
		            <c:param name="project_id" value="${searchQuery.project.id}"/>
		            <c:param name="offset" value="${offset-nbrObjectsPerPage}"/>
		        </c:url>">&lt;</a>
		     </c:when>
		     <c:otherwise>&lt;&lt;&nbsp;&nbsp;&lt;</c:otherwise>
		    </c:choose>
		    &nbsp;&nbsp;
		    <c:choose>
		     <c:when test="${offset < totalCount - (totalCount % nbrObjectsPerPage)}">
		        <a href="<c:url value="/searchform">
		            <c:param name="project_id" value="${searchQuery.project.id}"/>
		            <c:param name="offset" value="${offset+nbrObjectsThisPage}"/>
		        </c:url>">&gt;</a>
		        &nbsp;&nbsp;
		        <a href="<c:url value="/searchform">
		            <c:param name="project_id" value="${searchQuery.project.id}"/>
		            <c:param name="offset" value="${totalCount - (totalCount % nbrObjectsPerPage)}"/>
		        </c:url>">&gt;&gt;</a>
		     </c:when>
		     <c:otherwise>&gt;&nbsp;&nbsp;&gt;&gt;</c:otherwise>
		    </c:choose>
		    <a href="<c:url value="/export">
		        <c:param name="project_id" value="${searchQuery.project.id}"/>
		    </c:url>">Export</a>
		</div>
		</div>
		
		<br>
		
		<c:if test="${positionFixList != null}">
		
		    <table class="dataTable">
		    <tr>
		        <th>Date/Time</th>
		        <th>Animal Id</th>
		        <th>Animal Name</th>
		        <th>Latitude</th>
		        <th>Longitude</th>
		        <th>DataFile Upload</th>
		    </tr>
		    <c:forEach items="${positionFixList}" var="detection">
		    <tr>
		        <td><fmt:formatDate pattern="${dateTimeFormatPattern}" value="${detection.detectionTime}"/></td>
		        <td><c:out value="${detection.animal.projectAnimalId}"/></td>
		        <td><a href="<c:url value="/animalform"><c:param name="animal_id" value="${detection.animal.id}"/></c:url>">
		                <c:out value="${detection.animal.animalName}"/></a></td>
		        <td><c:out value="${detection.latitude}"/></td>
		        <td><c:out value="${detection.longitude}"/></td>
		        <td>
		            <a href="<c:url value="/datafiledetail">
			            <c:param name="project_id" value="${searchQuery.project.id}"/>
			            <c:param name="datafile_id" value="${detection.dataFile.id}"/>
		            </c:url>">
		           <fmt:formatDate pattern="${dateFormatPattern}" value="${detection.dataFile.createDate}"/></a>
		        </td>
		    </tr>
		    </c:forEach>
		    </table>
		
		 </c:if>
    </jsp:body>
</tags:page>

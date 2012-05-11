<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Receivers">
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
        &rsaquo; <a href="/projectdetail?project_id=${project.id}">${project.title}</a>
        &rsaquo; <span class="aCrumb">Receivers</span>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		<h2>Receivers</h2>
		
		<table class="dataTable">
		
		    <tr>
		        <th>Receiver ID</th>
		        <th>Name</th>
		        <th>Description</th>
		        <th>Deployment Date</th>
		        <th>Retrieval Date</th>
		        <th>Latitude</th>
		        <th>Longitude</th>
		        <th> </th>
		    </tr>
		
		    <c:forEach items="${receiverList}" var="receiver">
		        <tr>
		           <td><c:out value="${receiver.originalId}"/></td>
		            <td><c:out value="${receiver.receiverName}"/></td>
		            <td><c:out value="${receiver.receiverDescription}"/></td>
		            <td><c:out value="${receiver.deploymentDate}"/></td>
		            <td><c:out value="${receiver.retrievalDate}"/></td>
		            <td><c:out value="${receiver.receiverLocation.latitude}"/></td>
		            <td><c:out value="${receiver.receiverLocation.longitude}"/></td>
		            <td>
				        <!--
				            <a href="<c:url value="receiverform"><c:param name="receiver_id" value="${receiver.id}"/></c:url>">
		                -->
		                <a href="#">
		                Edit
		                </a>
		            </td>
				</tr>
		    </c:forEach>
		</table>
    </jsp:body>
</tags:page>
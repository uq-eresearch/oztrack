<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<tags:page title="${project.title}: Data File Detail">
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
        &rsaquo; <a href="/projectdetail?id=${project.id}">${project.title}</a>
        &rsaquo; <a href="/datafiles?project_id=${project.id}">Data Uploads</a>
        &rsaquo; <span class="aCrumb">Data File Detail</span>
    </jsp:attribute>
    <jsp:body>
		<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
		<h2>Data File Detail</h2>
		
		<table class="projectListTable">
		
		<tr><td><b>File Provided:</b></td><td><c:out value="${dataFile.userGivenFileName}"/></td></tr>
		<tr><td><b>Description:</b></td><td><c:out value="${dataFile.fileDescription}"/></td></tr>
		<tr><td><b>Content Type:</b></td><td><c:out value="${dataFile.contentType}"/></td></tr>
		<tr><td><b>Uploaded:</b></td><td><fmt:formatDate pattern="${dateTimeFormatPattern}" value="${dataFile.createDate}"/> by <c:out value="${dataFile.createUser.fullName}"/></td></tr>
		<tr><td><b>Detection Count:</b></td><td><c:out value="${dataFile.detectionCount}"/></td></tr>
		<tr><td><b>File Processing Status:</b></td>
		    <td>
		        <c:out value="${dataFile.status}"/>
		        <c:choose>
		             <c:when test="${dataFile.status=='FAILED'}">
		                &nbsp;&nbsp;<a href="<c:url value="datafileadd">
		                    <c:param name="datafile_id" value="${dataFile.id}"/>
		                    <c:param name="project_id" value="${dataFile.project.id}"/>
		                </c:url>">Retry</a>
		             </c:when>
		        </c:choose>
		    </td>
		</tr>
		
		<tr><td><b>File Processing Messages:</b></td><td><c:out value="${dataFile.statusMessage}"/></td></tr>
		
		</table>
		
		
		<table>
		<c:forEach items="${rawAcousticDetectionsList}" var="detection">
		        <tr>
		            <td><c:out value="${detection.datetime}"/></td>
		            <td><c:out value="${detection.animalid}"/></td>
		            <td><c:out value="${detection.sensor1}"/></td>
		            <td><c:out value="${detection.units1}"/></td>
		            <td><c:out value="${detection.receiverid}"/></td>
				</tr>
		</c:forEach>
		</table>
		
		</center>
    </jsp:body>
</tags:page>
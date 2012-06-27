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
            $(document).ready(function() {
                $('#navTrack').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projectdetail?id=${project.id}"/>">${project.title}</a>
        &rsaquo; <a href="<c:url value="/datafiles?project_id=${project.id}"/>">Data Uploads</a>
        &rsaquo; <span class="active">Data File Detail</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
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
		                &nbsp;&nbsp;<a href="<c:url value="/datafileadd">
		                    <c:param name="datafile_id" value="${dataFile.id}"/>
		                    <c:param name="project_id" value="${dataFile.project.id}"/>
		                </c:url>">Retry</a>
		             </c:when>
		        </c:choose>
		    </td>
		</tr>
		
		<tr><td><b>File Processing Messages:</b></td><td><c:out value="${dataFile.statusMessage}"/></td></tr>
		
		</table>
    </jsp:body>
</tags:page>

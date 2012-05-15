<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Projects">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navTrack').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a id="homeUrl" href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="aCrumb">Projects</span>
    </jsp:attribute>
    <jsp:body>
		<h1>Projects</h1>
		
		<c:choose>
		 <c:when test="${(empty user.projectUsers)}">
			<p>You have no projects to work with yet. You might like to <a href="<c:url value='projectadd'/>">add a project.</a></p>
		 </c:when>
		 <c:otherwise>
		    <p>You have access to <c:out value="${fn:length(user.projectUsers)}"/> project(s). <br>
		    Select a project to work with from the list below, or <a href="<c:url value='projectadd'/>">create a new project.</a></p>
		
			<h2>My Projects</h2>
			
			<table class="dataTable">
			   <tr>
			    <th>Title</th>
			    <th>Spatial Coverage</th>
			    <th>Project Type</th>
			    <th>Created Date</th>
			    <th>User role</th>
			   </tr>
			
			<c:forEach items="${user.projectUsers}" var="project">
			<tr>
			    <td><a href="<c:url value="projectdetail"><c:param name="id" value="${project.pk.project.id}"/></c:url>">
			            <c:out value="${project.pk.project.title}"/></a></td>
			    <td><c:out value="${project.pk.project.spatialCoverageDescr}"/></td>
			    <td><c:out value="${project.pk.project.projectType.displayName}"/></td>
			    <td><fmt:formatDate value="${project.pk.project.createDate}" type="date" dateStyle="long"/></td>
			    <td><c:out value="${project.role}"/></td>
			</tr>
			</c:forEach>
			</table>
		 </c:otherwise>
		</c:choose>
    </jsp:body>
</tags:page>
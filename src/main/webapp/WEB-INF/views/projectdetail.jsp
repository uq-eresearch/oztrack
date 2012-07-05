<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="dd/MM/yyyy"/>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<c:set var="shortDateFormatPattern" value="MMMM yyyy"/>
<tags:page title="${project.title}">
    <jsp:attribute name="head">
        <script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="<c:url value="/js/coveragemap.js"/>"></script>
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                <c:if test="${not empty project.boundingBox}">
                var coverageMap = createCoverageMap('coverageMap', '<c:out value="${project.boundingBox}"/>');
                </c:if>
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <span class="active">${project.title}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        
        <c:if test="${not empty project.boundingBox}">
        <div id="coverageMap" style="width:240px; height:200px; float:right"></div>
        </c:if>
        
        <div style="float:left; width:420px;">
        <h2 style="margin-top: 0;">Data Summary</h2>
        <c:choose>
        <c:when test="${(empty dataFileList)}">
             <p>
                 There is no data uploaded for this project yet. You might like to
                 <a href="<c:url value='/projects/${project.id}/datafiles/new'/>">upload a datafile.
             </a>
             </p>
        </c:when>
        <c:otherwise>              
            <table class="entityTable">
            <col style="width: 120px;" />
            <sec:authorize access="hasPermission(#project, 'write')">
            <tr>
                <th>Datafile Count:</th>
                <td><a href="<c:url value="/projects/${project.id}/datafiles"/>"><c:out value="${fn:length(dataFileList)}"/></a></td>
            </tr>
            </sec:authorize>
            <tr>
                <th>Detection Count:</th>
                <td><a href="<c:url value="/projects/${project.id}/search"/>"><c:out value="${project.detectionCount}"/></a></td>
            </tr>
            <tr>
                <th>Date Range:</th>
                <td><fmt:formatDate pattern="${dateFormatPattern}" value="${project.firstDetectionDate}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${project.lastDetectionDate}"/></td>
            </tr>
            <tr>
                <th>Animals:</th>
                <td>
                    <c:forEach items="${projectAnimalsList}" var="animal">
                    <a href="<c:url value="/animals/${animal.id}"/>"><c:out value="${animal.animalName}"/></a>,
                    </c:forEach>
                    <a href="<c:url value="/projects/${project.id}/animals"/>">View All</a>    
                </td>
            </tr>
            </table>
        </c:otherwise>
        </c:choose>
        </div>
        
        <div style="float:left">  
        <h2>Project Details</h2>
        <table class="entityTable">
        <tr>
            <th>Title:</th>
            <td><c:out value="${project.title}"/></td>
        </tr>
        <tr>
            <th>Description:</th>
            <td><c:out value="${project.description}"/></td>
        </tr>
        <tr>
            <th>Project Type:</th>
            <td><c:out value="${project.projectType.displayName}"/></td>
        </tr>
        <tr>
            <th>Species:</th>
            <td>
                <c:out value="${project.speciesCommonName}"/>
                <c:if test="${!empty project.speciesScientificName}"><i><br><c:out value="${project.speciesScientificName}"/></i></c:if>
          </td>
        </tr>
        <tr>
            <th>Temporal Coverage:</th>
            <td>
                <c:choose>
                <c:when test="${empty project.firstDetectionDate}">
                    No data has been uploaded for this project yet.
                </c:when>
                <c:otherwise>
                    <fmt:formatDate pattern="${shortDateFormatPattern}" value="${project.firstDetectionDate}"/> to <fmt:formatDate pattern="${shortDateFormatPattern}" value="${project.lastDetectionDate}"/>
                </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>Spatial Coverage:</th>
            <td><c:out value="${project.spatialCoverageDescr}"/></td>
        </tr>
        <tr>
            <th>Contact:</th>
            <td>
                <c:out value="${project.dataSpaceAgent.firstName}"/>&nbsp;<c:out value="${project.dataSpaceAgent.lastName}"/><br>
                <a href="mailto:<c:out value="${project.dataSpaceAgent.email}"/>"><c:out value="${project.dataSpaceAgent.email}"/></a>
            </td>
        </tr>
        <tr>
            <th>Contact Organisation:</th>
            <td><c:out value="${project.dataSpaceAgent.organisation}"/></td>
        </tr>
        <tr>
            <th>Publication:</th>
            <td>
                <i><c:out value="${project.publicationTitle}"/></i><br>
                <a href="<c:out value="${project.publicationUrl}"/>"><c:out value="${project.publicationUrl}"/></a>
            </td>
        </tr>
        <tr>
            <th>Rights Statement:</th>
            <td><c:out value="${project.rightsStatement}"/></td>
        </tr>
        <tr>
            <th>Access Rights:</th>
            <td>
            <c:choose>
            <c:when test="${project.isGlobal}">
                The data in the project is available in OzTrack for the public to use.
            </c:when>
            <c:otherwise>
                The data in this project is only available to users on the OzTrack system whom have been granted access.
            </c:otherwise>
            </c:choose>
            </td>
        </tr>
        
        <sec:authorize access="hasPermission(#project, 'write')">
        <tr>
            <th>Publication Status:</th>
            <td>
                <c:choose>
                <c:when test ="${empty project.dataSpaceUpdateDate}">
                    This project metadata has not yet been published externally.
                    <c:set var="publishButtonText" value="Publish Metadata to UQ DataSpace"/> 
                </c:when>
                <c:otherwise>
                    This project metadata has been published and was last updated on 
                    <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${project.dataSpaceUpdateDate}"/>.
                    <c:set var="publishButtonText" value="Update UQ DataSpace Collection Registry"/> 
                </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th></th>
            <td>
            <a class="oztrackButton" href="<c:url value="/projects/${project.id}/edit"/>">Edit Project Metadata</a>
            &nbsp;&nbsp;
            <a class="oztrackButton" href="<c:url value="/projects/${project.id}/publish"/>"><c:out value="${publishButtonText}"/></a>
            </td>
        </tr>
        </sec:authorize>
        
        </table>
        </div>
    </jsp:body>
</tags:page>

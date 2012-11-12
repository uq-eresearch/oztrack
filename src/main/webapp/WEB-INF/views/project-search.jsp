<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="dd/MM/yyyy"/>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy HH:mm:ss"/>
<tags:page title="${project.title}: View Raw Data">
    <jsp:attribute name="head">
        <style type="text/css">
            .dataTableNav {
                margin: 10px 0;
                font-weight: bold;
            }
            .dataTableNav a {
                font-weight: bold;
                text-decoration: none;
            }
        </style>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuSearch').addClass('active');
                $('#fromDateVisible').datepicker({
                    altField: "#fromDate"
                });
                $('#toDateVisible').datepicker({
                    altField: "#toDate"
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">View Raw Data</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>

        <form:form class="form-horizontal form-bordered" commandName="searchQuery" method="POST" name="searchQuery">
            <fieldset>
                <div class="legend">Search Project Data</div>
                <div class="control-group">
                    <label class="control-label" for="fromDateVisible">Date Range</label>
                    <div class="controls">
                        <form:hidden path="fromDate" id="fromDate"/>
                        <form:hidden path="toDate" id="toDate"/>
                        <input type="text" id="fromDateVisible" class="datepicker" style="width: 80px;" placeholder="From" value="<fmt:formatDate pattern="${dateFormatPattern}" value="${searchQuery.fromDate}"/>"/> -
                        <input type="text" id="toDateVisible" class="datepicker" style="width: 80px;" placeholder="To" value="<fmt:formatDate pattern="${dateFormatPattern}" value="${searchQuery.toDate}"/>"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="animalIds">Animal</label>
                    <div class="controls">
                        <form:select id="animalIds" path="animalIds" items="${projectAnimalsList}" itemLabel="animalName" itemValue="id" multiple="true"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="sortField">Sort by:</label>
                    <div class="controls">
                        <form:select path="sortField">
                            <form:option value="Animal"/>
                            <form:option value="Detection Time"/>
                        </form:select>
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Search"/>
            </div>
        </form:form>


        <div class="dataTableNav">
        <div style="float:left;">
            Displaying <c:out value="${offset+1}"/> to <c:out value="${offset+nbrObjectsThisPage}"/> of <c:out value="${totalCount}"/> records.
        </div>
        <div style="float:right">
            <c:choose>
            <c:when test="${offset > 0}">
                <a href="<c:url value="/projects/${searchQuery.project.id}/search">
                    <c:param name="offset" value="${0}"/>
                </c:url>">&lt;&lt;</a>
                &nbsp;&nbsp;
                <a href="<c:url value="/projects/${searchQuery.project.id}/search">
                    <c:param name="offset" value="${offset-nbrObjectsPerPage}"/>
                </c:url>">&lt;</a>
            </c:when>
            <c:otherwise>
                &lt;&lt;
                &nbsp;&nbsp;
                &lt;
            </c:otherwise>
            </c:choose>
            &nbsp;&nbsp;
            <c:choose>
            <c:when test="${offset < totalCount - (totalCount % nbrObjectsPerPage)}">
                <a href="<c:url value="/projects/${searchQuery.project.id}/search">
                    <c:param name="offset" value="${offset+nbrObjectsThisPage}"/>
                </c:url>">&gt;</a>
                &nbsp;&nbsp;
                <a href="<c:url value="/projects/${searchQuery.project.id}/search">
                    <c:param name="offset" value="${totalCount - (totalCount % nbrObjectsPerPage)}"/>
                </c:url>">&gt;&gt;</a>
            </c:when>
            <c:otherwise>
                &gt;
                &nbsp;&nbsp;
                &gt;&gt;
            </c:otherwise>
            </c:choose>
            &nbsp;&nbsp;
            <a href="<c:url value="/projects/${searchQuery.project.id}/export"/>">Export</a>
        </div>
        <div style="clear: both;"></div>
        </div>

        <c:if test="${positionFixList != null}">

        <table class="table table-bordered table-condensed">
            <col style="width: 110px;" />
            <col style="width: 60px;" />
            <col style="width: 150px;" />
            <col style="width: 70px;" />
            <col style="width: 70px;" />
            <sec:authorize access="hasPermission(#project, 'read')">
            <col style="width: 70px;" />
            </sec:authorize>
            <thead>
                <tr>
                    <th>Date/Time</th>
                    <th>Animal Id</th>
                    <th>Animal Name</th>
                    <th>Latitude</th>
                    <th>Longitude</th>
                    <sec:authorize access="hasPermission(#project, 'read')">
                    <th>Uploaded</th>
                    </sec:authorize>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${positionFixList}" var="detection">
                <tr>
                    <td><fmt:formatDate pattern="${dateTimeFormatPattern}" value="${detection.detectionTime}"/></td>
                    <td><c:out value="${detection.animal.projectAnimalId}"/></td>
                    <td><a href="<c:url value="/animals/${detection.animal.id}/edit"/>">
                            <c:out value="${detection.animal.animalName}"/></a></td>
                    <td><c:out value="${detection.latitude}"/></td>
                    <td><c:out value="${detection.longitude}"/></td>
                    <sec:authorize access="hasPermission(#project, 'read')">
                    <td>
                        <a href="<c:url value="/datafiles/${detection.dataFile.id}"/>"
                        ><fmt:formatDate pattern="${dateFormatPattern}" value="${detection.dataFile.createDate}"/></a>
                    </td>
                    </sec:authorize>
                </tr>
                </c:forEach>
            </tbody>
        </table>

        </c:if>
    </jsp:body>
</tags:page>

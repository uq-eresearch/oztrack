<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="/WEB-INF/functions.tld" prefix="oztrack" %>
<c:set var="dateFormatPattern" value="yyyy-MM-dd"/>
<tags:page title="${project.title}: View Data">
    <jsp:attribute name="description">
        View data in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuSearch').addClass('active');
                $('#fromDateVisible').datepicker({
                    altField: "#fromDate",
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    defaultDate: new Date(${projectDetectionDateRange.minimum.time})
                });
                $('#toDateVisible').datepicker({
                    altField: "#toDate",
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    defaultDate: new Date(${projectDetectionDateRange.maximum.time})
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <span class="active">View Data</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
        <tags:data-actions project="${project}"/>
        <tags:project-licence project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>

        <form:form class="form-horizontal form-bordered" commandName="searchQuery" method="GET" action="${pageContext.request.contextPath}/projects/${project.id}/search">
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
                        <select id="animalIds" name="animalIds" multiple="multiple">
                            <c:forEach items="${projectAnimalsList}" var="animal">
                            <option value="${animal.id}" ${oztrack:contains(searchQuery.animalIds, animal.id) ? 'selected="selected"' : ''}>
                                ${animal.animalName}
                            </option>
                            </c:forEach>
                        </select>
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
                <sec:authorize access="hasPermission(#project, 'write')">
                <div class="control-group">
                    <label class="control-label" for="includeDeleted">Include deleted:</label>
                    <div class="controls">
                        <input type="checkbox" name="includeDeleted" ${searchQuery.includeDeleted ?  'checked="checked"' : ''} value="true" />
                    </div>
                </div>
                </sec:authorize>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Search"/>
            </div>
        </form:form>

        <tags:search-results positionFixPage="${positionFixPage}" includeDeleted="${searchQuery.includeDeleted}"/>
    </jsp:body>
</tags:page>

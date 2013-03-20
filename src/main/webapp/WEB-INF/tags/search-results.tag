<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="positionFixPage" type="org.oztrack.data.access.Page" required="true" %>
<%@ attribute name="individualAnimal" type="java.lang.Boolean" required="false" %>
<c:set var="dateFormatPattern" value="yyyy-MM-dd"/>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm:ss"/>
<div class="searchResultsNav">
<div style="float:left; padding: 5px 0;">
    Detections <c:out value="${positionFixPage.offset+1}"/> to <c:out value="${positionFixPage.offset+fn:length(positionFixPage.objects)}"/> of <c:out value="${positionFixPage.count}"/>
</div>
<div style="float:right">
    <c:set var="urlParamsPrefix" value="${searchQuery.urlParams}${not empty searchQuery.urlParams ? '&' : ''}"/>
    <div class="btn-group">
        <c:choose>
        <c:when test="${(positionFixPage.offset > 0)}">
        <a class="btn" href="${pageContext.request.contextPath}/projects/${searchQuery.project.id}/search?${urlParamsPrefix}offset=${0}">&lt;&lt;</a>
        <a class="btn" href="${pageContext.request.contextPath}/projects/${searchQuery.project.id}/search?${urlParamsPrefix}offset=${positionFixPage.offset-positionFixPage.limit}">&lt;</a>
        </c:when>
        <c:otherwise>
        <a class="btn disabled" href="javascript:void(0);">&lt;&lt;</a>
        <a class="btn disabled" href="javascript:void(0);">&lt;</a>
        </c:otherwise>
        </c:choose>
        <c:choose>
        <c:when test="${(positionFixPage.offset + positionFixPage.limit < positionFixPage.count)}">
        <a class="btn" href="${pageContext.request.contextPath}/projects/${searchQuery.project.id}/search?${urlParamsPrefix}offset=${positionFixPage.offset + fn:length(positionFixPage.objects)}">&gt;</a>
        <a class="btn" href="${pageContext.request.contextPath}/projects/${searchQuery.project.id}/search?${urlParamsPrefix}offset=${positionFixPage.count - (positionFixPage.count % positionFixPage.limit) - (((positionFixPage.count > positionFixPage.limit) && (positionFixPage.count % positionFixPage.limit == 0)) ? positionFixPage.limit : 0)}">&gt;&gt;</a>
        </c:when>
        <c:otherwise>
        <a class="btn disabled" href="javascript:void(0);">&gt;</a>
        <a class="btn disabled" href="javascript:void(0);">&gt;&gt;</a>
        </c:otherwise>
        </c:choose>
    </div>
    <div class="btn-group">
        <a class="btn" href="${pageContext.request.contextPath}/projects/${searchQuery.project.id}/export?${urlParamsPrefix}format=csv">Export CSV</a>
        <a class="btn" href="${pageContext.request.contextPath}/projects/${searchQuery.project.id}/export?${urlParamsPrefix}format=xls">XLS</a>
    </div>
</div>
<div style="clear: both;"></div>
</div>

<c:if test="${positionFixPage.objects != null}">
<table class="table table-bordered table-condensed">
    <col style="width: 110px;" />
    <c:if test="${not individualAnimal}">
    <col style="width: 60px;" />
    <col style="width: 150px;" />
    </c:if>
    <col style="width: 70px;" />
    <col style="width: 70px;" />
    <sec:authorize access="hasPermission(#project, 'write')">
    <col style="width: 70px;" />
    </sec:authorize>
    <thead>
        <tr>
            <th>Date/Time</th>
            <c:if test="${not individualAnimal}">
            <th>Animal Id</th>
            <th>Animal Name</th>
            </c:if>
            <th>Latitude</th>
            <th>Longitude</th>
            <sec:authorize access="hasPermission(#project, 'write')">
            <th>Uploaded</th>
            </sec:authorize>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${positionFixPage.objects}" var="detection">
        <tr>
            <td><fmt:formatDate pattern="${dateTimeFormatPattern}" value="${detection.detectionTime}"/></td>
            <c:if test="${not individualAnimal}">
            <td><c:out value="${detection.animal.projectAnimalId}"/></td>
            <td><a href="${pageContext.request.contextPath}/animals/${detection.animal.id}"><c:out value="${detection.animal.animalName}"/></a></td>
            </c:if>
            <td><c:out value="${detection.latitude}"/></td>
            <td><c:out value="${detection.longitude}"/></td>
            <sec:authorize access="hasPermission(#project, 'write')">
            <td>
                <a href="${pageContext.request.contextPath}/datafiles/${detection.dataFile.id}"
                ><fmt:formatDate pattern="${dateFormatPattern}" value="${detection.dataFile.createDate}"/></a>
            </td>
            </sec:authorize>
        </tr>
        </c:forEach>
    </tbody>
</table>
</c:if>
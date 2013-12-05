<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Settings">
    <jsp:attribute name="description">
        Update institution records in OzTrack.
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <span class="active">Institutions</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Institutions</h1>
        <c:forEach items="${institutions}" var="institution">
        <h2><c:out value="${institution.title}"/></h2>
        <ul>
            <c:forEach items="${institution.people}" var="person">
            <li>${person.fullName}<c:if test="${not empty person.email}"> (${person.email})</c:if></li>
            </c:forEach>
        </ul>
        <form method="POST" action="${pageContext.request.contextPath}/institutions/${institution.id}" class="form-inline well" style="padding: 10px;">
            <input type="hidden" name="_method" value="PUT" />
            <label for="institution-${institution.id}-title">Update</label>
            <input type="text" name="title"
                id="institution-${institution.id}-title" class="input-large"
                placeholder="e.g. The University of Queensland"
                value="${institution.title}"
                />
            <input type="text" name="domainName"
                id="institution-${institution.id}-domainName" class="input-large"
                placeholder="e.g. uq.edu.au"
                value="${institution.domainName}"
                />
            <select name="country" id="institution-${institution.id}-country" style="width: 224px;">
                <option value="">Select country</option>
                <c:forEach var="country" items="${countries}">
                <option value="${country.id}"<c:if test="${country == institution.country}"> selected="selected"</c:if>>${country.title}</option>
                </c:forEach>
            </select>
            <button type="submit" class="btn">Save</button>
            <button class="btn" onclick="
                void(OzTrack.deleteEntity(
                    '${pageContext.request.contextPath}/institutions/${institution.id}',
                    '${pageContext.request.contextPath}/settings/institutions',
                    'Are you sure you want to delete this institution?'
                )); return false;">Delete</button>
        </form>
        </c:forEach>
    </jsp:body>
</tags:page>
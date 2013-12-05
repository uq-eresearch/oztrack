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
        <h2>${institution.title} (ID ${institution.id})</h2>
        <ul>
            <li><b>Domain</b>: ${institution.domainName}</li>
            <li><b>Country</b>: ${institution.country.title}</li>
        </ul>
        <ul>
            <c:forEach items="${institution.people}" var="person">
            <li>${person.fullName}<c:if test="${not empty person.email}"> (${person.email})</c:if></li>
            </c:forEach>
        </ul>
        <div>
            <a class="btn" href="${pageContext.request.contextPath}/institutions/${institution.id}/edit">Edit</a>
        </div>
        </c:forEach>
    </jsp:body>
</tags:page>
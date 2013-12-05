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
        <table class="table table-bordered">
            <col style="width: 30px;" />
            <col style="width: 300px;" />
            <col style="width: 150px;" />
            <col style="width: 150px;" />
            <col style="width: 300px;" />
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Domain</th>
                    <th>Country</th>
                    <th>People</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${institutions}" var="institution">
                <tr>
                    <td><c:out value="${institution.id}"/></td>
                    <td><c:out value="${institution.title}"/></td>
                    <td><c:out value="${institution.domainName}"/></td>
                    <td><c:out value="${institution.country.title}"/></td>
                    <td>
                        <ul>
                            <c:forEach items="${institution.people}" var="person">
                            <li>${person.fullName}<c:if test="${not empty person.email}"> (${person.email})</c:if></li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </jsp:body>
</tags:page>
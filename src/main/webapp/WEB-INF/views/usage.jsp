<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm"/>
<tags:page title="Usage">
    <jsp:attribute name="description">
        Summary of OzTrack usage statistics.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navSettings').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <span class="active">Usage Summary</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Usage Summary</h1>
        <p>
            <jsp:useBean id="now" class="java.util.Date" />
            <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${now}" />
        </p>
        <h2>Users</h2>
        <p>
            Total of ${fn:length(users)} users.
        </p>
        <table class="table table-bordered table-striped">
            <thead>
            <tr>
                <th>User</th>
                <th>Organisation</th>
                <th>Projects</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${users}" var="user">
            <tr>
                <td>
                    <p>${user.fullName} (${user.username})</p>
                    <p>
                        Logged in ${fn:length(user.loginDates)} times.
                        <c:if test="${not empty user.lastLoginDate}">
                        Last login <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${user.lastLoginDate}"/>.
                        </c:if>
                    </p>
                </td>
                <td>${user.organisation}</td>
                <td>
                    <p>
                        ${fn:length(user.projectUsers)} projects
                    </p>
                    <ul>
                        <c:forEach items="${user.projectUsers}" var="projectUser">
                        <li>${projectUser.project.title} (${projectUser.role})</li>
                        </c:forEach>
                    </ul>
                </td>
            </tr>
            </c:forEach>
            </tbody>
        </table>
        <h2>Projects</h2>
        <p>
            Total of ${fn:length(projects)} projects.
        </p>
        <table class="table table-bordered table-striped">
            <thead>
            <tr>
                <th>Project</th>
                <th>Data</th>
                <th>Users</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${projects}" var="project">
            <tr>
                <td>
                    <p style="font-weight: bold;">${project.title}</p>
                    <p>${project.description}</p>
                </td>
                <td>
                    <p>${fn:length(project.animals)} animals (${detectionCount[project]} position fixes)</p>
                    <c:if test="${not empty project.speciesCommonName or not empty project.speciesScientificName}">
                    <p>${project.speciesCommonName} <i>${project.speciesScientificName}</i></p>
                    </c:if>
                </td>
                <td>
                    <p>
                        ${fn:length(project.projectUsers)} users
                        (<c:forEach items="${project.projectUsers}" var="projectUser" varStatus="projectUserStatus">${projectUser.user.username}<c:if test="${!projectUserStatus.last}">, </c:if></c:forEach>)
                    </p>
                </td>
            </tr>
            </c:forEach>
            </tbody>
        </table>
        <h2>Statistics</h2>
        <ul>
            <li>Total of ${numAnimals} animals.</li>
            <li>Total of ${numDataFiles} data files.</li>
            <li>Total of ${numPositionFixes} position fixes.</li>
        </ul>
    </jsp:body>
</tags:page>

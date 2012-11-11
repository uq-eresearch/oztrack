<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Usage">
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navSettings').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/settings"/>">Settings</a>
        &rsaquo; <span class="active">Usage Summary</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
        <h1>Usage Summary</h1>
        <h2>Users</h2>
        <p>
            Total of ${fn:length(users)} users.
        </p>
        <table class="table table-bordered">
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
                <td>${user.fullName} (${user.username})</td>
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
        <table class="table table-bordered">
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
                    <p>${fn:length(project.animals)} animals (${projectDao.getDetectionCount(project, false)} position fixes)</p>
                    <p style="font-style: italic;">Species: ${project.speciesCommonName} (${project.speciesScientificName})</p>
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

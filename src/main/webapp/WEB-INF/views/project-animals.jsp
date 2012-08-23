<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Animals">
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">Animals</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Animals</h2>

        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Species</th>
                    <sec:authorize access="hasPermission(#project, 'write')">
                    <th> </th>
                    </sec:authorize>
                </tr>
            </thead>
            <tbody>
            <c:forEach items="${projectAnimalsList}" var="animal">
                <tr>
                    <td><c:out value="${animal.projectAnimalId}"/></td>
                    <td><a href="<c:url value="/animals/${animal.id}"/>"><c:out value="${animal.animalName}"/></a></td>
                    <td><c:out value="${animal.animalDescription}"/></td>
                    <td><c:out value="${animal.speciesName}"/></td>
                    <sec:authorize access="hasPermission(#project, 'write')">
                    <td>
                        <a href="<c:url value="/animals/${animal.id}/edit"/>">Edit</a>
                        <c:if test="${empty animal.positionFixes}">
                        <br /><a href="javascript:void(deleteEntity('<c:url value="/animals/${animal.id}"/>', '<c:url value="/projects/${project.id}/animals"/>', 'Are you sure you want to delete this animal?'));">Delete</a>
                        </c:if>
                    </td>
                    </sec:authorize>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </jsp:body>
</tags:page>

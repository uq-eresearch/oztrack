<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Reject contributor listing">
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <span class="active">Reject contributor listing</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
        <tags:project-licence project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <form class="form-vertical form-bordered" action="" method="POST">
            <fieldset>
                <div class="legend">Reject contribution listing</div>
                <p>
                    Welcome, <span style="font-weight: bold;">${contribution.contributor.fullName}</span>.
                </p>
                <p>
                    Are you sure you want to reject your listing as a contributor on this project?
                </p>
            </fieldset>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Confirm</button>
            </div>
        </form>
    </jsp:body>
</tags:page>

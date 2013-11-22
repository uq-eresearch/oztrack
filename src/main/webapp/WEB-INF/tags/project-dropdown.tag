<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<div class="btn-group">
    <a class="btn btn-inverse" href="/projects/${project.id}">Back to project</a>
    <a class="btn btn-inverse dropdown-toggle" data-toggle="dropdown" href="#">
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu">
        <tags:project-menu project="${project}" itemsOnly="${true}"/>
        <c:set var="projectActions"><tags:project-actions project="${project}" itemsOnly="${true}"/></c:set>
        <c:if test="${not empty projectActions}"><hr />${projectActions}</c:if>
    </ul>
</div>
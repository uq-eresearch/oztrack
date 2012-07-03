<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<div class="sidebarMenu">
    <ul>
        <li><a href="<c:url value="/projects/${project.id}"/>">Project Details</a></li>
        <li><a href="<c:url value="/projects/${project.id}/tools"/>">Analysis Tools</a></li>
        <li><a href="<c:url value="/projects/${project.id}/search"/>">View Raw Data</a></li>
        <li><a href="<c:url value="/projects/${project.id}/datafiles"/>">Data Uploads</a></li>
    </ul>
</div>
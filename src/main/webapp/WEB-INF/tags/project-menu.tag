<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<div class="sidebarMenu">
    <ul>
        <li><a href="<c:url value="/projectdetail"><c:param name="id" value="${project.id}"/></c:url>">Project Details</a></li>
        <li><a href="<c:url value="/projectmap"><c:param name="id" value="${project.id}"/></c:url>">Analysis Tools</a></li>
        <li><a href="<c:url value="/searchform"><c:param name="project_id" value="${project.id}"/></c:url>">View Raw Data</a></li>
        <li><a href="<c:url value="/datafiles"><c:param name="project_id" value="${project.id}"/></c:url>">Data Uploads</a></li>
    </ul>
</div>
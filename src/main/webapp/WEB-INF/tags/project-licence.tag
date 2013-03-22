<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<c:set var="dataLicencingEnabled"><%= OzTrackApplication.getApplicationContext().isDataLicencingEnabled() %></c:set>
<c:if test="${dataLicencingEnabled}">
<c:if test="${(project.access == 'OPEN') and (project.dataLicence != null)}">
</c:if>
<div class="sidebar-data-licence">
<p class="sidebar-data-licence-image">
    <a href="${project.dataLicence.infoUrl}"><img src="${project.dataLicence.imageUrl}" /></a>
</p>
<p class="sidebar-data-licence-text">
    Data in this project are made available under the
    <a href="${project.dataLicence.infoUrl}">${project.dataLicence.title}</a>
    licence.
</p>
</div>
</c:if>
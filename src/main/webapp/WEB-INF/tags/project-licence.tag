<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<c:if test="${(project.access == 'OPEN') and (project.dataLicence != null)}">
<div class="sidebar-data-licence">
<p class="sidebar-data-licence-image">
    <a target="_blank" href="${project.dataLicence.infoUrl}"
        ><img src="${pageContext.request.scheme}://${fn:substringAfter(project.dataLicence.imageUrl, '://')}"/></a>
</p>
<p class="sidebar-data-licence-text">
    Data in this project are made available under the
    <a target="_blank" href="${project.dataLicence.infoUrl}">${project.dataLicence.title}</a>.
</p>
</div>
</c:if>
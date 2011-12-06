<%@ include file="header.jsp" %>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>

<h2>Project Details</h2>
<table class="projectListTable">
<tr><td class="projectFieldName">Title:</td><td><c:out value="${project.title}"/></td></tr>
<tr><td class="projectFieldName">Description:</td><td><c:out value="${project.description}"/></td></tr>
<tr><td class="projectFieldName">Project Type:</td><td><c:out value="${project.projectType.displayName}"/></td></tr>
<tr><td class="projectFieldName">Contact:</td><td><c:out value="${project.contactGivenName}"/><c:out value="${project.contactFamilyName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
<tr><td class="projectFieldName">Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
<tr><td class="projectFieldName">Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
</table>

<p><a href="home">Return to Home page</a></p>

<%@ include file="footer.jsp" %>

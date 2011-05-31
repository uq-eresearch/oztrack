<%@ include file="header.jsp" %>

<h1><c:out value="${project.title}"/></h1>


<table class="projectListTable">
<tr><td><b>Description:</b><td><c:out value="${project.description}"/></td></tr>
<tr><td><b>Contact Details:</b><td><c:out value="${project.contactName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
<tr><td><b>Custodian Details:</b><td><c:out value="${project.custodianName}"/><br><c:out value="${project.custodianUrl}"/></td></tr>
<tr><td><b>Spatial Coverage:</b><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
<tr><td><b>Temporal Coverage:</b><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
<tr><td><b>Publications:</b><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
<tr><td><a class="oztrackButton" href="#">Edit</a><br><br></td></tr>

</table>




<%@ include file="footer.jsp" %>
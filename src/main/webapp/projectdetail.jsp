<%@ include file="header.jsp" %>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>

<div id="map_canvas"></div>

<table class="projectListTable">
<tr><td><b>Description:</b></td><td><c:out value="${project.description}"/></td></tr>
<tr><td><b>Project Data:</b></td>
    <td><a href="<c:url value="datafiles"/>">Data Files</a><br>
        <a href="<c:url value="projectanimals"/>">Animals</a><br>
        <c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}">
            <a href="<c:url value="projectreceivers"/>">Receivers</a><br>
        </c:if>
    </td>
</tr>
<tr><td><b>Project Type:</b></td><td><c:out value="${project.projectType.displayName}"/></td></tr>
<tr><td><b>Contact Details:</b></td><td><c:out value="${project.contactName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
<tr><td><b>Custodian Details:</b></td><td><c:out value="${project.custodianName}"/><br><c:out value="${project.custodianUrl}"/></td></tr>
<tr><td><b>Spatial Coverage:</b></td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
<tr><td><b>Temporal Coverage:</b></td><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
<tr><td><b>Publications:</b></td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
<tr><td><a class="oztrackButton" href="#">Edit</a><br><br></td></tr>

</table>

<%@ include file="footer.jsp" %>
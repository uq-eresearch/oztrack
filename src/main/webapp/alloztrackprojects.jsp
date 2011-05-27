<%@ include file="header.jsp" %>

<h2>All OzTrack Projects</h2>


<c:forEach items="${projectList}" var="project">

    <div class="accordianHead">
       <table id="alloztrackprojectsTableHeader">
       <tr>
         <td><a href="#"><c:out value="${project.title}"/></a></td>
         <td><c:out value="${project.spatialCoverageDescr}"/></td>
         <td>Created: 01/05/2011</td>
        </tr>
        </table>
    </div>

    <div class="accordianBody">

        <table id="alloztrackprojectsTable">
        <tr><td>Description:</td><td><c:out value="${project.description}"/></td></tr>
        <tr><td>Species:</td><td>Saltwater Crocodiles</td></tr>
        <tr><td>Custodian Details:</td><td><c:out value="${project.contactName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
        <tr><td>Custodian Details:</td><td><c:out value="${project.custodianName}"/><br><c:out value="${project.custodianUrl}"/></td></tr>
        <tr><td>Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
        <tr><td>Temporal Coverage:</td><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
        <tr><td>Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
        <tr><td>Data Globally Available?
                <td><c:choose>
                    <c:when test="${project.isGlobal == true}">Y</c:when>
                    <c:otherwise>N</c:otherwise>
                </c:choose></td>
         </td></tr>

        </table>
    </div>
    <br><br>
</c:forEach>

<!--
<a href="<c:url value="projectdetail"><c:param name="project_id" value="${thisUserProject.pk.project.id}"/></c:url>"></a>
-->

<%@ include file="footer.jsp" %>
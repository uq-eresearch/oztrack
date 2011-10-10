<%@ include file="header.jsp" %>
<script type="text/javascript">projectPage = true;</script>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>

<h2>Summary</h2>
<table class="projectListTable">
<tr><td class="projectFieldName">Datafile Count:</td><td><c:out value="${fn:length(project.dataFiles)}"/></td></tr>
<tr><td class="projectFieldName">Detection Count:</td><td><c:out value="${project.detectionCount}"/></td></tr>
<tr><td class="projectFieldName">First Detection Date:</td><td><c:out value="${project.firstDetectionDate}"/></td></tr>
<tr><td class="projectFieldName">Last Detection Date:</td><td><c:out value="${project.lastDetectionDate}"/></td></tr>
<tr><td class="projectFieldName">Animals:</td><td><c:forEach items="${projectAnimalsList}" var="animal">
								<c:out value="${animal.animalName}"/><br>
						  </c:forEach>	
	</td>
</tr>
</table>

<h2>Project Details</h2>
<table class="projectListTable">
<tr><td class="projectFieldName">Description:</td><td><c:out value="${project.description}"/></td></tr>
<tr><td class="projectFieldName">Project Data:</td>
    <td><a href="<c:url value="datafiles"/>">Data Files</a><br>
        <a href="<c:url value="projectanimals"/>">Animals</a><br>
        <c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}">
            <a href="<c:url value="projectreceivers"/>">Receivers</a><br>
        </c:if>
    </td>
</tr>
<tr><td class="projectFieldName">Project Type:</td><td><c:out value="${project.projectType.displayName}"/></td></tr>
<tr><td class="projectFieldName">Contact Details:</td><td><c:out value="${project.contactName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
<tr><td class="projectFieldName">Custodian Details:</td><td><c:out value="${project.custodianName}"/><br><c:out value="${project.custodianUrl}"/></td></tr>
<tr><td class="projectFieldName">Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
<tr><td class="projectFieldName">Temporal Coverage:</td><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
<tr><td class="projectFieldName">Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
<tr><td class="projectFieldName"></td><td><a class="oztrackButton" href="#">Edit</a><br><br></td></tr>

</table>



<%@ include file="footer.jsp" %>
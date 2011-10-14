<%@ include file="header.jsp" %>
<script type="text/javascript">projectPage = true;</script>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>

<h2>Summary</h2>
<c:choose>
 
 <c:when test="${(empty dataFileList)}">
	 <p>
	 There is no data uploaded for this project yet. You might like to <a href="<c:url value='datafileadd'/>">upload a datafile.
	 </a>
	 </p>
 </c:when>
 <c:otherwise>	 	 	
	<table class="projectListTable">
	<tr><td class="projectFieldName">Datafile Count:</td>
					<td><a href="<c:url value="datafiles"/>"><c:out value="${fn:length(dataFileList)}"/></a></td></tr>
	<tr><td class="projectFieldName">Detection Count:</td>
					<td><a href="<c:url value="searchform"/>"><c:out value="${project.detectionCount}"/></a></td></tr>
	<tr><td class="projectFieldName">Detection Date Range:</td><td><fmt:formatDate pattern="${dateFormatPattern}" value="${project.firstDetectionDate}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${project.lastDetectionDate}"/></td></tr>
	<tr><td class="projectFieldName">Animals:</td><td>
							<c:forEach items="${projectAnimalsList}" var="animal">
									<a href="<c:url value="animalform"><c:param name="animal_id" value="${animal.id}"/></c:url>"><c:out value="${animal.animalName}"/></a>,
							  </c:forEach>
							  <a href="<c:url value="projectanimals"/>">View All</a>	
		</td>
	</tr>
	
	<c:if test="${project.projectType == 'PASSIVE_ACOUSTIC'}">
		<tr><td class="projectFieldName">Receivers:</td><td><a href="<c:url value="projectreceivers"/>">View List</a></td></tr>
	</c:if>
	</table>
  </c:otherwise>
</c:choose>
  
<h2>Project Details</h2>
<table class="projectListTable">
<tr><td class="projectFieldName">Description:</td><td><c:out value="${project.description}"/></td></tr>
<tr><td class="projectFieldName">Project Type:</td><td><c:out value="${project.projectType.displayName}"/></td></tr>
<tr><td class="projectFieldName">Contact:</td><td><c:out value="${project.contactGivenName}"/><c:out value="${project.contactFamilyName}"/><br><c:out value="${project.contactUrl}"/></td></tr>
<tr><td class="projectFieldName">Spatial Coverage:</td><td><c:out value="${project.spatialCoverageDescr}"/></td></tr>
<tr><td class="projectFieldName">Temporal Coverage:</td><td><c:out value="${project.temporalCoverageDescr}"/></td></tr>
<tr><td class="projectFieldName">Publications:</td><td><i><c:out value="${project.publicationTitle}"/></i><br> <c:out value="${project.publicationUrl}"/></td></tr>
<tr><td class="projectFieldName"></td><td><a class="oztrackButton" href="#">Edit</a><br><br></td></tr>

</table>

<%@ include file="footer.jsp" %>
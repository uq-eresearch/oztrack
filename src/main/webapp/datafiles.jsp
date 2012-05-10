<%@ include file="header.jsp" %>
<script type="text/javascript">
	projectPage = true;
</script>


<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Data Uploads</h2>


<c:choose>
 <c:when test="${(empty dataFileList)}">
	<p>This project has no data to work with. You might like to <a href="<c:url value='datafileadd'>
	    <c:param name="project_id" value="${project.id}"/>
	</c:url>">upload a data file.</a></p>
	<a class="oztrackButton" id="pageRefresh" href="#">Refresh</a>
 </c:when>
 <c:otherwise>
    
    <p><c:out value="${fn:length(dataFileList)}"/> data file(s) found.</p>

		<p><a class="oztrackButton" href="<c:url value='datafileadd'>
	        <c:param name="project_id" value="${project.id}"/>
	    </c:url>" >Add a Datafile</a>
		<a class="oztrackButton" id="pageRefresh" href="#">Refresh</a></p>
		
		<p><c:out value="${errorStr}"/></p>
		
		<table class="dataTable" id="dataFileStatusTable">
		
		    <tr>
		        <th>File Name</th>
		        <th>Detection Date Range</th>
		        <th>Upload Date</th>
		        <th>File Status</th>
		    </tr>
		
		    <c:forEach items="${dataFileList}" var="dataFile">
		        <tr>
		            <td><c:out value="${dataFile.userGivenFileName}"/></td>
		            <td><c:if test="${fn:contains(dataFile.status,'COMPLETE')}">
		            	<fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.firstDetectionDate}"/> to <fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.lastDetectionDate}"/>
		            	</c:if>
		             </td>	
		            <td><fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.createDate}"/>
		            <td>
		                <a href="<c:url value="datafiledetail">
					        <c:param name="project_id" value="${project.id}"/>
					        <c:param name="datafile_id" value="${dataFile.id}"/>
				            </c:url>"><c:out value="${dataFile.status}"/></a>
		            </td>
				</tr>
		    </c:forEach>
		</table>
	</c:otherwise>
</c:choose>	

<%@ include file="footer.jsp" %>
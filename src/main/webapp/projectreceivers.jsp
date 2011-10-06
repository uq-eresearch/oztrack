<%@ include file="header.jsp" %>
<script type="text/javascript">projectPage = true;</script>


<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Receivers</h2>

<table class="dataTable">

    <tr>
        <th>Receiver ID</th>
        <th>Name</th>
        <th>Description</th>
        <th>Deployment Date</th>
        <th>Retrieval Date</th>
        <th>Latitude</th>
        <th>Longitude</th>
        <th> </th>
    </tr>

    <c:forEach items="${receiverList}" var="receiver">
        <tr>
           <td><c:out value="${receiver.originalId}"/></td>
            <td><c:out value="${receiver.receiverName}"/></td>
            <td><c:out value="${receiver.receiverDescription}"/></td>
            <td><c:out value="${receiver.deploymentDate}"/></td>
            <td><c:out value="${receiver.retrievalDate}"/></td>
            <td><c:out value="${receiver.receiverLocation.latitude}"/></td>
            <td><c:out value="${receiver.receiverLocation.longitude}"/></td>
            <td>
		        <!--
		            <a href="<c:url value="receiverform"><c:param name="receiver_id" value="${receiver.id}"/></c:url>">
                -->
                <a href="#">
                Edit
                </a>
            </td>
		</tr>
    </c:forEach>
</table>


<%@ include file="footer.jsp" %>
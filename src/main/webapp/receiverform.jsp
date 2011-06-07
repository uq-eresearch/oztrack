<%@ include file="header.jsp" %>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Update Receiver Details</h2>

<form:form commandName="receiverDeployment" method="POST" name="receiverDeployment">


<div>
<label for="originalId">Receiver Id:</label>
<form:input path="originalId" id="originalId"/>
<form:errors path="originalId" cssClass="formErrors"/>
</div>

<div>
<label for="receiverName">Name:</label>
<form:input path="receiverName" id="receiverName"/>
<form:errors path="receiverName" cssClass="formErrors"/>
</div>

<div>
<label for="receiverDescription">Description:</label>
<form:input path="receiverDescription" id="receiverDescription"/>
<form:errors path="receiverDescription" cssClass="formErrors"/>
</div>


<br>
<div align="center"><input type="submit" value="Update"/></div>

</form:form>








<%@ include file="footer.jsp" %>

<%@ include file="header.jsp" %>

<c:choose>
<c:when test="${param.update}">
<h1>Edit User Profile</h1>
<c:set var="buttonText" value="Update"/>
</c:when>
<c:otherwise>
<h1>Register</h1>
<c:set var="buttonText" value="Register"/>
</c:otherwise>
</c:choose>

<form:form commandName="user" method="POST" name="user">

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Username:</b><br> This will be the name that you log on to OzTrack with.
	</span></a>
	</div>
	
<div>
<label for="username">Username:</label>
<form:input path="username" id="username"/><br>
<form:errors path="username" cssClass="formErrors"/>
</div>

<div>
<label for="password">Password:</label>
<form:password path="password" id="password"/>
<form:errors path="password" cssClass="formErrors"/>
</div>

<br/>

<div>
<label for="title">Title:</label>
<form:select path="title">
    <form:option value="none">--- Select ---</form:option>
    <form:option value="Dr">Dr</form:option>
    <form:option value="A/Prof">A/Prof</form:option>
    <form:option value="Prof">Prof</form:option>
    <form:option value="Mr">Mr</form:option>
    <form:option value="Ms">Ms</form:option>
    <form:option value="none">None</form:option>
</form:select>
</div>

<div>
<label for="firstname">First Name:</label>
<form:input path="firstName" id="firstname"/>
<form:errors path="firstName" cssClass="formErrors"/>
</div>

<div>
<label for="lastname">Last Name:</label>
<form:input path="lastName" id="lastname"/>
<form:errors path="lastName" cssClass="formErrors"/>
</div>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Description:</b><br> This field is important when project metadata is syndicated to DataSpace and ANDS.
	See examples at http://dataspace.uq.edu.au/agents.
	</span></a>
	</div>

<div>
<label for="dataSpaceAgentDescription">Description:</label>
<form:input path="dataSpaceAgentDescription" id="dataSpaceAgentDescription"/>
<form:errors path="dataSpaceAgentDescription" cssClass="formErrors"/>
</div>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Organisation:</b><br> Please give the name of the organisation in full. This field is important when project metadata is syndicated to DataSpace and ANDS.
	</span></a>
	</div>

<div>
<label for="organisation">Organisation:</label>
<form:input path="organisation" id="organisation"/>
<form:errors path="organisation" cssClass="formErrors"/>
</div>

<div>
<label for="email">Email:</label>
<form:input path="email" id="email"/>
<form:errors path="email" cssClass="formErrors"/>
</div>

<div>
<label> &nbsp;</label>
<div class="formButton"><input type="submit" value="<c:out value="${buttonText}"/>"/></div>
</div>

</form:form>


<%@ include file="footer.jsp" %>
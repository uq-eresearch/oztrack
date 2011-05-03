<%@ include file="header.jsp" %>


<form:form commandName="user" method="POST" name="login">

<fieldset>
<legend>Login</legend>


<div>
<label for="username">Username:</label>
<form:input path="username" id="username"/>
<form:errors path="username" cssClass="formErrors"/>
</div>

<div>
<label for="password">Password:</label>
<form:password path="password" id="password"/>
<form:errors path="password" cssClass="formErrors"/>
</div>

<div align="center">
<input type="submit" value="Login"/>
</div>

</fieldset>
</form:form>





<%@ include file="footer.jsp" %>
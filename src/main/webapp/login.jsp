<%@ include file="header.jsp" %>
<input id="titleText" type="hidden" value="Login"/>

<h1>Login</h1>

<div class="errorMessage"><c:out value="${errorMessage}"/></div>

<form:form commandName="user" method="POST" name="login">

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

<div>
<label></label>
<div class="formButton"><input type="submit" value="Login"/></div>
</div>

<div><label></label><a href="<c:url value="register"/>">Register as a new user</a>
</div>
</form:form>



<%@ include file="footer.jsp" %>


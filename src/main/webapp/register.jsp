<%@ include file="header.jsp" %>

<h1>Register</h1>

<form:form commandName="user" method="POST" name="user">

<table border="0">

<tr>
<td>Username:</td>
<td><form:input path="username"/></td>
<td><font color="red"><form:errors path="username"/></font></td>
</tr>

<tr>
<td>First Name:</td>
<td><form:input path="firstName"/></td>
<td><font color="red"><form:errors path="firstName"/></font></td>
</tr>

<tr>
<td>Last Name:</td>
<td><form:input path="lastName"/></td>
<td><font color="red"><form:errors path="lastName"/></font></td>
</tr>

<tr>
<td>Password:</td>
<td><form:password path="password"/></td>
<td><font color="red"><form:errors path="password"/></font></td>
</tr>


<tr>
<td>Email:</td>
<td><form:input path="email"/></td>
<td><font color="red"><form:errors path="email"/></font></td>
</tr>


<tr>
<td colspan="3" align="center"><input type="submit" value="Register"/></td>
</tr>

</table>

</form:form>


<%@ include file="footer.jsp" %>
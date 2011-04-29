<%@ include file="header.jsp" %>


<h1>Login</h1>
<form:form commandName="user" method="POST" name="login">

<table border="0">

<tr>
<td>Username:</td>
<td><form:input path="username"/></td>
<td><font color="red"><form:errors path="username"/></font></td>
</tr>

<tr>
<td>Password:</td>
<td><form:password path="password"/></td>
<td><font color="red"><form:errors path="password"/></font></td>
</tr>

<tr>
<td colspan="3" align="center"><input type="submit" value="Login"/></td>
</tr>

</table>

</form:form>




<%@ include file="footer.jsp" %>
<%@ include file="header.jsp" %>

<h3><br/>User Registered Successfully.</h3><br/>

<table>

<tr>
<td colspan="2" align="center"><font size="5">User Information</font></td>
</tr>

<tr>
<td>Username:</td>
<td><c:out value="${user.username}"/></td>
</tr>

<tr>
<td>Full Name:</td>
<td><c:out value="${user.firstName} ${user.lastName}"/></td>
</tr>

<tr>
<td>Email:</td>
<td><c:out value="${user.email}"/></td>
</tr>

</table>



</center>

<p><a href="<c:url value="login"/>">Proceed to Login</a>
</p>

<%@ include file="footer.jsp" %>
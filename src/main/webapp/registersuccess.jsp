<%@ include file="header.jsp" %>

<h2>Registration successful.</h2>

<table id="registrationTable">

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
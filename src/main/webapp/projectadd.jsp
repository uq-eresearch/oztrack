<%@ include file="header.jsp" %>

<h1>Add a Project</h1>

<form:form commandName="project" method="POST" name="project">

<table border="0">

<tr>
<td>Title</td>
<td><form:input path="title"/></td>
<td><font color="red"><form:errors path="title"/></font></td>
</tr>

<tr>
<td>Description</td>
<td><form:input path="description"/></td>
<td><font color="red"><form:errors path="description"/></font></td>
</tr>

<tr>
<td>Organisation Name:</td>
<td><form:input path="organisationName"/></td>
<td><font color="red"><form:errors path="organisationName"/></font></td>
</tr>

<tr>
<td>Custodian Name:</td>
<td><form:input path="custodianName"/></td>
<td><font color="red"><form:errors path="custodianName"/></font></td>
</tr>

<tr>
<td colspan="3" align="center"><input type="submit" value="Create OzTrack Project"/></td>
</tr>

</table>

</form:form>



<%@ include file="footer.jsp" %>
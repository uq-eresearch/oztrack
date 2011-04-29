<%@ include file="header.jsp" %>

<h1>Add a Data File</h1>
<hr/>


<form:form commandName="dataFile" method="POST" enctype="multipart/form-data">

<table border="0">

<tr>
<td>File Name</td>
<td><form:input path="userGivenFileName"/></td>
<td><font color="red"><form:errors path="userGivenFileName"/></font></td>
</tr>

<tr>
<td>Description</td>
<td><form:input path="fileDescription"/></td>
<td><font color="red"><form:errors path="fileDescription"/></font></td>
</tr>

<tr>
<td></td>
<td colspan="2">
<input type="file" name="file"/>
</td>
</tr>

<tr>
<td colspan="3" align="center"><input type="submit" value="Add this file to my project"/></td>
</tr>

</table>

</form:form>



<%@ include file="footer.jsp" %>
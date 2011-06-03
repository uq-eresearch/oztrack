<%@ include file="header.jsp" %>


<h1>Add a Data File</h1>

<form:form commandName="dataFile" method="POST" enctype="multipart/form-data">


<p>This file will be added to the project: <b><span id="projectTitle"><c:out value="${projectTitle}"/></span>.</p></b>
<div>
<label for="fileDescription">File Description:</label>
<form:input path="fileDescription" id="fileDescription"/><br>
<form:errors path="fileDescription" cssClass="formErrors"/>
</div>

<div>
<label for="dataFileType">File Type:</label>
 <form:radiobutton path="dataFileType" value="ACOUSTIC" cssClass="radiobutton"/>Acoustic VR100<br>
 <form:radiobutton path="dataFileType" value="SATELLITE" cssClass="radiobutton" />Satellite/Position Fix
<form:errors path="dataFileType" cssClass="formErrors"/>
</div>

<div>
<label for="timeConversion">Convert to local time?</label>
<form:checkbox path="localTimeConversionRequired" id="localTimeConversionRequired" cssClass="checkbox"/>
 &nbsp; Local time is GMT + <form:input path="localTimeConversionHours" cssClass="shortInput"/> hours.
</div>

<div><label for="file">File: </label><input type="file" name="file"/>
</div>

<br>
<div align="center"><input type="submit" value="Add File"/></div>


</form:form>



<%@ include file="footer.jsp" %>
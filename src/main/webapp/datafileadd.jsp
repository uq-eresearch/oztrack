<%@ include file="header.jsp" %>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Add a Data File</h2>

<form:form commandName="dataFile" method="POST" enctype="multipart/form-data">


<div>
<label for="fileDescription">File Description:</label>
<form:input path="fileDescription" id="fileDescription"/><br>
<form:errors path="fileDescription" cssClass="formErrors"/>
</div>

<div>
<label for="timeConversion">Convert to local time?</label>
<form:checkbox path="localTimeConversionRequired" id="localTimeConversionRequired" cssClass="checkbox"/>
 &nbsp; Local time is GMT + <form:input path="localTimeConversionHours" cssClass="shortInput"/> hours.
</div>

<div class="checkboxDiv">
<form:checkbox cssClass="checkbox" path="singleAnimalInFile" id="singleAnimalInFile"/>
This file contains data for a single tagged animal only.
</div>


<div><label for="file">File: </label><input type="file" name="file"/>
</div>

<br>
<div align="center"><input type="submit" value="Add File"/></div>


</form:form>



<%@ include file="footer.jsp" %>
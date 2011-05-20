<%@ include file="header.jsp" %>

<form:form commandName="dataFile" method="POST" enctype="multipart/form-data">

<span class="formHeader">Add a Data File</span>

<div>
<label for="fileDescription">File Description:</label>
<form:input path="fileDescription" id="fileDescription"/>
<form:errors path="fileDescription" cssClass="formErrors"/>
</div>

<div>
<label for="userGivenFileName">File Name:</label>
<form:input path="userGivenFileName" id="userGivenFileName"/>
<form:errors path="userGivenFileName" cssClass="formErrors"/>
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
<div align="center"><input type="submit" value="Add this file to my project"/></div>

</fieldset>
</form:form>



<%@ include file="footer.jsp" %>
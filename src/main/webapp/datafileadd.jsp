<%@ include file="header.jsp" %>
<script type="text/javascript">projectPage = true;</script>

<h1 id="projectTitle"><c:out value="${project.title}"/></h1>
<h2>Add a Data File</h2>

<p>This form allows you to upload animal tracking data in csv format. The uploaded data will be added to the dataset
available for visualisations and analysis for your project.
</p>

<form:form commandName="dataFile" method="POST" enctype="multipart/form-data">

<div class="help">
<a class=info href="#"><img src="images/help.png" border="0">
<span><b>DataFile Type:</b> Determined by the Project Type (specified on the creation of the project).
</span></a>
</div>

<div>
<label for="projectType">Datafile Type:</label>
<span><c:out value="${project.projectType.displayName}"/></span>
</div>

<br>

<div class="help">
<a class=info href="#"><img src="images/help.png" border="0">
<span><b>File Description:</b> A short description to help you identify the contents of the file.<br>
</span></a>
</div>

<div>
<label for="fileDescription">File Description:</label>
<form:input path="fileDescription" id="fileDescription"/>
<br>
<form:errors path="fileDescription" cssClass="formErrors"/>
</div>

<br>

<div class="help">
<a class=info href="#"><img src="images/help.png" border="0">
<span><b>Time Conversion:</b> Specify a time conversion value to apply to the timestamps in your file.<br>
</span></a>
</div>

<div>
<label for="timeConversion">Convert to local time?</label>
<form:checkbox path="localTimeConversionRequired" id="localTimeConversionRequired" cssClass="checkbox"/>
 &nbsp; Local time is GMT + <form:input path="localTimeConversionHours" cssClass="shortInput"/> hours.
</div>

<br>

<div class="checkboxDiv">
<form:checkbox cssClass="checkbox" path="singleAnimalInFile" id="singleAnimalInFile"/>
This file contains data for a single tagged animal only.
</div>

<br>

<div><label for="file">File: </label><input type="file" name="file"/>
</div>

<br>
<div align="center"><input type="submit" value="Add File"/></div>


</form:form>

<h2>Uploading Files</h2>
<ul>
<li><span>All files must be CSV - comma separated only.</span></li>
<li><span>Date formats that can be read:
    <ul>
        <li><span>dd/MM/yyyy H:mi:s.S</span></li>
        <li><span>dd.MM.yyyy H:mi:s.S</span></li>
    </ul>
    </span>
</li>
<li><span>Decimal Lat/Longs only.</span></li>
</ul>


<%@ include file="footer.jsp" %>
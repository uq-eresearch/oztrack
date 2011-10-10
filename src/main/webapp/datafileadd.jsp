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
	<span><b>DataFile Type:</b><br>Determined by the Project Type (specified on the creation of the project).
	</span></a>
	</div>

<div>
<label for="projectType">Datafile Type:</label>
<span><c:out value="${project.projectType.displayName}"/></span>
</div>

<br>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>File Description:</b><br>A short description to help you identify the contents of the file.<br>
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
	<span><b>Time Conversion:</b><br>Specify a time conversion value to apply to the timestamps in your file.<br>
	</span></a>
	</div>
	
<div>
<label for="timeConversion">Convert to local time?</label>
<form:checkbox path="localTimeConversionRequired" id="localTimeConversionRequired" cssClass="checkbox"/>
&nbsp; Local time is GMT + <form:input path="localTimeConversionHours" cssClass="shortInput"/> hours.
</div>

<br>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Important!</b><br>The file loader assumes that there is an Id field in the data which will identify an animal.
							   If this isn't the case, checking this box will create a new animal in the database and allocate
							   the data in this file to the new animal.<br>
	</span></a>
	</div>

<div class="checkboxDiv">
<form:checkbox cssClass="checkbox" path="singleAnimalInFile" id="singleAnimalInFile"/>
This file contains data for a single tagged animal only.
</div>


<br>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Uploading the File:</b><br>See below for details on the required file format.
	</span></a>
	</div>
<div>
<label for="file">File: </label><input type="file" name="file"/>
</div>

<br>
<div>
<label></label>
<div class="formButton"><input type="submit" value="Add File"/></div>
</div>

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
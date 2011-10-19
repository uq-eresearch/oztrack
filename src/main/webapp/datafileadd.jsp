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

<!--
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
 -->

<br>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Uploading the File:</b><br>See below for details on the required file format.
	</span></a>
	</div>
<div>
<label for="file">File: </label>
<input type="file" name="file"/>
<form:errors path="file" cssClass="formErrors"/>
</div>

<br>
<div>
<label></label>
<div class="formButton"><input type="submit" value="Add File"/></div>
</div>

</form:form>

<h2>Uploading Files</h2>
<ul>
<li>Format
	<p>All files must be CSV - comma separated only.</p>
	<p>OzTrack expects that files will contain particular headers, depending on the type of project specified when the project was created. 
	Because this project is for <b><c:out value="${project.projectType.displayName}"/></b>, the headers in the file can be any of the 
	following (spaces and non alphanumeric characters are ignored): <br><br>
	<c:out value="${fileHeaders}"/>
	
		
</li>
<li>Dates
	<p>Date formats that can be read:</p>
	    <ul>
	        <li><span>dd/MM/yyyy H:mi:s.S</span></li>
	        <li><span>dd.MM.yyyy H:mi:s.S</span></li>
	    </ul>
	 <br>
</li>
<li>Spatial Coordinates
	<p>At this stage we accept only decimal Lat/Longs in projection EPSG:4326.</p>
</li>
<li>Animals: 
	<p>If there <b>is</b> an ID or ANIMAL Id field in the file, then OzTrack will assume that this field is the identifier of the animals.
	However, OzTrack only allows a maximum of 20 animals in a file upload. So if there are more, the file upload will Fail and the user asked to take action.
	<br>
	If there is <b>no </b> ID or Animal Id field in the file, OzTrack will assume that the file pertains to a single animal and will create an ID for it. You can add the details for the animal later.
</p></li>
</ul>


<%@ include file="footer.jsp" %>
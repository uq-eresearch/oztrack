<%@ include file="header.jsp" %>

<h1>Create a New Project</h1>

<p>As a condition of use of OzTrack, the information collected in this form is registered with the
Australian National Data Service, ANDS.</p>
<form:form commandName="project" method="POST" name="project"  enctype="multipart/form-data">

<div class="formSubheader">Project Metadata</div>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Title:</b> A short title (less than 50 characters if possible) to identify your project in OzTrack.
	</span></a>
	</div>
	
<div>
<label for="title">*Title:</label>
<form:textarea path="title" rows="1" cols="40" id="title"/>
<form:errors path="title" cssClass="formErrors"/>
</div>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Description:</b><br> Required by ANDS. 
	</span></a>
	</div>

<div>
<label for="description">*Description:</label>
<form:textarea path="description" rows="5" cols="40" id="description"/>
<form:errors path="description" cssClass="formErrors"/>
</div>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Project Type:</b><br> What kind of telemetry device was used to track your animal? This 
								   will determine the format of the datafiles you upload, and the 
								   types of analysis available to this project's dataset. 
	</span></a>
	</div>

<div>
<label for="projectType">Project Type:</label>

<form:select path="projectType">
    <form:option value="GPS">GPS Based Telemetry</form:option>
    <form:option value="PASSIVE_ACOUSTIC">Passive Acoustic Telemetry</form:option>
    <form:option value="ACTIVE_ACOUSTIC">Active Acoustic Telemetry</form:option>
    <form:option value="ARGOS">ARGOS Telemetry</form:option>
    <form:option value="RADIO">Radio Telemetry</form:option>
</form:select>
</div>

	<div class="help">
	<a class=info href="#"><img src="images/help.png" border="0">
	<span><b>Location Description:</b><br> The general area of the study, eg. country, state, town. 
	</span></a>
	</div>
	
<div>
<label for="spatialCoverageDescr">Location Description:</label>
<form:input path="spatialCoverageDescr" id="spatialCoverageDescr"/>
<form:errors path="spatialCoverageDescr" cssClass="formErrors"/>
</div>

<div>
<label for="temporalCoverageDescr">Temporal Description:</label>
<form:input path="temporalCoverageDescr" id="temporalCoverageDescr"/>
<form:errors path="temporalCoverageDescr" cssClass="formErrors"/>
</div>

<div class="checkboxDiv">
<form:checkbox cssClass="checkbox" path="isGlobal" id="isGlobal"/>
The data in this project is to be publically available via OzTrack.
</div>

<div class="formSubheader">Species</div>

<div>
<label for="speciesCommonName">Common Name:</label>
<form:input path="speciesCommonName" id="speciesCommonName"/>
<form:errors path="speciesCommonName" cssClass="formErrors"/>
</div>

<div>
<label for="speciesScientificName">Scientific Name:</label>
<form:input path="speciesScientificName" id="speciesScientificName"/>
<form:errors path="speciesScientificName" cssClass="formErrors"/>
</div>

<div class="formSubheader">Data Contact</div>

<div>
<label for="contactName">Name:</label>
<form:input path="contactName" id="contactName"/>
<form:errors path="contactName" cssClass="formErrors"/>
</div>

<div>
<label for="contactOrganisation">Organisation:</label>
<form:input path="contactOrganisation" id="contactOrganisation"/>
<form:errors path="contactOrganisation" cssClass="formErrors"/>
</div>


<div>
<label for="contactEmail">Email:</label>
<form:input path="contactEmail" id="contactEmail"/>
<form:errors path="contactEmail" cssClass="formErrors"/>
</div>

<div>
<label for="contactUrl">URL:</label>
<form:input path="contactUrl" id="contactUrl"/>
<form:errors path="contactUrl" cssClass="formErrors"/>
</div>

<div class="formSubheader">Data Custodian</div>

<div>
<label for="custodianname">Custodian Name:</label>
<form:input path="custodianName" id="custodianname"/>
<form:errors path="custodianName" cssClass="formErrors"/>
</div>

<div>
<label for="custodianOrganisation">Custodian Organisation:</label>
<form:input path="custodianOrganisation" id="custodianOrganisation"/>
<form:errors path="custodianOrganisation" cssClass="formErrors"/>
</div>

<div>
<label for="custodianEmail">Custodian Email:</label>
<form:input path="custodianEmail" id="custodianEmail"/>
<form:errors path="custodianEmail" cssClass="formErrors"/>
</div>

<div>
<label for="custodianUrl">Custodian URL:</label>
<form:input path="custodianUrl" id="custodianUrl"/>
<form:errors path="custodianUrl" cssClass="formErrors"/>
</div>

<div class="formSubheader">Image</div>

<div>
<label for="imageFile">Upload an Image file:</label>
<input type="file" name="imageFile"/><br><br>
<form:errors path="imageFile" cssClass="formErrors"/>
</div>

<div class="formSubheader">Publications</div>

<div>
<label for="publicationTitle">Publication Title:</label>
<form:textarea path="publicationTitle" rows="2" cols="40" id="publicationTitle"/>
<form:errors path="publicationTitle" cssClass="formErrors"/>
</div>

<div>
<label for="publicationUrl">Publication URL:</label>
<form:input path="publicationUrl" id="publicationUrl"/>
<form:errors path="publicationUrl" cssClass="formErrors"/>
</div>

<div>
<label></label>
<div class="formButton"><input type="submit" value="Create OzTrack Project"/></div>
</div>

</table>

</fieldset>
</form:form>



<%@ include file="footer.jsp" %>
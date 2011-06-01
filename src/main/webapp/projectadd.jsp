<%@ include file="header.jsp" %>

<h1>Add a Project</h1>

<form:form commandName="project" method="POST" name="project">


<div>
<label for="title">*Title:</label>
<form:textarea path="title" rows="1" cols="40" id="title"/>
<form:errors path="title" cssClass="formErrors"/>
</div>

<div>
<label for="description">*Description:</label>
<form:textarea path="description" rows="5" cols="40" id="description"/>
<form:errors path="description" cssClass="formErrors"/>
</div>

<div>
<label for="spatialCoverageDescr">Spatial Coverage:</label>
<form:input path="spatialCoverageDescr" id="spatialCoverageDescr"/>
<form:errors path="spatialCoverageDescr" cssClass="formErrors"/>
</div>

<div>
<label for="temporalCoverageDescr">Temporal Coverage:</label>
<form:input path="temporalCoverageDescr" id="temporalCoverageDescr"/>
<form:errors path="temporalCoverageDescr" cssClass="formErrors"/>
</div>

<div class="checkboxDiv">
<form:checkbox cssClass="checkbox" path="isGlobal" id="isGlobal"/>
The data in this project is to be publically available via OzTrack.
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

<div class="formButton"><input type="submit" value="Create OzTrack Project"/></div>

</table>

</fieldset>
</form:form>



<%@ include file="footer.jsp" %>
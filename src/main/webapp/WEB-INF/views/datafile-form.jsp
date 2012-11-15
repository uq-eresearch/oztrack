<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Add a Data File">
    <jsp:attribute name="head">
        <style type="text/css">
            pre.datafile {
                width: 600px;
                background: white;
            }
        </style>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuUploads').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Animal Tracking</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles">Data Uploads</a>
        &rsaquo; <span class="active">Add a Data File</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>

        <p>
            This form allows you to upload animal tracking data in CSV format.
        </p>

        <form:form cssClass="form-horizontal form-bordered" action="/projects/${project.id}/datafiles" commandName="dataFile" method="POST" enctype="multipart/form-data">
            <fieldset>
                <div class="legend">Add a Data File</div>
                <div class="control-group">
                    <label class="control-label" for="projectType">Data File Type</label>
                    <div class="controls">
                        <input type="text" disabled="disabled" value="<c:out value="${project.projectType.displayName}"/>" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="fileDescription">File Description</label>
                    <div class="controls">
                        <form:input path="fileDescription" id="fileDescription" cssClass="input-xlarge"/>
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="${pageContext.request.contextPath}/img/help.png" border="0">
                                <span>
                                    <b>File Description</b><br>
                                    <br>
                                    A short description to help you identify the contents of the file.
                                </span>
                            </a>
                        </div>
                        <form:errors path="fileDescription" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="timeConversion">Convert to local time?</label>
                    <div class="controls">
                        <form:checkbox path="localTimeConversionRequired" id="localTimeConversionRequired" cssClass="checkbox"/>
                        Local time is GMT +<form:input path="localTimeConversionHours" cssClass="input-mini" cssStyle="width: 30px;"/> hours.
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="${pageContext.request.contextPath}/img/help.png" border="0">
                                <span>
                                    <b>Time Conversion</b><br>
                                    <br>
                                    Specify a time conversion value to apply to the timestamps in your file.
                                </span>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="file">File: </label>
                    <div class="controls">
                        <input type="file" name="file"/>
                        <div class="help-inline">
                            <a class=info href="#">
                                <img src="${pageContext.request.contextPath}/img/help.png" border="0">
                                <span>
                                    <b>Uploading the File:</b><br>
                                    <br>
                                    See below for details on the required file format.
                                </span>
                            </a>
                        </div>
                        <form:errors path="file" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Add File"/>
                <a class="btn" href="${pageContext.request.contextPath}/projects/${project.id}/datafiles">Cancel</a>
            </div>
        </form:form>

        <p style="margin-top: 1.00em; margin-bottom: 0.33em;">
            <b>Note: all files must be CSV (comma-separated values) only.</b>
        </p>
        <p style="margin-top: 0.33em; margin-bottom: 1.00em;">
            If you are uploading data from an Excel spreadsheet, please save as .csv before proceeding.
        </p>

        <h2>Example data files</h2>

        <p>Data file containing a single animal:</p>
<pre class="datafile">
date,time,latitude,longitude
30/03/2010,5:46:35,-17.557141,146.089866
30/03/2010,6:46:35,-17.557291,146.089891
31/03/2010,21:47:53,-17.558633,146.089075
1/04/2010,1:47:05,-17.558375,146.0878
2/04/2010,22:17:23,-17.559016,146.087858
</pre>

        <p>Data file containing several animals (note the extra <tt>ANIMALID</tt> column):</p>
<pre class="datafile">
ANIMALID,DATE,LONGITUDE,LATITUDE
Ernie,5/08/2009 11:16:00,142.17893,-12.38277
Ernie,11/08/2009 20:56:00,142.17896,-12.38248
Ernie,12/08/2009 5:56:00,142.10926,-12.31637
Bert,5/08/2009 11:16:00,142.17888,-12.38272
Bert,11/08/2009 20:56:00,142.17881,-12.3824
Bert,12/08/2009 2:55:00,142.10619,-12.32208
</pre>

        <h2>File format</h2>
        <p>OzTrack expects that files will contain particular headers, depending on the type of project specified when the project was created.
        Because this project is for <b><c:out value="${project.projectType.displayName}"/></b>, the headers in the file can be any of the
        following:<br>
        <br>
        <c:out value="${fileHeaders}"/>
        <h3>Date formats</h3>
        <p>Date formats that can be read:</p>
            <ul>
                <li><span>dd/MM/yyyy H:mi:s.S</span></li>
                <li><span>dd/MM/yyyy H:mi:s</span></li>
                <li><span>dd/MM/yyyy H:mi</span></li>
                <li><span>dd/MM/yyyy</span></li>
                <li><span>dd.MM.yyyy H:mi:s.S</span></li>
                <li><span>dd.MM.yyyy H:mi:s</span></li>
                <li><span>dd.MM.yyyy H:mi</span></li>
                <li><span>dd.MM.yyyy</span></li>
                <li><span>yyyy.MM.dd H:mi:s.S</span></li>
                <li><span>yyyy.MM.dd H:mi:s</span></li>
                <li><span>yyyy.MM.dd H:mi</span></li>
                <li><span>yyyy.MM.dd</span></li>
            </ul>

            <table class="table table-bordered table-condensed">
                <thead>
                <tr><th>Field</th>
                    <th>Description</th>
                    <th>Example</th></tr>
                </thead>
                <tbody>
                <tr><td>dd</td>
                    <td>day in month (number)</td>
                    <td>01, 1, 31, 08</td></tr>

                <tr><td>MM</td>
                    <td>month (number)</td>
                    <td>01, 1, 12, 6, 06</td></tr>

                <tr><td>yyyy</td>
                    <td>year (4 digit number)</td>
                    <td>1997, 2011</td></tr>

                <tr><td>H</td>
                    <td>hour in day (0-23)</td>
                    <td>00, 23, 16</td></tr>

                <tr><td>mi</td>
                    <td>minute in hour (0-60)</td>
                    <td>00, 01, 1, 58</td></tr>

                <tr><td>s</td>
                    <td>second in hour (0-60)</td>
                    <td>00, 01, 1, 58</td></tr>

                <tr><td>S</td>
                    <td>millisecond</td>
                    <td>00, 01234, 1234</td></tr>
                </tbody>
            </table>
        <p>Note: OzTrack will look for the headings specified above to populate the date and time fields. The date fields above (including <tt>ACQUIISITIONTIME</tt>) can contain either
           a date or a date and time stamp. The time stamp can be in a separate field to the date, but the date field must precede it (left to right). </p>
        <h3>Spatial coordinates</h3>
        <p>At this stage we only accept Lat/Longs in the WGS 84 coordinate system.</p>
        <p>Coordinate formats that can be read:</p>
        <ul>
            <li>Degrees (e.g., 153.017433)</li>
            <li>Degrees minutes (e.g., 153 1.046)</li>
            <li>Degrees minutes seconds (e.g., 153 1 2.76)</li>
        </ul>
        <h3>Animal IDs</h3>
            <p>
                If there is an <tt>ID</tt> or <tt>ANIMALID</tt> field in the file,
                OzTrack will assume that this field is the identifier of the animals.
            </p>
            <p>
                If there is no <tt>ID</tt> or <tt>ANIMALID</tt> field in the file,
                OzTrack will assume that the file pertains to a single animal and will automatically generate an ID for it.
                You can add the details for the animal later.
            </p>
        </p>
    </jsp:body>
</tags:page>

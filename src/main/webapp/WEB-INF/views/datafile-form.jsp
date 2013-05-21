<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Add Data File">
    <jsp:attribute name="description">
        Upload a new data file to the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="head">
        <style type="text/css">
            .datafile {
                background-color: white;
            }
            pre.datafile {
                border: 1px solid #aaaaaa;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#dataActionsCreateFile').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}/datafiles">Data Files</a>
        &rsaquo; <span class="active">Upload Data File</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
        <tags:data-actions project="${project}"/>
        <tags:project-licence project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Upload data file</h2>

        <form:form cssClass="form-horizontal form-bordered" action="/projects/${project.id}/datafiles" commandName="dataFile" method="POST" enctype="multipart/form-data">
            <fieldset>
                <div class="legend">Data file</div>
                <p style="margin: 18px 0;">
                    Uploaded files must be in
                    <a target="_blank" href="http://en.wikipedia.org/wiki/Comma-separated_values">CSV</a> or
                    <a target="_blank" href="http://en.wikipedia.org/wiki/Microsoft_Excel">Excel</a> format.
                    See below for details on required columns.
                </p>
                <div class="control-group">
                    <label class="control-label" for="file">File: </label>
                    <div class="controls">
                        <input type="file" name="file"/>
                        <form:errors path="file" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="fileDescription">File Description</label>
                    <div class="controls">
                        <form:input path="fileDescription" id="fileDescription" cssClass="input-xlarge"/>
                        <div class="help-inline">
                            <div class="help-popover" title="File Description">
                                A short description to help you identify the contents of the file.
                            </div>
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
                            <div class="help-popover" title="Time Conversion">
                                Specify a time conversion value to apply to the timestamps in your file.
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Upload File"/>
                <a class="btn" href="${pageContext.request.contextPath}/projects/${project.id}/datafiles">Cancel</a>
            </div>
        </form:form>

        <h2>Example data files</h2>

        <p>CSV file containing a single animal, with separate columns for date and time:</p>
<pre class="datafile">
DATE,TIME,LATITUDE,LONGITUDE
30/03/2010,5:46:35,-17.557141,146.089866
30/03/2010,6:46:35,-17.557291,146.089891
31/03/2010,21:47:53,-17.558633,146.089075
1/04/2010,1:47:05,-17.558375,146.0878
2/04/2010,22:17:23,-17.559016,146.087858
</pre>

        <p>Excel file containing several animals (note the extra <tt>ANIMALID</tt> column - and optional <tt>ARGOSCLASS</tt> values):</p>
        <table class="table table-bordered table-condensed datafile">
        <tr><th>ANIMALID</th><th>DATE</th><th>LONGITUDE</th><th>LATITUDE</th><th>ARGOSCLASS</th></tr>
        <tr><td>Ernie</td><td>5/08/2009 11:16:00</td><td>142.17893</td><td>-12.38277</td><td>B</td></tr>
        <tr><td>Ernie</td><td>11/08/2009 20:56:00</td><td>142.17896</td><td>-12.38248</td><td>A</td></tr>
        <tr><td>Ernie</td><td>12/08/2009 5:56:00</td><td>142.10926</td><td>-12.31637</td><td>2</td></tr>
        <tr><td>Bert</td><td>5/08/2009 11:16:00</td><td>142.17888</td><td>-12.38272</td><td>3</td></tr>
        <tr><td>Bert</td><td>11/08/2009 20:56:00</td><td>142.17881</td><td>-12.3824</td><td>0</td></tr>
        <tr><td>Bert</td><td>12/08/2009 2:55:00</td><td>142.10619</td><td>-12.32208</td><td>1</td></tr>
        </table>

        <h2>File format</h2>

        <h3>Columns</h3>
        <p>
            OzTrack accepts CSV and Excel files containing the following headers:<br>
        </p>
        <ul>
            <li><tt>DATE</tt>, <tt>UTCDATE</tt>, <tt>LOCDATE</tt>, or <tt>ACQUISITIONTIME</tt>: date or date/time value (required)</li>
            <li><tt>TIME</tt> or <tt>UTCTIME</tt>: time value, if not included in date column (optional)</li>
            <li><tt>LATITUDE</tt>, <tt>LATT</tt>, or <tt>LAT</tt>: WGS84 latitude in decimal degrees (required)</li>
            <li><tt>LONGITUDE</tt>, <tt>LONG</tt>, or <tt>LON</tt>: WGS84 longitude in decimal degrees (required)</li>
            <li><tt>ID</tt> or <tt>ANIMALID</tt>: alphanumeric ID for animal, if file contains several animals (optional)</li>
            <li><tt>ARGOSCLASS</tt>: Argos location class (${fn:join(argosClassCodes, ', ')}) (optional)</li>
        </ul>

        <h3>Date and time formats</h3>
        <p>Date formats that can be read:</p>
        <ul>
            <li><span>dd/MM/yyyy</span></li>
            <li><span>dd.MM.yyyy</span></li>
            <li><span>yyyy.MM.dd</span></li>
            <li><span>yyyy-MM-dd</span></li>
        </ul>

        <p>Time formats that can be read:</p>
        <ul>
            <li><span>H:mi:s.S</span></li>
            <li><span>H:mi:s</span></li>
            <li><span>H:mi</span></li>
        </ul>

        <p>Date/time values can be combined by putting a space between the date and time, e.g.:</p>
        <ul>
            <li><span>dd/MM/yyyy H:mi</span></li>
            <li><span>dd.MM.yyyy H:mi:s.S</span></li>
            <li><span>yyyy.MM.dd H:mi:s</span></li>
            <li><span>yyyy-MM-dd H:mi:s.S</span></li>
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
        <p>
            Note: OzTrack will look for the headings specified above to populate the date and time fields.
            The date fields above (including <tt>ACQUIISITIONTIME</tt>) can contain either a date or a date
            and time stamp. The time stamp can be in a separate field to the date, but the date field must
            precede it (left to right).
        </p>

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
    </jsp:body>
</tags:page>

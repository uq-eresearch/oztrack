<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
<head>
    <title>OzTrack</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
</head>
<body>
<h1 align="center">OzTrack</h1>
<hr/>



<center>

<h3><br/>Data File Detail</h3><br/>

<table>

<tr>
<td>File Provided:</td>
<td><c:out value="${dataFile.userGivenFileName}"/></td>
</tr>

<tr>
<td>Description:</td>
<td><c:out value="${dataFile.fileDescription}"/></td>
</tr>

<tr>
<td>Content Type:</td>
<td><c:out value="${dataFile.contentType}"/></td>
</tr>

<tr>
<td>Upload Date:</td>
<td><c:out value="${dataFile.uploadDate}"/> by <c:out value="${dataFile.uploadUser}"/></td>
</tr>


<tr>
<td>Number Detections:</td>
<td><c:out value="${numberDetections}"/></td>
</tr>

</table>


<table>
<c:forEach items="${rawAcousticDetectionsList}" var="detection">
        <tr>
            <td><c:out value="${detection.datetime}"/></td>
            <td><c:out value="${detection.animalid}"/></td>
            <td><c:out value="${detection.sensor1}"/></td>
            <td><c:out value="${detection.units1}"/></td>
            <td><c:out value="${detection.receiverid}"/></td>
		</tr>
</c:forEach>
</table>

</center>


<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
</body>
</html>
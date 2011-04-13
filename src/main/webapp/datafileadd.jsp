<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>OzTrack</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
</head>
<body>
<h1 align="center">OzTrack </h1>
<hr/>


<form:form commandName="dataFile" method="POST" enctype="multipart/form-data">

<table border="0">

<tr>
<td>File Name</td>
<td><form:input path="userGivenFileName"/></td>
<td><font color="red"><form:errors path="userGivenFileName"/></font></td>
</tr>

<tr>
<td>Description</td>
<td><form:input path="fileDescription"/></td>
<td><font color="red"><form:errors path="fileDescription"/></font></td>
</tr>

<tr>
<td>File Type:</td>
<td><form:input path="fileType"/></td>
<td><font color="red"><form:errors path="fileType"/></font></td>
</tr>

<tr>
<td colspan="2">
<input type="file" name="file"/>
</td>
</tr>

<tr>
<td colspan="3" align="center"><input type="submit" value="Add datafile record"/></td>
</tr>

</table>

</form:form>






<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
</body>
</html>
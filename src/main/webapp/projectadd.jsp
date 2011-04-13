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
<h1 align="center">OzTrack</h1>
<hr/>


<form:form commandName="project" method="POST" name="project">

<table border="0">

<tr>
<td>Title</td>
<td><form:input path="title"/></td>
<td><font color="red"><form:errors path="title"/></font></td>
</tr>

<tr>
<td>Description</td>
<td><form:input path="description"/></td>
<td><font color="red"><form:errors path="description"/></font></td>
</tr>

<tr>
<td>Organisation Name:</td>
<td><form:input path="organisationName"/></td>
<td><font color="red"><form:errors path="organisationName"/></font></td>
</tr>

<tr>
<td>Custodian Name:</td>
<td><form:input path="custodianName"/></td>
<td><font color="red"><form:errors path="custodianName"/></font></td>
</tr>

<tr>
<td colspan="3" align="center"><input type="submit" value="Create OzTrack Project"/></td>
</tr>

</table>

</form:form>






<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
</body>
</html>
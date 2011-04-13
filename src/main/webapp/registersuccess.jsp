<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html>
<head>
    <title>Index</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
</head>
<body>
<h1 align="center">OzTrack</h1>
<hr/>



<center>

<h3><br/>User Registered Successfully.</h3><br/>

<table>

<tr>
<td colspan="2" align="center"><font size="5">User Information</font></td>
</tr>

<tr>
<td>Username:</td>
<td><c:out value="${user.username}"/></td>
</tr>

<tr>
<td>Full Name:</td>
<td><c:out value="${user.firstName} ${user.lastName}"/></td>
</tr>

<tr>
<td>Email:</td>
<td><c:out value="${user.email}"/></td>
</tr>

</table>



</center>

<p><a href="<c:url value="login"/>">Proceed to Login</a>
</p>


<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
</body>
</html>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Index</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
</head>
<body>
<h1 align="center">OzTrack Login</h1>
<hr/>
<br/>


<form:form commandName="user" method="POST" name="login">

<table border="0">

<tr>
<td>Username:</td>
<td><form:input path="username"/></td>
<td><font color="red"><form:errors path="username"/></font></td>
</tr>

<tr>
<td>Password:</td>
<td><form:password path="password"/></td>
<td><font color="red"><form:errors path="password"/></font></td>
</tr>

<tr>
<td colspan="3" align="center"><input type="submit" value="Login"/></td>
</tr>

</table>

</form:form>






<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
<c:out value="${1+1}" />
</body>
</html>
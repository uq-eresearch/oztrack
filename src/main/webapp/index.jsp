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
<br/>

<h3>Welcome</h3>
<br/>
<p><a href="<c:url value="login"/>">Login</a> or <a href="<c:url value="register"/>">Register</a></p>
<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
<c:out value="${1+1}" />
</body>
</html>
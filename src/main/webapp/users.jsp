<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
    <title>Users</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
</head>
<body>
<h1 align="center">OzTrack</h1>
<hr/>
<br/>

<h3>List of Users</h3>
<table>
    <tr>
        <th>Name</th>
        <th>Username</th>
        <th>Email</th>
    </tr>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td><c:out value="${user.firstName}"/></td>
            <td><c:out value="${user.username}"/></td>
            <td><c:out value="${user.email}"/></td>
        </tr>
    </c:forEach>
</table>
<br/>
<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
</body>
</html>
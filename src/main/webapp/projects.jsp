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
<h1 align="center">OzTrack</h1>
<hr/>
<p>Welcome, <c:out value="${currentUser.firstName}"/></p>

<h3>Projects<br /></h3>
<p><a href="<c:url value="projectadd"/>">Add a Project</a>
</p>

<table border="0">
    <tr>
        <th>Title</th>
        <th>Contact Name</th>
        <th>Role</th>
    </tr>
    <c:forEach items="${userProjectList}" var="thisUserProject">
        <tr>
            <td><a href="<c:url value="projectdetail"><c:param name="project_id" value="${thisUserProject.pk.project.id}"/></c:url>">
                <c:out value="${thisUserProject.pk.project.title}"/>
                </a></td>
            <td><c:out value="${thisUserProject.pk.project.contactName}"/></td>
            <td><c:out value="${thisUserProject.role}"/></td>
		</tr>
    </c:forEach>
</table>

<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
<c:out value="${1+1}" />
</body>
</html>

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


<h3>Project Details<br /></h3>
<h2>Title:<c:out value="${project.title}"/> | Contact Name: <c:out value="${project.contactName}"/></h2>

<p><a href="<c:url value="datafileadd"><c:param name="project_id" value="${project.id}"/></c:url>">Add a Datafile</a>
</p>

<p><c:out value="${errorStr}"/></p>

<table border="0">

    <tr>
        <th>File Name</th>
        <th>Description</th>
    </tr>

    <c:forEach items="${project.dataFiles}" var="dataFile">
        <tr>
            <td><c:out value="${dataFile.userGivenFileName}"/></td>
            <td>
                <c:choose>
                 <c:when test="${dataFile.status=='NEW'}">
                    <c:out value="${dataFile.fileDescription}"/>
                 </c:when>
                 <c:otherwise>
                    <a href="<c:url value="datafiledetail"><c:param name="datafile_id" value="${dataFile.id}"/></c:url>"><c:out value="${dataFile.fileDescription}"/></a>
                 </c:otherwise>
                </c:choose>
            </td>
            <td><c:out value="${dataFile.status}"/></td>
		</tr>
    </c:forEach>
</table>

<hr/>
<div class="version-footer" align="center"><c:out value="${appTitle}"/> &copy; 2010 ver. (<c:out value="${version}"/>)</div>
<c:out value="${1+1}" />
</body>
</html>

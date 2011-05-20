<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Index</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="css/oztrack.css"/>
    <link rel="stylesheet" type="text/css" href="css/formalize.css"/>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js">;</script>
    <script type="text/javascript" src="js/oztrack.js"></script>
    <script type="text/javascript" src="js/jquery/jquery.formalize.min.js"></script>
    <!--<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>-->
</head>

<body>

<div id="container">
<div id="top_header">
    <img src="images/header_croc.jpg" align="right"/>
</div>


<div id="nav">
<a href="<c:url value=""/>">Home</a>
<a href="<c:url value="searchacoustic"/>">Tracking Portal</a>

<c:choose>
 <c:when test="${currentUser != null}">
    <a href="<c:url value="projects"/>">My Projects</a>
 </c:when>
<c:otherwise>
    <a href="<c:url value="login"/>">Login</a>
</c:otherwise>
</c:choose>

<a href="<c:url value="about"/>">About</a>
<a href="<c:url value="contact"/>">Contact</a>
</div>

<div id="subnav">
 &nbsp;
<div id="crumbs">
    <a id="homeUrl" href="<c:url value=""/>">Home</a>
</div>

<div id="login">
<c:set var="thisURL" value="${pageContext.request.requestURL}"/>
<c:if test="${!fn:contains(thisURL,'login') && !fn:contains(thisURL,'register')}">
    <c:choose>
    <c:when test="${currentUser != null}">
      Welcome, <c:out value="${currentUser.firstName}"/>
      &nbsp;|&nbsp;
      <a href=#>Profile</a>
      &nbsp;|&nbsp;
      <a href="<c:url value="logout"/>">Logout</a>
    </c:when>
    <c:otherwise>
      <a href="<c:url value="login"/>">Login</a> or <a href="<c:url value="register"/>">Register</a>
    </c:otherwise>
    </c:choose>
    <br>
</c:if>
</div>

</div>

<div id="leftMenu">
<c:if test="${fn:contains(thisURL,'project') || fn:contains(thisURL,'dataFile') }">
    <ul>
    <li><a href="<c:url value="projects"/>">My Projects</a></li>
    <li><a href="<c:url value="projectadd"/>">Add a Project</a></li>

    <c:if test="${fn:contains(thisURL,'projectDetail')}">
        <li><a href="<c:url value="datafileadd"/>">Add a Data File</a></li>
    </c:if>
    </ul>
</c:if>
</div>

<div id="main">

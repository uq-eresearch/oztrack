<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="css/oztrack.css"/>
    <link rel="stylesheet" type="text/css" href="css/formalize.css"/>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>

    <title></title>

    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js">;</script>
    <script type="text/javascript" src="js/jquery/jquery.formalize.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
    <script type="text/javascript" src="js/oztrack.js"></script>

<!--
    <c:forEach var="js" items="${paramValues.jsIncludes}" >
        <script src="${js}" type="text/javascript"></script>
    </c:forEach>
-->

</head>

<body>

<div id="container">
<div id="top_header">
    <img src="images/header_croc.jpg" align="right"/>
</div>


<div id="nav">
<ul id="navMenu">

<li><a id="navHome" href="<c:url value="home"/>">Home</a></li>

<c:choose>
 <c:when test="${currentUser != null}">
    <li><a id="navTrack" class="menuParent" href="#">Animal Tracking</a>
        <ul>
            <li><a href="<c:url value="projectadd"/>">Create New Project</a></li>
            <li><a href="<c:url value="projects"/>">Project List</a></li>
        </ul>
    </li>
 </c:when>
<c:otherwise>
    <li><a id="navTrack" href="<c:url value="login"/>">Animal Tracking</a></li>
</c:otherwise>
</c:choose>

<li><a id="navSighting" class="menuParent" href="#">Animal Sightings</a>
        <ul>
            <li><a href="<c:url value="sighting"/>">Report a Sighting</a></li>
            <li><a href="<c:url value="#"/>">Sightings Map</a></li>
        </ul>
</li>
<li><a id="navGallery" href="<c:url value="#"/>">Gallery</a></li>
<li><a id="navAbout" href="<c:url value="about"/>">About</a></li>
<li><a id="navContact" href="<c:url value="contact"/>">Contact</a></li>
</ul>
</div>


<div id="crumbs">
    <a id="homeUrl" href="<c:url value="home"/>">Home</a>
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

</c:if>
</div>

 <div id="main">


<div id="leftMenu">

<c:if test="${fn:contains(thisURL,'project') && !fn:contains(thisURL,'projectdetail')&& !fn:contains(thisURL,'projectanimals') && !fn:contains(thisURL,'projectreceivers')&& !fn:contains(thisURL,'alloztrackprojects')&& !fn:contains(thisURL,'projectmap')}">
    <ul>
    <li><a href="<c:url value="projectadd"/>">Create New Project</a></li>
    <li><a href="<c:url value="projects"/>">Project List</a></li>
    </ul>
</c:if>

<c:if test="${fn:contains(thisURL,'projectdetail') || fn:contains(thisURL,'projectmap') || fn:contains(thisURL,'searchform') || fn:contains(thisURL,'datafiles') || fn:contains(thisURL,'projectanimals') || fn:contains(thisURL,'projectreceivers') || fn:contains(thisURL,'datafileadd')|| fn:contains(thisURL,'datafiledetail')|| fn:contains(thisURL,'animalform')|| fn:contains(thisURL,'receiverform')}">
    <c:if test="${project.title != null}">
        <ul>
          <li><a href="<c:url value="projectmap"/>">Visualisation Tools</a></li>
          <li><a href="<c:url value="searchform"/>">Analysis Tools</a></li>
          <li><a href="<c:url value="datafiles"/>">Data Uploads</a></li>
          <li><a href="<c:url value="projectdetail"/>">Project Details</a></li>
          <!--
          <li><a href="<c:url value="projectanimals"/>">Animals</a></li>
          <li><a href="<c:url value="projectreceivers"/>">Receivers</a></li>
          -->
        </ul>

    </c:if>
</c:if>

<c:if test="${fn:contains(thisURL,'home')}">
    <p>
    OzTrack is a collaboration between the eResearch lab and Eco-Lab
    at the University of Queensland. It is funded through the ANDS program,
    with the aim being to capture and share datasets associated with animal
    and marine species tracking.
    </p>
</c:if>

<div id="logos">
<a href="http://ands.org.au/"/><img src="images/ands-logo.png" width="140px" height="67px"/></a>
<a href="http://itee.uq.edu.au/~eresearch/"/><img src="images/uq_logo.png" width="140px" height="40px"/></a>
</div>

</div>

<div id="content">

<!--
<c:forEach items='${requestScope}' var='p'>
<ul>
<li>Parameter Name: <c:out value='${p.key}'/></li>
<li>Parameter Value: <c:out value='${p.value}'/></li>
</ul>
</c:forEach>
-->




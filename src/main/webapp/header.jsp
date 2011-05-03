<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Index</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="css/oztrack.css"/>
    <script type="text/javascript" src="js/jquery/jquery.js"></script>
    <script type="text/javascript" src="js/oztrack.js"></script>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>

</head>

<body>

<div id="container">
<div id="top_header"><h1>OzTrack</h1>

</div>
<div id="login">
       <p><a href="<c:url value="login"/>">Login</a> or <a href="<c:url value="register"/>">Register</a></p>
    </div>

<div id="nav" align="center"><br> Home | Login | Search | Related Links | Contact Us
</div>

<div id="main">

<br><br>

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
<div id="top_header">
    <img src="images/header_croc.jpg" align="right"/>
</div>


<div id="nav">
<a href="<c:url value=""/>">Home</a>
<a href="<c:url value="searchacoustic"/>">Tracking Portal</a>
<a href="<c:url value="projects"/>">My Projects</a>
<a href="<c:url value="about"/>">About</a>
<a href="<c:url value="contact"/>">Contact</a>
</div>


<div id="crumbs">
<a href=#>Home </a>
<a href=#> &rsaquo; My Projects </a>
<a href=#> &rsaquo; My Animals</a>
</div>


<div id="login">

        <h1>Login</h1>
        <form action="#" method="post" id="loginForm" onsubmit="login(); return false;">
           <p id="login-error" style="color:#ff0000;"></p>
           <label for="username">Username</label><input id="username" name="username" value="" title="username" type="text"></input>
           <br>
           <label for="password">Password</label><input id="password" name="password" value="" title="password" type="password"></input>
           <br>
           <input id="loginSubmit" value="Log in" type="submit" src="images/login.gif"/>
        </form>

       <p><a href="<c:url value="login"/>">Login</a> or <a href="<c:url value="register"/>">Register</a></p>

</div>

<div id="main">




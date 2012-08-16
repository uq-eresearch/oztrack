<!DOCTYPE html>
<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="head" required="true" fragment="true" %>
<%@ attribute name="breadcrumbs" required="false" fragment="true" %>
<%@ attribute name="sidebar" required="false" fragment="true" %>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/formalize.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui/jquery-ui-1.8.22.custom.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/oztrack.css"/>"/>
    
    <script type="text/javascript" src="<c:url value="/js/jquery/jquery-1.7.2.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery/jquery.formalize.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery/jquery-ui-1.8.22.custom.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/ckeditor/ckeditor.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/ckeditor/adapters/jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/oztrack.js"/>"></script>
    
    <title>OzTrack: ${title}</title>
    
    <jsp:invoke fragment="head"/>
</head>
<body>
<div id="container">
<div id="top_header">
    <div id="header_left"></div>
    <div id="header_right">
        <div id="login">
            <c:choose>
                <c:when test="${currentUser != null}">
                  Welcome, <c:out value="${currentUser.firstName}"/>
                  &nbsp;|&nbsp;
                  <a href="<c:url value="/users/${currentUser.id}/edit"/>">Profile</a>
                  &nbsp;|&nbsp;
                  <a href="<c:url value="/logout"/>">Logout</a>
                </c:when>
                <c:otherwise>
                  <a href="<c:url value="/login"/>">Login</a>
                  or
                  <a href="<c:url value="/users/new"/>">Register</a>
                </c:otherwise>
            </c:choose>
        </div>   
    </div>
</div>
<div id="nav">
    <ul id="navMenu">
        <li><a id="navHome" href="<c:url value="/"/>">Home</a></li>
        <li><a id="navTrack" href="<c:url value="/projects"/>">Animal Tracking</a></li>
        <li><a id="navAbout" href="<c:url value="/about"/>">About</a></li>
        <li><a id="navContact" href="<c:url value="/contact"/>">Contact</a></li>
        <c:if test="${currentUser.admin}">
        <li><a id="navSettings" href="<c:url value="/settings"/>">Settings</a></li>
        </c:if>
    </ul>
</div>
<c:if test="${!empty breadcrumbs}">
<div id="crumbs">
    <jsp:invoke fragment="breadcrumbs"/>
</div>
</c:if>
<div id="main">
    <jsp:invoke var="sidebarContent" fragment="sidebar"/>
    <c:if test="${!empty sidebarContent}">
	<div id="leftMenu">
        ${sidebarContent}
	</div>
    </c:if>
	<div id="content" class="${empty sidebarContent ? 'wide' : 'narrow'}">
		<jsp:doBody/>
		<div class="clearboth">&nbsp;</div>
	</div> <!-- content -->
</div> <!-- main -->
<div id="footer">
    <div id="logos">
        <a href="http://nectar.org.au/"><img src="<c:url value="/images/nectar-logo.png"/>" width="140px" height="32px"/></a>
        <a href="http://ands.org.au/"><img src="<c:url value="/images/ands-logo.png"/>" width="90px" height="40px" style="margin-top: -8px;"/></a>
        <a href="http://itee.uq.edu.au/~eresearch/"><img src="<c:url value="/images/uq_logo.png"/>" width="140px" height="40px"/></a>
    </div>
    <div id="copyright">
        &copy; 2011 The University of Queensland
    </div>
</div>
</div> <!-- container -->
</body>
</html>

<!DOCTYPE html>
<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="head" required="true" fragment="true" %>
<%@ attribute name="breadcrumbs" required="false" fragment="true" %>
<%@ attribute name="breadcrumbsRight" required="false" fragment="true" %>
<%@ attribute name="sidebar" required="false" fragment="true" %>
<%@ attribute name="fluid" required="false" type="java.lang.Boolean" %>
<c:set var="googleAnalyticsTrackingID"><%= OzTrackApplication.getApplicationContext().getGoogleAnalyticsTrackingID() %></c:set>
<c:set var="googleAnalyticsDomainName"><%= OzTrackApplication.getApplicationContext().getGoogleAnalyticsDomainName() %></c:set>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />

    <link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery-ui/jquery-ui-1.8.23.custom.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/bootstrap.css"/>"/>

    <script type="text/javascript" src="<c:url value="/js/jquery/jquery-1.8.0.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/jquery/jquery-ui-1.8.23.custom.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/ckeditor/ckeditor.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/ckeditor/adapters/jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/bootstrap.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/oztrack.js"/>"></script>
    <c:if test="${not empty googleAnalyticsTrackingID}">
    <script type="text/javascript">
        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', '${googleAnalyticsTrackingID}']);
        <c:if test="${not empty googleAnalyticsDomainName}">
        _gaq.push(['_setDomainName', '${googleAnalyticsDomainName}']);
        _gaq.push(['_setAllowLinker', true]);
        </c:if>
        _gaq.push(['_trackPageview']);
    
        (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();
    </script>
    </c:if>

    <title>OzTrack: ${title}</title>

    <jsp:invoke fragment="head"/>
</head>
<body>
<div id="header">
    <div class="container${fluid ? '-fluid' : ''}">
    <div id="banner">
        <div id="banner-left"></div>
        <div id="banner-right">
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
                      &nbsp;|&nbsp;
                      <a href="<c:url value="/users/new"/>">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    </div>
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <ul class="nav">
          <li id="navHome"><a href="<c:url value="/"/>">Home</a></li>
          <li id="navTrack"><a href="<c:url value="/projects"/>">Animal Tracking</a></li>
          <li id="navAbout"><a href="<c:url value="/about"/>">About</a></li>
          <li id="navContact"><a href="<c:url value="/contact"/>">Contact</a></li>
          <c:if test="${currentUser.admin}">
          <li id="navSettings"><a href="<c:url value="/settings"/>">Settings</a></li>
          </c:if>
        </ul>
      </div>
    </div>
</div>
<div id="main">
<div class="container${fluid ? '-fluid' : ''}">
    <c:if test="${!empty breadcrumbs}">
    <div id="crumbs">
        <c:if test="${!empty breadcrumbsRight}">
        <div id="crumbs-right">
            <jsp:invoke fragment="breadcrumbsRight"/>
        </div>
        </c:if>
        <jsp:invoke fragment="breadcrumbs"/>
    </div>
    </c:if>
    <jsp:invoke var="sidebarContent" fragment="sidebar"/>
    <div class="row${fluid ? '-fluid' : ''}">
    <c:if test="${!empty sidebarContent}">
    <div id="left-menu" class="span2">
        ${sidebarContent}
    </div>
    </c:if>
    <div id="content" class="${empty sidebarContent ? 'span12' : 'span10'}">
        <jsp:doBody/>
        <div style="clear:both;"></div>
    </div> <!-- content -->
    </div>
</div>
</div>
<c:if test="${not fluid}">
<div id="footer">
    <div id="logos">
        <a href="http://nectar.org.au/"><img src="<c:url value="/img/nectar-logo.png"/>" width="140px" height="32px"/></a>
        <a href="http://ands.org.au/"><img src="<c:url value="/img/ands-logo.png"/>" width="90px" height="40px" style="margin-top: -8px;"/></a>
        <a href="http://itee.uq.edu.au/~eresearch/"><img src="<c:url value="/img/uq_logo.png"/>" width="140px" height="40px"/></a>
    </div>
    <div id="copyright">
        &copy; 2011 The University of Queensland
    </div>
</div>
</c:if>
</body>
</html>

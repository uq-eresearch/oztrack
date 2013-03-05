<!DOCTYPE html>
<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="description" required="false" %>
<%@ attribute name="head" required="false" fragment="true" %>
<%@ attribute name="tail" required="false" fragment="true" %>
<%@ attribute name="breadcrumbs" required="false" fragment="true" %>
<%@ attribute name="navExtra" required="false" fragment="true" %>
<%@ attribute name="sidebar" required="false" fragment="true" %>
<%@ attribute name="fluid" required="false" type="java.lang.Boolean" %>
<c:set var="googleAnalyticsTrackingID"><%= OzTrackApplication.getApplicationContext().getGoogleAnalyticsTrackingID() %></c:set>
<c:set var="googleAnalyticsDomainName"><%= OzTrackApplication.getApplicationContext().getGoogleAnalyticsDomainName() %></c:set>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <c:if test="${not empty description}">
    <meta name="description" content="${description}" />
    </c:if>
    <title>OzTrack${(not empty title) ? ': ' : ' - '}${(not empty title) ? title : 'Free Animal Tracking Software'}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/optimised/core.css"/>
    <jsp:invoke fragment="head"/>
</head>
<body>
<div id="header">
    <div class="container${fluid ? '-fluid' : ''}">
    <div id="banner">
        <div id="banner-left">
            <c:if test="${!empty navExtra}">
            <div id="nav-extra">
                <jsp:invoke fragment="navExtra"/>
            </div>
            </c:if>
        </div>
        <div id="banner-right">
            <div id="login">
                <c:choose>
                <c:when test="${currentUser != null}">
                <div class="btn-group">
                    <a class="btn btn-inverse dropdown-toggle" data-toggle="dropdown" href="#">
                        <span><c:out value="${currentUser.fullName}"/></span>
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu pull-right">
                        <li><a href="${pageContext.request.contextPath}/users/${currentUser.id}/edit">Edit profile</a></li>
                        <c:if test="${currentUser.admin}">
                        <li><a href="${pageContext.request.contextPath}/settings">Settings</a></li>
                        </c:if>
                        <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                    </ul>
                </div>
                </c:when>
                <c:otherwise>
                <div>
                    <a class="btn btn-inverse" href="${pageContext.request.contextPath}/users/new">Register</a>
                    <a class="btn btn-inverse" href="${pageContext.request.contextPath}/login">Login</a>
                </div>
                </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    </div>
    <div class="navbar navbar-inverse">
      <div class="navbar-inner">
        <ul class="nav">
          <li id="navHome"><a href="${pageContext.request.contextPath}/">Home</a></li>
          <li id="navTrack"><a href="${pageContext.request.contextPath}/projects">Projects</a></li>
          <li id="navAbout"><a href="${pageContext.request.contextPath}/about">About</a></li>
          <li id="navContact"><a href="${pageContext.request.contextPath}/contact">Contact</a></li>
        </ul>
      </div>
    </div>
</div>
<div id="main">
<div class="container${fluid ? '-fluid' : ''}">
    <c:if test="${!empty breadcrumbs}">
    <div id="crumbs">
        <jsp:invoke fragment="breadcrumbs"/>
    </div>
    </c:if>
    <jsp:invoke var="sidebarContent" fragment="sidebar"/>
    <div class="row${fluid ? '-fluid' : ''}">
    <c:if test="${!empty sidebarContent}">
    <div id="left-menu" class="span3">
        ${sidebarContent}
    </div>
    </c:if>
    <div id="content" class="${empty sidebarContent ? 'span12' : 'span9'}">
        <jsp:doBody/>
        <div style="clear:both;"></div>
    </div> <!-- content -->
    </div>
</div>
</div>
<c:if test="${not fluid}">
<div id="footer">
    <div id="logos">
        <a href="http://nectar.org.au/"><img src="${pageContext.request.contextPath}/img/nectar-logo.png" width="140px" height="32px"/></a>
        <a href="http://ands.org.au/"><img src="${pageContext.request.contextPath}/img/ands-logo.png" width="90px" height="40px" style="margin-top: -8px;"/></a>
        <a href="http://itee.uq.edu.au/~eresearch/"><img src="${pageContext.request.contextPath}/img/uq_logo.png" width="140px" height="40px"/></a>
    </div>
    <div id="copyright">
        &copy; 2011 The University of Queensland
    </div>
</div>
</c:if>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/optimised/core.js"></script>
<jsp:invoke fragment="tail"/>
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
</body>
</html>

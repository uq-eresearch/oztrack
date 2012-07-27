<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<tags:page title="Login">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navHome').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="active">Login</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
		<h1>Login</h1>
		
        <c:if test="${not empty sessionScope['SPRING_SECURITY_LAST_EXCEPTION'].message}">
        <p class="errorMessage">
            ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
        </p>
        </c:if>
        
		<div style="clear: both;"></div>

        <c:if test="${aafEnabled}">
        <form style="width: 600px; margin: 20px 0;">
        <h2>Login using AAF</h2>
        <div style="margin: 1em 0;">
            Click here to authenticate using the <a href="http://www.aaf.edu.au/">Australian Access Federation (AAF)</a>.
        </div>
        <div style="margin: 1em 0;">
            You will be redirected to your home institution's website to login.
        </div>
        <div style="margin: 1em 0;">
        <a class="oztrackButton" href="<c:url value="/login/shibboleth"/>">Login using AAF</a>
        </div>
        </form>
        </c:if>
        
		<form id="nativeLoginForm" method="POST" action="<c:url value="/j_spring_security_check"/>" style="width: 600px; margin: 20px 0;">
		
        <c:if test="${aafEnabled}">
        <h2>Login using OzTrack</h2>
        </c:if>
        
        <div>
		<label for="username" style="width: auto; text-align: left;">Username:</label>
		<input type="text" name="username" id="username"/>
		</div>
		
		<div>
		<label for="password" style="width: auto; text-align: left;">Password:</label>
		<input type="password" name="password" id="password"/>
		</div>
		
		<div style="margin: 1em 0;">
		<input type="submit" value="Login"/>
		</div>
		</form>

        <div style="clear: both;"></div>

        <p style="font-size: 12px; margin: 1em 0;">Don't have an account yet? <a href="<c:url value="/users/new"/>">Register as a new user</a></p>
    </jsp:body>
</tags:page>

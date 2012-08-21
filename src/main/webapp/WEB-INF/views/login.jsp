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
        <div class="alert alert-error" style="width: 586px;">
            ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
        </div>
        </c:if>
        
		<div style="clear: both;"></div>

        <c:if test="${aafEnabled}">
        <form class="form-vertical form-bordered" style="width: 600px;">
            <fieldset>
                <legend>Login using AAF</legend>
                <div class="control-group">
                    <p>
                        Click here to authenticate using the <a href="http://www.aaf.edu.au/">Australian Access Federation (AAF)</a>.
                    </p>
                    <p>
                        You will be redirected to your home institution's website to login.
                    </p>
                </div>
                <div class="form-actions">
                <a class="btn btn-primary" href="<c:url value="/login/shibboleth"/>">Login using AAF</a>
                </div>
            </fieldset>
        </form>
        </c:if>
        
		<form id="nativeLoginForm" class="form-vertical form-bordered" style="width: 600px;" method="POST" action="<c:url value="/j_spring_security_check"/>">
    		<fieldset>
                <c:if test="${aafEnabled}">
                <legend>Login using OzTrack</legend>
                </c:if>
                <div class="control-group">
            		<label class="control-label" for="username">Username</label>
                    <div class="controls">
            		    <input type="text" name="username" id="username"/>
                    </div>
        		</div>
        		<div class="control-group">
            		<label class="control-label" for="password">Password</label>
                    <div class="controls">
            		    <input type="password" name="password" id="password"/>
                    </div>
        		</div>
            </fieldset>
    		<div class="form-actions">
    		<input class="btn btn-primary" type="submit" value="Login"/>
    		</div>
		</form>

        <div style="clear: both;"></div>

        <p style="margin: 1em 0;">Don't have an account yet? <a href="<c:url value="/users/new"/>">Register as a new user</a></p>
    </jsp:body>
</tags:page>

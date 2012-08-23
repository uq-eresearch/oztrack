<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<tags:page title="Reset Password">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navHome').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="active">Reset Password</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
        <h1>Reset Password</h1>

        <c:choose>
        <c:when test="${not empty successMessage}">
            <div class="alert alert-success" style="width: 586px;">
                ${successMessage}
            </div>
        </c:when>
        <c:when test="${not empty errorMessage}">
            <div class="alert alert-error" style="width: 586px;">
                ${errorMessage}
            </div>
        </c:when>
        </c:choose>

        <p>
            Please enter a new password to reset your account.
        </p>
        <form class="form-vertical form-bordered" style="width: 600px;" method="POST" action="">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="username">Username</label>
                    <div class="controls">
                        <input type="text" name="user" disabled="disabled" value="${user.username}" />
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Password</label>
                    <div class="controls">
                        <input type="password" name="password" />
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Reset Password" />
            </div>
        </form>
    </jsp:body>
</tags:page>
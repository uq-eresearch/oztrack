<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<tags:page title="Reset Password">
    <jsp:attribute name="description">
        Reset a forgotten password in OzTrack.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navHome').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <span class="active">Reset Password</span>
    </jsp:attribute>
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
            Please enter the email address associated with your user profile.
        </p>
        <p>
            You'll receive an email shortly containing a link to reset your password.
        </p>
        <form class="form-vertical form-bordered" style="width: 600px;" method="POST" action="">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="email">Email address</label>
                    <div class="controls">
                        <input type="text" name="email" value="${empty successMessage ? param.email : ''}" />
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Reset Password" />
            </div>
        </form>
        <p>
            Don't have an account yet? <a href="${pageContext.request.contextPath}/users/new">Register as a new user</a>
        </p>
    </jsp:body>
</tags:page>

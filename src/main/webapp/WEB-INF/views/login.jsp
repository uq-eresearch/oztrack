<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<tags:page title="Login">
    <jsp:attribute name="description">
        Log in to OzTrack. OzTrack is a free-to-use web-based platform for
        analysing and visualising individual-based animal location data.
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
        &rsaquo; <span class="active">Login</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Login</h1>

        <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            ${errorMessage}
        </div>
        </c:if>

        <div class="alert alert-info" style="text-align: center;">
            Don't have an account yet? <a href="${pageContext.request.contextPath}/users/new">Register as a new user</a>
        </div>

        <div class="row">
            <c:if test="${aafEnabled}">
            <div class="span6">
                <form class="form-vertical form-bordered" style="height: 230px;">
                    <fieldset>
                        <div class="legend">Login using AAF</div>
                        <div style="margin: 18px 0;">
                            <p>
                                Click here to authenticate using the <a target="_blank" href="http://www.aaf.edu.au/">Australian Access Federation (AAF)</a>.
                            </p>
                            <p>
                                You will be redirected to your home institution's website to login.
                            </p>
                        </div>
                    </fieldset>
                    <div class="form-actions">
                        <c:url var="shibbolethUrl" value="${pageContext.request.contextPath}/login/shibboleth">
                            <c:if test="${not empty redirectUrl}">
                            <c:param name="redirect">
                                ${redirectUrl}
                            </c:param>
                            </c:if>
                        </c:url>
                        <a class="btn btn-primary" href="${shibbolethUrl}">Login using AAF</a>
                    </div>
                </form>
            </div>
            </c:if>
            <div class="${aafEnabled ? 'span6' : 'span12'}">
                <form id="nativeLoginForm" class="form-vertical form-bordered" style="height: 230px;"
                    method="POST" action="${pageContext.request.contextPath}/login">
                    <fieldset>
                        <div class="legend">Login ${aafEnabled ? 'using' : 'to'} OzTrack</div>
                        <div class="control-group">
                            <label class="control-label" for="username">Username</label>
                            <div class="controls">
                                <input type="text" name="username" id="username" value="${username}"/>
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="password">Password</label>
                            <div class="controls">
                                <input type="password" name="password" id="password"/>
                            </div>
                        </div>
                        <c:if test="${not empty redirectUrl}">
                        <input type="hidden" name="redirect" value="${redirectUrl}" />
                        </c:if>
                    </fieldset>
                    <div class="form-actions">
                        <input class="btn btn-primary" type="submit" value="Login"/>
                        <a style="margin-left: 10px;" href="${pageContext.request.contextPath}/reset-password">Can't access your account?</a>
                    </div>
                </form>
            </div>
        </div>
    </jsp:body>
</tags:page>

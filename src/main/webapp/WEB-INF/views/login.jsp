<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
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
        
		<form method="POST" action="/j_spring_security_check" style="width: 600px;">
		
		<div>
		<label for="username">Username:</label>
		<input type="text" name="username" id="username"/>
		</div>
		
		<div>
		<label for="password">Password:</label>
		<input type="password" name="password" id="password"/>
		</div>
		
		<div>
		<label></label>
		<div class="formButton"><input type="submit" value="Login"/></div>
		</div>
		
		<div><label></label><a href="<c:url value="/users/new"/>">Register as a new user</a>
		</div>
		</form>
    </jsp:body>
</tags:page>

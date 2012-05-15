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
    <jsp:body>
		<h1>Login</h1>
		
		<div class="errorMessage"><c:out value="${errorMessage}"/></div>
        
		<form:form commandName="user" method="POST" name="login">
		
		<div>
		<label for="username">Username:</label>
		<form:input path="username" id="username"/>
		<form:errors path="username" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="password">Password:</label>
		<form:password path="password" id="password"/>
		<form:errors path="password" cssClass="formErrors"/>
		</div>
		
		<div>
		<label></label>
		<div class="formButton"><input type="submit" value="Login"/></div>
		</div>
		
		<div><label></label><a href="<c:url value="/register"/>">Register as a new user</a>
		</div>
		</form:form>
    </jsp:body>
</tags:page>

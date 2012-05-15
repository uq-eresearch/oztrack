<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Registration successful">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            projectPage = true;
            $(document).ready(function() {
                $('#navHome').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="aCrumb">Registration successful</span>
    </jsp:attribute>
    <jsp:body>
		<h2>Registration successful</h2>
		<table id="registrationTable">
			<tr>
				<td>Username:</td>
				<td><c:out value="${user.username}"/></td>
			</tr>
			<tr>
				<td>Full Name:</td>
				<td><c:out value="${user.firstName} ${user.lastName}"/></td>
			</tr>
			<tr>
				<td>Email:</td>
				<td><c:out value="${user.email}"/></td>
			</tr>
		</table>
		<p>
		    <a href="<c:url value="login"/>">Proceed to Login</a>
		</p>
    </jsp:body>
</tags:page>
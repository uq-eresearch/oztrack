<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Users">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navHome').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a id="homeUrl" href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="aCrumb">Users</span>
    </jsp:attribute>
    <jsp:body>
		<h1>Users</h1>
		<table class="dataTable">
		    <tr>
		        <th>Name</th>
		        <th>Username</th>
		        <th>Email</th>
		    </tr>
		    <c:forEach items="${userList}" var="user">
		        <tr>
		            <td><c:out value="${user.firstName}"/></td>
		            <td><c:out value="${user.username}"/></td>
		            <td><c:out value="${user.email}"/></td>
		        </tr>
		    </c:forEach>
		</table>
    </jsp:body>
</tags:page>
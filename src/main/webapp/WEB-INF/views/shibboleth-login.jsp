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
		<h1>Login ${empty errorMessage ? "Successful" : "Unsuccessful"}</h1>
		
        <c:choose>
        <c:when test="${not empty errorMessage}">
        <p class="errorMessage">
            ${errorMessage}
        </p>
        <p class="errorMessage">
            <a href="<c:url value="/login/shibboleth"/>">Login again</a>
        </p>
        </c:when>
        <c:when test="${not empty commonName}">
        <p>
            Logged in as ${commonName}<c:if test="${not empty organisation}">, ${organisation}</c:if> (${eppn}).
        </p>
        </c:when>
        <c:otherwise>
        <p>
            Logged in as ${eppn}.
        </p>
        </c:otherwise>
        </c:choose>
    </jsp:body>
</tags:page>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Sighting Reported">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
            	$('#navHome').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a id="homeUrl" href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="/sighting">Reporting Animal Sighting</a>
        &rsaquo; <span class="aCrumb">Sighting Reported</span>
    </jsp:attribute>
    <jsp:body>
		<h1>Thank You.</h1>
		<p>Your sighting has been included.</p>
    </jsp:body>
</tags:page>
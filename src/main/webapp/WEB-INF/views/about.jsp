<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="About">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
            	$('#navAbout').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a id="homeUrl" href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="aCrumb">About</span>
    </jsp:attribute>
    <jsp:body>
		<h1>About OzTrack</h1>
		<p>
		    This project is supported by the Australian National Data Service (ANDS).
		    ANDS is supported by the Australian Government through the National Collaborative
		    Research Infrastructure Strategy Program and the Education Investment Fund (EIF)
		    Super Science Initiative.
		</p>
    </jsp:body>
</tags:page>
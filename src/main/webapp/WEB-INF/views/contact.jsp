<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Contacts">
    <jsp:attribute name="description">
        Contact OzTrack staff. OzTrack is a free-to-use web-based platform
        for analysing and visualising individual-based animal location data.
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navContact').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <span class="active">Contact</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
        ${text}
    </jsp:body>
</tags:page>

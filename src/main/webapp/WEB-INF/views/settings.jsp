<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Settings">
    <jsp:attribute name="description">
        Update OzTrack settings.
    </jsp:attribute>
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navSettings').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <span class="active">Settings</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Settings</h1>
        <h2>Manage Settings</h2>
        <ul class="icons">
            <li class="edit"><a href="${pageContext.request.contextPath}/settings/geoserver">GeoServer</a></li>
            <li class="edit"><a href="${pageContext.request.contextPath}/settings/srs">Spatial Reference Systems</a></li>
            <li class="edit"><a href="${pageContext.request.contextPath}/settings/content">Website Content</a></li>
            <li class="edit"><a href="${pageContext.request.contextPath}/settings/usage">Usage Summary</a></li>
        </ul>
    </jsp:body>
</tags:page>
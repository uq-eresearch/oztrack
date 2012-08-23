<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Settings">
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navSettings').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="active">Settings</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Settings</h1>
        <div class="actions">
        <h2>Manage Settings</h2>
        <ul>
            <li class="edit"><a href="<c:url value="/settings/srs"/>">Spatial reference systems</a></li>
            <li class="edit"><a href="<c:url value="/settings/content"/>">Website content</a></li>
        </ul>
        </div>
    </jsp:body>
</tags:page>
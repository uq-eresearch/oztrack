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
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <span class="active">GeoServer</span>
    </jsp:attribute>
    <jsp:body>
        <h1>GeoServer</h1>
        <form:form cssClass="form-vertical form-bordered" method="POST">
            <p>
                Click to update objects in GeoServer (e.g. workspace, datastore, layers, styles)
            </p>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Update GeoServer" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>
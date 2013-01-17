<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Embargo Settings">
    <jsp:attribute name="description">
        Update Embargo settings.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navSettings').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <span class="active">Embargo</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Embargo</h1>
        <form:form cssClass="form-vertical form-bordered" method="POST">
            <p>
                Run the embargo updater:
            </p>
            <ul>
                <li>update projects that have reached the end of their embargo period;</li>
                <li>send notifications for projects nearing the end of their embargo period.</li>
            </ul>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Update Embargo" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>

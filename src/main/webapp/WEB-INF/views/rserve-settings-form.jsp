<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Rserve Settings">
    <jsp:attribute name="description">
        Update Rserve settings.
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
        &rsaquo; <span class="active">Rserve</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Rserve</h1>
        <p>Connection pool:</p>
        <ul>
            <li>${rServeConnectionPool.numActive} active connections</li>
            <li>${rServeConnectionPool.numIdle} idle connections</li>
        </ul>
        <c:if test="${not empty err}">
        <p>Error stream</p>
        <pre>${err}</pre>
        </c:if>
        <c:if test="${not empty out}">
        <p>Output stream</p>
        <pre>${out}</pre>
        </c:if>
        <form:form cssClass="form-vertical form-bordered" method="POST">
            <div class="control-group">
                <label class="control-label" for="title">Stop Rserve process</label>
                <div class="controls">
                    <label class="checkbox">
                        <input name="force" type="checkbox" /> Force (<tt>SIGKILL</tt> instead of <tt>SIGTERM</tt>)
                    </label>
                </div>
            </div>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Stop Rserve" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>

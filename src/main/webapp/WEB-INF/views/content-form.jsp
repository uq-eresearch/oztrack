<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Content Settings">
    <jsp:attribute name="description">
        Update content settings.
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
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <span class="active">Content</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Content</h1>
        <form:form cssClass="form-vertical" commandName="settings" method="POST" name="settings">
            <fieldset>
            <div class="control-group">
                <h2>Home text</h2>
                <div class="controls">
                    <form:errors path="homeText" element="div" cssClass="alert alert-error"/>
                    <form:textarea path="homeText" id="homeText" cssClass="ckeditor"/>
                </div>
            </div>
            <div class="control-group">
                <h2>About text</h2>
                <div class="controls">
                    <form:errors path="aboutText" element="div" cssClass="alert alert-error"/>
                    <form:textarea path="aboutText" id="aboutText" cssClass="ckeditor"/>
                </div>
            </div>
            <div class="control-group">
                <h2>Contact text</h2>
                <div class="controls">
                    <form:errors path="contactText" element="div" cssClass="alert alert-error"/>
                    <form:textarea path="contactText" id="contactText" cssClass="ckeditor"/>
                </div>
            </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Save" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>
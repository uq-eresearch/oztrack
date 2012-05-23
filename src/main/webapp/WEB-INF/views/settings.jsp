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
        <form:form commandName="settings" method="POST" name="settings" cssStyle="width: 100%; padding: 0; background-color: transparent;">
            <div>
                <h2>Home text</h2>
                <form:textarea path="homeText" id="homeText" cssClass="ckeditor"/>
                <form:errors path="homeText" cssClass="formErrors"/>
                <h2>About text</h2>
                <form:textarea path="aboutText" id="aboutText" cssClass="ckeditor"/>
                <form:errors path="aboutText" cssClass="formErrors"/>
                <h2>Contact text</h2>
                <form:textarea path="contactText" id="contactText" cssClass="ckeditor"/>
                <form:errors path="contactText" cssClass="formErrors"/>
            </div>
            <div>
                <input type="submit" value="Save" class="oztrackButton" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="title">
    <c:choose>
    <c:when test="${srs.id != null}">
        Update Spatial Reference System
    </c:when>
    <c:otherwise>
        Create Spatial Reference System
    </c:otherwise>
    </c:choose>
</c:set>
<tags:page title="${title}">
    <jsp:attribute name="head">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/settings"/>">Settings</a>
        &rsaquo; <a href="<c:url value="/settings/srs"/>">Spatial Reference Systems</a>
        &rsaquo; <span class="active">${title}</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Spatial Reference Systems</h1>
        <c:choose>
            <c:when test="${srs.id != null}">
                <c:set var="method" value="PUT"/>
                <c:set var="action" value="/settings/srs/${srs.id}"/>
            </c:when>
            <c:otherwise>
                <c:set var="method" value="POST"/>
                <c:set var="action" value="/settings/srs"/>
            </c:otherwise>
        </c:choose>
        <form:form class="form-horizontal form-bordered" method="${method}" action="${action}" commandName="srs">
            <fieldset>
                <div class="legend">${title}</div>
                <div class="control-group">
                    <label class="control-label" for="identifier">ID</label>
                    <div class="controls">
                        <form:input path="identifier" cssStyle="width: 150px;"/>
                        <form:errors path="identifier" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="title">Title</label>
                    <div class="controls">
                        <form:input path="title" cssClass="input-xxlarge"/>
                        <form:errors path="title" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="bounds">Bounds</label>
                    <div class="controls">
                        <form:input path="bounds" cssClass="input-xxlarge"/>
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span>
                                <b>Bounds:</b><br>
                                <br>
                                Comma-separated values for the spatial reference system's WGS84 bounding box.<br>
                                <br>
                                Example: 144.0000, -54.7500, 150.0000, -1.7100
                            </span>
                            </a>
                        </div>
                        <form:errors path="bounds" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="${(srs.id != null) ? 'Update SRS' : 'Create SRS'}" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>

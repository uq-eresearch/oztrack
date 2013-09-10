<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page>
    <jsp:attribute name="title">
        <c:choose>
        <c:when test="${srs.id != null}">
            Update Spatial Reference System
        </c:when>
        <c:otherwise>
            Create Spatial Reference System
        </c:otherwise>
        </c:choose>
    </jsp:attribute>
    <jsp:attribute name="description">
        <c:choose>
        <c:when test="${srs.id != null}">
            Update details for the ${srs.title} spatial reference system.
        </c:when>
        <c:otherwise>
            Create a new spatial reference system.
        </c:otherwise>
        </c:choose>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings/srs">Spatial Reference Systems</a>
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
                <div class="control-group required">
                    <label class="control-label" for="identifier">ID</label>
                    <div class="controls">
                        <form:input path="identifier" cssStyle="width: 150px;"/>
                        <form:errors path="identifier" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group required">
                    <label class="control-label" for="title">Title</label>
                    <div class="controls">
                        <form:input path="title" cssClass="input-xxlarge"/>
                        <form:errors path="title" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group required">
                    <label class="control-label" for="bounds">Bounds</label>
                    <div class="controls">
                        <form:input path="bounds" cssClass="input-xxlarge"/>
                        <div class="help-inline">
                            <div class="help-popover" title="Bounds">
                                Comma-separated values for the spatial reference system's WGS84 bounding box.<br>
                                <br>
                                Example: 144.0000, -54.7500, 150.0000, -1.7100
                            </div>
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

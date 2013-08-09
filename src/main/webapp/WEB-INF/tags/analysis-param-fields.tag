<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="analysisType" type="org.oztrack.data.model.types.AnalysisType" required="true" %>
<c:set var="foundAdvancedParameterType" value="false"/>
<c:forEach items="${analysisType.parameterTypes}" var="parameterType">
<c:if test="${!foundAdvancedParameterType and parameterType.advanced}">
<c:set var="foundAdvancedParameterType" value="true"/>
<div style="margin-bottom: 3px;">
    <a href="javascript:void(0);" onclick="$(this).parent().nextAll().fadeToggle();">Advanced parameters</a>
</div>
</c:if>
<div class="paramPair <c:if test="${parameterType.paired}">paired</c:if>" style="<c:if test="${parameterType.advanced}">display: none;</c:if>">
    <span class="paramLabel">
        ${parameterType.displayName}
        <c:if test="${not empty parameterType.explanation}">
        <div class="help-popover" title="${parameterType.displayName}">
            ${parameterType.explanation}
        </div>
        </c:if>
    </span>
    <span class="paramField <c:if test="${not empty parameterType.units}"> input-append</c:if><c:if test="${parameterType.dataType != 'boolean'}"> wide</c:if>">
        <c:choose>
        <c:when test="${parameterType.options != null}">
        <select class="paramField-${analysisType}" style="width: 180px;" name="${parameterType.identifier}">
        <c:forEach items="${parameterType.options}" var="option">
        <option
            value="${option.value}"
            <c:if test="${parameterType.defaultValue == option.value}">selected="selected"</c:if>
            >${option.title}</option>
        </c:forEach>
        </select>
        </c:when>
        <c:when test="${parameterType.dataType == 'boolean'}">
        <input
            class="paramField-${analysisType} checkbox"
            name="${parameterType.identifier}"
            type="checkbox"
            <c:if test="${parameterType.defaultValue == 'true'}">checked="checked"</c:if>
            value="true"
            style="margin: 4px 1px;" />
        </c:when>
        <c:when test="${parameterType.dataType == 'date'}">
        <input
            type="hidden"
            id="paramField-${analysisType}-${parameterType.identifier}"
            class="paramField-${analysisType}"
            name="${parameterType.identifier}" />
        <input
            type="text"
            id="paramField-${analysisType}-${parameterType.identifier}Visible"
            class="datepicker input-mini"
            style="margin-bottom: 3px; width: 80px;" />
        </c:when>
        <c:otherwise>
        <input
            class="paramField-${analysisType} input-mini"
            name="${parameterType.identifier}"
            type="text"
            <c:if test="${not empty parameterType.defaultValue}">
            value="${parameterType.defaultValue}"
            </c:if>
            style="margin-bottom: 3px; text-align: right;"/>
        <c:if test="${not empty parameterType.units}">
        <span class="add-on">${parameterType.units}</span>
        </c:if>
        </c:otherwise>
        </c:choose>
    </span>
</div>
</c:forEach>
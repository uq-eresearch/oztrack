<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="analysisType" type="org.oztrack.data.model.types.AnalysisType" required="true" %>
<table>
    <c:set var="foundAdvancedParameterType" value="false"/>
    <c:forEach items="${analysisType.parameterTypes}" var="parameterType">
    <c:if test="${!foundAdvancedParameterType and parameterType.advanced}">
    <c:set var="foundAdvancedParameterType" value="true"/>
    <tr>
        <td colspan="2">
            <div style="margin-bottom: 3px;">
                <a href="javascript:void(0);" onclick="$(this).closest('tr').nextAll().fadeToggle();">Advanced parameters</a>
            </div>
        </td>
    </tr>
    </c:if>
    <tr <c:if test="${parameterType.advanced}">style="display: none;"</c:if>>
        <td style="white-space: nowrap; width: 10px;">${parameterType.displayName}</td>
        <td class="${(not empty parameterType.units) ? 'input-append' : ''}" style="margin: 0;">
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
                id="${parameterType.identifier}"
                class="paramField-${analysisType}"
                name="${parameterType.identifier}" />
            <input
                type="text"
                id="${parameterType.identifier}Visible"
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
        </td>
        <td style="width: 10px;">
            <c:if test="${not empty parameterType.explanation}">
            <div class="help-popover" title="${parameterType.displayName}">
                ${parameterType.explanation}
            </div>
            </c:if>
        </td>
    </tr>
    </c:forEach>
</table>
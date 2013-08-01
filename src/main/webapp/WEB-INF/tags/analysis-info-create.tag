<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="analysisTypeList" type="java.util.List" required="true" %>
<%@ attribute name="deleteLayerJsStmt" type="java.lang.String" required="false" %>
<%@ attribute name="parentIdJsExpr" type="java.lang.String" required="true" %>
<%@ attribute name="childIdJsExpr" type="java.lang.String" required="true" %>
<%@ attribute name="statsIdJsExpr" type="java.lang.String" required="true" %>
<%@ attribute name="classNameJsExpr" type="java.lang.String" required="false" %>
var html = '<div class="layerInfoTitle">';
<c:if test="${not empty deleteLayerJsStmt}">
html += '<a class="layer-delete" href="javascript:${deleteLayerJsStmt}">delete</a></span>';
</c:if>
html += layerName;
html += '</div>';
var statsHtml = '';
statsHtml += '<span class="layerInfoStat">';
statsHtml += 'Dates: ' + fromDate + ' - ' + toDate;
statsHtml += '</span>';
<c:forEach items="${analysisTypeList}" var="analysisType">
if (analysis.analysisType == '${analysisType}') {
    <c:forEach items="${analysisType.parameterTypes}" var="parameterType">
    if (analysis.params.${parameterType.identifier}) {
        statsHtml += '<span class="layerInfoStat">';
        statsHtml += '${parameterType.displayName}: ';
        statsHtml += analysis.params.${parameterType.identifier} + ' ${parameterType.units}';
        statsHtml += '</span>';
    }
    </c:forEach>
}
</c:forEach>
html += '<div id="' + ${statsIdJsExpr} + '" class="layerInfoStats" style="' + (statsHtml ? '' : 'display: none;') + '">' + statsHtml + '</table>';
$('#' + ${parentIdJsExpr}).append(
    $('<div>')
        .attr('id', ${childIdJsExpr})
        .addClass('layerInfo')
        <c:if test="${not empty classNameJsExpr}">
        .addClass(${classNameJsExpr})
        </c:if>
        .html(html)
);
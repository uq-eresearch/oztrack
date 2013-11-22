<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="project" type="org.oztrack.data.model.Project" required="true" %>
<%@ attribute name="analysisTypeList" type="java.util.List" required="true" %>
<%@ attribute name="deleteLayerJsStmt" type="java.lang.String" required="false" %>
<%@ attribute name="childIdJsExpr" type="java.lang.String" required="true" %>
<%@ attribute name="statsIdJsExpr" type="java.lang.String" required="true" %>
<%@ attribute name="helpPopoverIdJsExpr" type="java.lang.String" required="true" %>
var statsParent = $('#' + ${statsIdJsExpr});
var overallStatsHtml = '';
<c:forEach items="${analysisTypeList}" var="analysisType">
if (analysis.analysisType == '${analysisType}') {
    <c:forEach items="${analysisType.overallResultAttributeTypes}" var="resultAttributeType">
    if (analysis.result.attributes.${resultAttributeType.identifier}) {
        overallStatsHtml += '<span class="layerInfoStat">';
        overallStatsHtml += '${resultAttributeType.displayName}: ';
        <c:choose>
        <c:when test="${resultAttributeType.dataType == 'double' && not empty resultAttributeType.numDecimalPlaces}">
        overallStatsHtml += parseFloat(analysis.result.attributes.${resultAttributeType.identifier}).toFixed(${resultAttributeType.numDecimalPlaces});
        </c:when>
        <c:otherwise>
        overallStatsHtml += analysis.result.attributes.${resultAttributeType.identifier};
        </c:otherwise>
        </c:choose>
        <c:if test="${not empty resultAttributeType.units}">
        overallStatsHtml += ' ${resultAttributeType.units}';
        </c:if>
        overallStatsHtml += '</span>';
    }
    </c:forEach>
}
</c:forEach>
if (overallStatsHtml) {
    if (statsParent.is(':not(:empty)')) {
        statsParent.append($('<div>').css('margin', '4px 0').css('border-bottom', '1px dashed #aaa'));
    }
    statsParent.append(overallStatsHtml);
    statsParent.show();
}
var animalStatsHtml = '';
<c:forEach items="${analysisTypeList}" var="analysisType">
if (analysis.analysisType == '${analysisType}') {
    <c:forEach items="${analysisType.featureResultAttributeTypes}" var="resultAttributeType">
    if (
        animalAttributes &&
        animalAttributes.${resultAttributeType.identifier} &&
        animalAttributes.${resultAttributeType.identifier}.value
    ) {
        animalStatsHtml += '<span class="layerInfoStat">';
        animalStatsHtml += '${resultAttributeType.displayName}: ';
        <c:choose>
        <c:when test="${resultAttributeType.dataType == 'double' && not empty resultAttributeType.numDecimalPlaces}">
        animalStatsHtml += parseFloat(animalAttributes.${resultAttributeType.identifier}.value).toFixed(${resultAttributeType.numDecimalPlaces});
        </c:when>
        <c:otherwise>
        animalStatsHtml += animalAttributes.${resultAttributeType.identifier}.value;
        </c:otherwise>
        </c:choose>
        <c:if test="${not empty resultAttributeType.units}">
        animalStatsHtml += ' ${resultAttributeType.units}';
        </c:if>
        animalStatsHtml += '</span>';
    }
    </c:forEach>
}
</c:forEach>
if (animalStatsHtml) {
    if (statsParent.is(':not(:empty)')) {
        statsParent.append($('<div>').css('margin', '4px 0').css('border-bottom', '1px dashed #aaa'));
    }
    statsParent.append(animalStatsHtml);
    statsParent.show();
}
var exportHtml = '';
exportHtml += '<div class="layerInfoExport">';
exportHtml += 'Download: ';
<c:if test="${project.crosses180}">
if (analysis.result.type === 'HOME_RANGE') {
    exportHtml += '<div style="float: right;">';
    exportHtml += '<div id="' + ${helpPopoverIdJsExpr} + '" class="help-popover" title="KML Download">';
    exportHtml += '<p>Home range KML files are available in an outline-only version due to rendering issues ';
    exportHtml += 'in some versions of Google Earth. If you find that polygons crossing 180° longitude are ';
    exportHtml += 'being cut off or "wrapped", use the outline-only KML link.</p>';
    exportHtml += '</div>';
    exportHtml += '</div>';
}
</c:if>
exportHtml += $.map(analysis.result.files, function(r) {
    return '<a class="icon ' + r.format + '" href="' + r.url + '">' + r.title + '</a>';
}).join(', ');
exportHtml += '</div>';
$('#' + ${childIdJsExpr}).append(exportHtml);
<c:if test="${project.crosses180}">
if (analysis.result.type === 'HOME_RANGE') {
    OzTrack.initHelpPopover($('#' + ${helpPopoverIdJsExpr}));
}
</c:if>
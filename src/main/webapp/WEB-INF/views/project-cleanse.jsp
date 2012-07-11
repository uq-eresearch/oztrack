<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.data.model.types.MapQueryType" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Data Cleansing">
    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/style.css"/>" type="text/css">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/google.css"/>" type="text/css">
        <script src="http://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/proj4js/proj4js-compressed.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/LoadingPanel.js"></script>
        <script type="text/javascript" src="<c:url value="/js/cleansemap.js"/>"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $("#projectMapOptions").accordion({fillSpace: true});
                map = createCleanseMap(${project.id});
            });
        </script>
        <style type="text/css">
            ul#cleanseList {
                margin: 10px 0;
                padding-left: 0;
            }
            ul#cleanseList li {
                margin: 2px 0;
                list-style: none;
                background-repeat: no-repeat;
                background-position: left middle;
                line-height: 22px;
                padding: 0 0 5px 28px;
            }
            ul#cleanseList li {
                background-image: url(/js/openlayers/theme/default/img/draw_polygon_on.png);
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">Data Cleansing</span>
    </jsp:attribute>
    <jsp:body>
        <div class="mapTool">
        <div id="projectMapOptions">
            <h3 id="projectTitle"><a href="#"><c:out value="${project.title}"/></a></h3>
            <div style="padding: 0;">
                <form method="POST">
                <p style="font-size: 11px; font-weight: bold;">Data cleansing</p>
                <c:if test="${not empty numDeleted}">
                <p style="font-size: 10px; font-weight: bold; color: red;">
                    ${numDeleted} points deleted
                </p>
                </c:if>
                <p style="font-size: 10px; font-style: italic;">
                    Select points for removal from the project by drawing polygons around them.
                    Click to start drawing and click again to draw each side of your selected area.
                    Double-click to finish drawing. You can draw as many polygons as are required.
                </p>
                <select id="cleanseSelect" name="polygon" multiple="multiple" style="display: none;">
                </select>
                <ul id="cleanseList">
                </ul>
                <div style="margin: 0; padding: 0;">
                    <input type="submit" id="cleanseSubmit" value="Done" disabled="disabled"/>
                </div>
                </form>
            </div>
            <h3><a href="#">Project Menu</a></h3>
            <tags:project-menu project="${project}"/>
        </div>
        <div id="projectMap"></div>
        <div class="clearboth">&nbsp;</div>
        </div>
    </jsp:body>
</tags:page>

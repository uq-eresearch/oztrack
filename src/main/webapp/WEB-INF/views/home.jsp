<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Home">
    <jsp:attribute name="description">
        OzTrack is a free-to-use web-based platform for analysing and visualising
        individual-based animal location data. Upload your tracking data now.
    </jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/js/openlayers/theme/default/style.css" type="text/css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/js/openlayers/theme/default/google.css" type="text/css">
        <style type="text/css">
            #homeMap {
                height: 500px;
                background-color: #e6e6c0;
                padding: 8px;
                -khtml-border-radius: 10px;
                -webkit-border-radius: 10px;
                -moz-border-radius: 10px;
                -ms-border-radius: 10px;
                -o-border-radius: 10px;
                border-radius: 10px;
            }
            #map-instructions-container {
                position: relative;
                z-index: 1100;
            }
            #map-instructions {
                position: absolute;
                top: 12px;
                left: 302px;
                width: 318px;
                background-color: white;
                opacity: 0.7;
                text-align: center;
                font-size: 12px;
                padding: 5px;
                -khtml-border-radius: 5px;
                -webkit-border-radius: 5px;
                -moz-border-radius: 5px;
                -ms-border-radius: 5px;
                -o-border-radius: 5px;
                border-radius: 5px;
            }
            .home-popup {
                margin-top: 0;
                padding: 0 10px;
            }
            .home-popup-title {
                margin-botom: 1em;
                font-size: 15px;
                font-weight: bold;
            }
            .home-popup-attr-name {
                font-weight: bold;
                margin-bottom: 0.25em;
            }
            .home-popup-attr-value {
                margin-bottom: 0.75em;
            }
        </style>
        <script src="${pageContext.request.scheme}://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/LoadingPanel.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/home.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navHome').addClass('active');
                map = createHomeMap('homeMap');
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="row">
            <div class="span9">
                <div id="welcome">
                    ${text}
                </div>
            </div>
            <div class="span3">
                <div id="welcome-buttons">
                    <a class="btn btn-large btn-block btn-primary" href="${pageContext.request.contextPath}/projects/new">Create Project</a>
                    <a class="btn btn-large btn-block" href="${pageContext.request.contextPath}/projects">View Projects</a>
                </div>
            </div>
        </div>
        <div style="clear: both;"></div>
        <div id="homeMap">
            <div id="map-instructions-container">
                <div id="map-instructions">
                    Click markers to view project details
                </div>
            </div>
        </div>
    </jsp:body>
</tags:page>

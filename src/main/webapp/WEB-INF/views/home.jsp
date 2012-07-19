<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Home">
	<jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/style.css"/>" type="text/css">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/google.css"/>" type="text/css">
        <style type="text/css">
            #welcome {
                margin: 0 0 0 0;
                float: left;
                text-align: justify;
                z-index: 10000000;
                padding: 16px 16px 8px 16px;
                background-color:#e6e6c0;
                -khtml-border-radius: 10px;
                -webkit-border-radius: 10px;
                -moz-border-radius: 10px;
                -ms-border-radius: 10px;
                -o-border-radius: 10px;
                border-radius: 10px;
            }
            #welcome h1 {
                font-size: 12pt;
            }
            #welcome p {
                margin: 1em 1em 1em 0;
            }
            #homeMap {
                margin: 20px 0 0 0;
                height: 500px;
                background-color:#e6e6c0;
                padding: 8px;
                -khtml-border-radius: 10px;
                -webkit-border-radius: 10px;
                -moz-border-radius: 10px;
                -ms-border-radius: 10px;
                -o-border-radius: 10px;
                border-radius: 10px;
            }
        </style>
        <script src="http://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="<c:url value="/js/home.js"/>"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navHome').addClass('active');
                map = createHomeMap('homeMap');
            });
        </script>
    </jsp:attribute>
	<jsp:body>
        <div id="welcome">
            ${text}
            <p style="margin-bottom: 4px;"><a href="javascript:void(0)" onclick="$('#welcome').fadeOut();">Close</a></p>
        </div>
        <div style="clear: both;"></div>
        <div id="homeMap"></div>
	</jsp:body>
</tags:page>

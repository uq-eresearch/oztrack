<%@ include file="header.jsp" %>
<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}" /></script>
<script type="text/javascript" src="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/keydragzoom/src/keydragzoom_packed.js" /></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js" /></script>
<script type="text/javascript" src="js/oztrackmaps.js" /></script>

<h1><c:out value="${project.title}"/></h1>

    <div class="mapTool">
        <div id="projectMap"></div>
        <div id="projectMapOptions"></div>
        <div class="clearboth">&nbsp;</div>
    </div>


    <p><c:out value="${rResult}"/></p>
       <div style="border: 1px solid red;"><c:out value="${rOutput}"/></div>
<%@ include file="footer.jsp" %>

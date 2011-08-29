<%@ include file="header.jsp" %>
<!--script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}"></script-->
<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js"></script>
<script type="text/javascript" src="js/oztrackmaps.js"></script>

<h1><c:out value="${project.title}"/></h1>

<p style="color:red;"><c:out value="${errorMessage}"/></p>

<div class="mapTool">
<div id="projectMap"></div>
<div id="projectMapOptions">

<input type="hidden" value="${project.id}" id="projectId"/>
<input type="hidden" value="${project.boundingBox}" id="projectBoundingBox"/>

    <div id="mapDescription"></div>

    <form method="POST" id="mapToolForm" onsubmit="updateProjectMap(); return false;">

    <div id="accordion">
        <h3><a href="#">Animals</a></h3>
        <div>
            <style>
                .legend {
                    border-collapse: collapse;
                    border-spacing: 2px;
                }
                .legend td {
                    border: 5px solid black;
                    border-color: white;
                 }
                .legend .legend-colour {
                    width: 12px;
                }
            </style>
            <table class="legend">
            <c:forEach items="${projectAnimalsList}" var="animal">
                <tr>
                    <td class="legend-colour" id="legend-colour-${animal.projectAnimalId}">&nbsp;</td>
                    <td class="legend-value"><a href="#" onclick="zoomToTrack(${animal.projectAnimalId});">${animal.animalName}</a></td>
                </tr>
            </c:forEach>
            </table>
        </div>

        <h3><a href="#">Date Range</a></h3>
        <div>
           <label class="shortInputLabel" for="fromDatepicker">Date From:</label>
            <input id="fromDatepicker" class="shortInputBox"/>

            <label for="toDatepicker" class="shortInputLabel">Date To:</label>
            <input id="toDatepicker" class="shortInputBox"/>
        </div>

        <h3><a href="#">Map Layers</a></h3>
        <div>
            <div id="layerSwitcherDiv"></div>
            <div>
             <label for="mapQueryTypeSelect">Query Type:</label>
             <select id="mapQueryTypeSelect">
                    <c:forEach items="${mapQueryTypeList}" var="mapQueryType">
                        <c:if test="${!fn:contains(mapQueryType,'ALL_')}">
                          <option value="${mapQueryType}"><c:out value="${mapQueryType.displayName}"/></option>
                        </c:if>
                    </c:forEach>
             <select>
             </div>
        </div>
    </div>
    <div class="formButton"><input type="submit" id="projectMapSubmit" value="Refresh"/></div>
    </form>
</div>
<div class="clearboth">&nbsp;</div>
</div>



<%@ include file="footer.jsp" %>

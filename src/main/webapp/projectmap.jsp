<%@ include file="header.jsp" %>
<!--script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}"></script-->
<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js"></script>
<script type="text/javascript" src="js/oztrackmaps.js"></script>
<script type="text/javascript"> 
	mapPage = true;
</script>

<div class="mapTool">
<div id="projectMapOptions">
<h1><c:out value="${project.title}"/></h1>

<p style="color:red;"><c:out value="${errorMessage}"/></p>
<input type="hidden" value="${project.id}" id="projectId"/>
<input type="hidden" value="${project.boundingBox}" id="projectBoundingBox"/>

    <div id="mapDescription"></div>

    <form method="POST" id="mapToolForm" onsubmit="addProjectMapLayer(); return false;">

    <div id="accordion">

        <h3><a href="#">Map Layers</a></h3>
        <div id="customLayerSwitcher"></div>

        <h3><a href="#">Navigation</a></h3>

        <div>
            <c:forEach items="${projectAnimalsList}" var="animal">
            	<div class="accordianHead">
                    <span class="smallSquare" id="legend-colour-${animal.projectAnimalId}"></span>
                    <a href="#">${animal.animalName}</a>
            	</div>	
        		<div>
        			<div class="animalMenu">
                    <input type="checkbox" class="shortInputCheckbox" name="animalCheckbox" id="select-animal-${animal.projectAnimalId}" value="${animal.projectAnimalId}"/>
                    Show/Hide All
                    <script type="text/javascript">
                        $('input[id=select-animal-${animal.projectAnimalId}]').change(function() {
                                toggleAllAnimalFeatures("${animal.projectAnimalId}",this.checked);
                        });
                    </script>
                    &nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onclick="zoomToTrack(${animal.projectAnimalId});">Zoom</a>&nbsp;&nbsp;
                    <a href="<c:url value="exportKML"><c:param name="projectId" value="${project.id}"/><c:param name="animalId" value="${animal.id}"/></c:url>">
        			KML</a>
        			</div>
        			<div id="animalInfo-${animal.projectAnimalId}" class="animalInfo"></div>
        		</div>
            </c:forEach>
        </div>

        
        <h3><a href="#">Home Range Calculator</a></h3>
        
	    <div>
	        <label class="shortInputLabel">Add A Layer:</label> <br>
	            <b>Date Range:</b>
	            <table>
	             <tr>
	                <td><b>From:</b></td>
	                <td><input id="fromDatepicker" class="shortInputBox"/></td>
	             </tr>
	             <tr>
	                <td><b>To:</b></td>
	                <td><input id="toDatepicker" class="shortInputBox"/></td>
	             </tr>
	            </table>
	         	<style>
	                .mapQueryType {
	                    border-collapse: collapse;
	                    border-spacing: 2px;
	                }
	                .mapQueryType td {
	                    border: 5px solid white;
	                 }
	           </style>
	                <table class="mapQueryType">
	                <c:forEach items="${mapQueryTypeList}" var="mapQueryType">
	                    <c:if test="${!fn:contains(mapQueryType,'ALL_')}">
	                        <tr>
	                         <td><input class="shortInputRadioButton" type="radio" name="mapQueryTypeSelect" value="${mapQueryType}"/></td>
	                         <td id="${mapQueryType}"><c:out value="${mapQueryType.displayName}"/></td>
	                        </tr>
	                    </c:if>
	                </c:forEach>
	                </table>
	        </div>
    </div>
    <div class="formButton"><input type="submit" id="projectMapSubmit" value="Add a New Layer"/></div>
    </form>
</div>
<div id="projectMap"></div>
<div class="clearboth">&nbsp;</div>
</div>



<%@ include file="footer.jsp" %>

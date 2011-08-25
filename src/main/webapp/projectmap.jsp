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

    <form method="POST">

        <input type="hidden" value="${project.id}" id="projectId"/>
        <input type="hidden" value="${project.boundingBox}" id="projectBoundingBox"/>

        <label class="shortInputLabel" for="fromDatepicker">Date From:</label>
        <input id="fromDatepicker" class="shortInputBox"/>

        <label for="toDatepicker" class="shortInputLabel">Date To:</label>
        <input id="toDatepicker" class="shortInputBox"/>

         <br>
         <label for="mapQueryTypeSelect">Query Type:</label>
         <select id="mapQueryTypeSelect">
                <c:forEach items="${mapQueryTypeList}" var="mapQueryType">
                    <c:if test="${!fn:contains(mapQueryType,'ALL_')}">
                      <option value="${mapQueryType}"><c:out value="${mapQueryType.displayName}"/></option>
                    </c:if>
                </c:forEach>
         <select>

         <div class="formButton"><input type="submit" id="projectMapSubmit" value="Update Map"/></div>

    </form>




</div>
<div class="clearboth">&nbsp;</div>
</div>



<%@ include file="footer.jsp" %>

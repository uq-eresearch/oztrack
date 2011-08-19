<%@ include file="header.jsp" %>
<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}"></script>
<script type="text/javascript" src="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/keydragzoom/src/keydragzoom_packed.js"></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js"></script>
<script type="text/javascript" src="js/oztrackmaps.js"></script>

<h1><c:out value="${project.title}"/></h1>

<p style="color:red;"><c:out value="${errorMessage}"/></p>

<div class="mapTool">
<div id="projectMap"></div>
<div id="projectMapOptions" style="border:solid 1px red">

    <form:form commandName="searchQuery" method="POST" name="mapSearchQuery">

        <input type="hidden" value="${searchQuery.project.id}" id="projectId"/>

            <label class="shortInputLabel" for="fromDate">Date From:</label>
            <form:input path="fromDate" id="fromDatepicker" cssClass="shortInputBox"/>

            <label for="toDate" class="shortInputLabel">Date To:</label>
            <form:input path="toDate" id="toDatepicker" cssClass="shortInputBox"/>
            <br>
            <label for="mapQueryType">Query Type:</label>
            <form:select path="mapQueryType">
                <c:forEach items="${mapQueryTypeList}" var="mapQueryType">
                    <c:if test="${!fn:contains(mapQueryType,'ALL_')}">
                     <form:option value="${mapQueryType}" label="${mapQueryType.displayName}"/>
                    </c:if>
                </c:forEach>
            </form:select>

        <div class="formButton"><input type="submit" value="Update Map"/></div>

    </form:form>




</div>
<div class="clearboth">&nbsp;</div>
</div>

<%@ include file="footer.jsp" %>

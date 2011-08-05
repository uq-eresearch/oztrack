<%@ include file="header.jsp" %>
<script type="text/javascript" src="http://maps.google.com/maps?file=api&v=2&key=${initParam['api-key']}" /></script>
<script type="text/javascript" src="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/keydragzoom/src/keydragzoom_packed.js" /></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js" /></script>
<script type="text/javascript" src="js/oztrackmaps.js" /></script>

<h1><c:out value="${project.title}"/></h1>

    <div class="mapTool">
        <div id="projectMap"></div>
        <div id="projectMapOptions">
        <div>             <form id="projectMapForm">
            Display Layers:<br>
             <input type="checkbox" name="mapLayer" value="Points"> Points <br>
             <input type="checkbox" name="mapLayer" value="Trajectories"> Trajectories<br>
             <br>
             <input type="button" value = "View" onClick="updateMapView()"/>
             </form>

        </div> </div>
        <div class="clearboth">&nbsp;</div>
    </div>



   <p style="color:red;"><c:out value="${errorStr}"/></p>

    <h2>Data In</h2>

   <div style="border: 1px solid red;">
       <c:forEach items="${rDataIn}" var="r">
        <c:forEach items="${r}" var="x">
         <c:out value="${x}"/>
        </c:forEach>
        <br>
       </c:forEach>
   </div>

    <div style="border: 1px solid green;">

      <c:forEach items="${rAnimalsIn}" var="a">
        <c:forEach items="${a}" var="s">
         <c:out value="${s}"/>
        </c:forEach>
        <br>
       </c:forEach>

      </div>


    <h2>Data Out</h2>

   <div style="border: 1px solid red;">
       <c:forEach items="${posFixNames}" var="name">
        <c:out value="${name} "/>
       </c:forEach>
        <br>
       <c:forEach items="${rDataOut}" var="r">
        <c:forEach items="${r}" var="x">
         <c:out value="${x}"/>
        </c:forEach>
        <br>
       </c:forEach>
   </div>

    <div style="border: 1px solid green;">
       <c:forEach items="${animalRefNames}" var="animal">
          <c:out value="${animal} "/>
       </c:forEach>
         <br>
       <c:forEach items="${rAnimalsOut}" var="a">
        <c:forEach items="${a}" var="s">
         <c:out value="${s}"/>
        </c:forEach>
        <br>
       </c:forEach>
      </div>

<%@ include file="footer.jsp" %>

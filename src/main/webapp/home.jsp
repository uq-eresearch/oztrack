<%@ include file="header.jsp" %>
<script src="http://maps.google.com/maps/api/js?v=3.2&sensor=false"></script>
<script type="text/javascript" src="js/openlayers/OpenLayers.js" /></script>
<script type="text/javascript" src="js/homemap.js" /></script>
<script type="text/javascript"> 
    mapPage = true;
</script>    

    <div class="mapTool">
        <div id="homeMapOptions">
            <h1>Welcome to OzTrack Beta!</h1>
        
            <p style="font-size:1.2em">OzTrack is a free-to-use web-based platform for the analysis and 
            visualisation of animal tracking data. It was developed for the Australian 
            animal tracking community but can be used to determine, measure and plot 
            home-ranges for animals anywhere in the world.</p>
            <!-- Projects marked as pins in the map. When user clicks on pin, the bubble 
            opens up with project details including any pictures that have been uploaded. 
            If the data is publically available then zoom in to show trajectory data only. 
            If not publically available go to screen which shows contact details 
            of the user group. Data not presently available for public viewing, 
            contact user for further details. -->
            
            <div id="logos" style="margin: 75px 40px 0 40px;">
                <a href="http://nectar.org.au/"><img src="images/nectar-logo.png" width="140px" height="32px"/></a>
                <a href="http://ands.org.au/"><img src="images/ands-logo.png" width="90px" height="40px" style="margin-top: -8px;"/></a>
                <a href="http://itee.uq.edu.au/~eresearch/"><img src="images/uq_logo.png" width="140px" height="40px"/></a>
            </div>
        </div>
        <div id="homeMap"></div>
        <div class="clearboth">&nbsp;</div>  
    </div>


<%@ include file="footer.jsp" %>



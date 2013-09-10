<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Environmental layers">
    <jsp:attribute name="description">
        OzTrack is a free-to-use web-based platform for analysing and visualising
        individual-based animal location data. OzTrack incorporates a number of
        external environmental layers.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navAbout').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/about">About</a>
        &rsaquo; <span class="active">Environmental layers</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Environmental layer credits</h1>
        <ul class="icons">
            <li class="layer">
                <a href="https://developers.google.com/maps/documentation/javascript/maptypes">Google Satellite</a>,
                <a href="https://developers.google.com/maps/documentation/javascript/maptypes">Google Physical</a>,
                <a href="https://developers.google.com/maps/documentation/javascript/maptypes">Google Streets</a>,
                <a href="https://developers.google.com/maps/documentation/javascript/maptypes">Google Hybrid</a>:<br />
                accessed via the <a href="https://developers.google.com/maps/documentation/javascript/">Google Maps JavaScript API</a>;<br />
                used according to the <a href="https://developers.google.com/maps/terms">Google Maps/Google Earth APIs Terms of Service</a>.
            </li>
            <li class="layer">
                <a href="http://tile.openstreetmap.org/">OpenStreetMap</a>:<br />
                accessed via the <a href="https://wiki.openstreetmap.org/wiki/OpenLayers">OpenLayers JavaScript API</a>;<br />
                used according to the <a href="https://wiki.openstreetmap.org/wiki/Tile_usage_policy">OpenStreetMap tile usage policy</a>.
            </li>
            <li class="layer">
                <a href="http://www.gebco.net/data_and_products/gridded_bathymetry_data/">The GEBCO_08 Grid, version 20100927</a> (bathymetry, elevation):<br />
                provided by <a href="http://www.gebco.net/">The General Bathymetric Chart of the Oceans (GEBCO)</a>;<br />
                hosted for purposes of scientific research, environmental conservation, and education according to its
                <a href="http://www.gebco.net/data_and_products/gridded_bathymetry_data/documents/gebco_08.pdf">terms of use</a>.
            </li>
            <li class="layer">
                <a href="http://www.ga.gov.au/earth-observation/landcover.html">The National Dynamic Land Cover Dataset</a>:<br />
                provided by <a href="http://www.ga.gov.au/">Geoscience Australia</a>;<br />
                hosted under the <a href="http://creativecommons.org/licenses/by/2.5/au/">Creative Commons Attribution 2.5 Australia</a> licence.
            </li>
            <li class="layer">
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B116AACA6-9E11-43E6-AD68-75AE380504CD%7D">Present Major Vegetation Groups - National Vegetation Information System Version 4.1</a>,<br />
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B245434BF-95D1-4C3E-8104-EC4B2988782D%7D">Present Major Vegetation Subgroups - National Vegetation Information System Version 4.1</a>:<br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under Agreement for the Supply of Data with the Department of Environment, Water, Population and Communities, Australian Government.
            </li>
            <li class="layer">
                <a href="http://data.auscover.org.au/geonetwork/srv/en/main.home?uuid=3535a8c1-940e-4f60-b55b-24185730acba">Fire Frequency - AVHRR, Charles Darwin University algorithm, Australia coverage</a>:<br />
                provided by <a href="http://www.cdu.edu.au/">Charles Darwin University (CDU)</a> via <a href="http://data.auscover.org.au/">AusCover</a>;<br />
                hosted under the <a href="http://creativecommons.org/licenses/by/3.0">Creative Commons Attribution 3.0</a> licence.
            </li>
            <li class="layer">
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={C4B70940-75BC-4114-B935-D28EE8A52937}">Collaborative Australian Protected Areas Database (CAPAD) 2010</a>,<br />
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={0E24A4B5-BA44-48D5-AF2F-7F4749F4EA2D}">Collaborative Australian Protected Areas Database (CAPAD) 2010 - Restricted</a>:<br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under Agreement for the Supply of Data with the Department of Environment, Water, Population and Communities, Australian Government.
            </li>
            <li class="layer">
                <a href="​http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B4970516C-6F4A-4B1E-AF33-AB6BDE6B008A%7D">Collaborative Australian Protected Areas Database (CAPAD) 2010 - Marine</a>,<br />
                <a href="​http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B905AD083-39A0-41C6-B2F9-CBF5E0B86A3C%7D">Collaborative Australian Protected Areas Database (CAPAD) 2010 - Marine - Restricted</a>:<br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under Agreement for the Supply of Data with the Department of Environment, Water, Population and Communities, Australian Government.
            </li>
            <li class="layer">
                <a href="​http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={052C61B4-3662-4842-8B4D-15DC57B355FE}">Commonwealth Marine Reserves Network (2012)</a><br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under Agreement for the Supply of Data with the Department of Environment, Water, Population and Communities, Australian Government.
            </li>
            <li class="layer">
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId={FA68F769-550B-4605-A0D5-50B10ECD0EB9}">Natural Resource Management (NRM) Regions (2010)</a>:<br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under the <a href="http://creativecommons.org/licenses/by/2.5/au/">Creative Commons Attribution 2.5 Australia</a> licence.
            </li>
            <li class="layer">
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B573FA186-1997-4F8B-BCF8-58B5876A156B%7D">Interim Biogeographic Regionalisation for Australia (IBRA), Version 7 (Regions)</a>,<br />
                <a href="​http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7BC88F4317-42B0-4D4B-AC5D-47F6ACF1A24F%7D">Interim Biogeographic Regionalisation for Australia (IBRA), Version 7 (Subregions)</a>:<br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under the <a href="http://creativecommons.org/licenses/by/3.0/au/">Creative Commons Attribution 3.0 Australia</a> licence.
            </li>
            <li class="layer">
                <a href="http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7B30DA5FD4-AE08-405B-9F55-7E1833C230A4%7D">Integrated Marine and Coastal Regionalisation of Australia (IMCRA) v4.0 - Provincial Bioregions</a>,<br />
                <a href="​http://www.environment.gov.au/metadataexplorer/full_metadata.jsp?docId=%7BA0D9F8EE-4261-438A-8ADE-EFF664EFF55C%7D">Integrated Marine and Coastal Regionalisation of Australia (IMCRA) v4.0 - Meso-scale Bioregions</a>:<br />
                provided by the <a href="http://www.environment.gov.au/">Department of Environment, Water, Population and Communities, Australian Government</a>;<br />
                hosted under Agreement for the Supply of Data with the Department of Environment, Water, Population and Communities, Australian Government.
            </li>
        </ul>
    </jsp:body>
</tags:page>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="About">
    <jsp:attribute name="description">
        OzTrack is a free-to-use web-based platform for analysing and visualising
        individual-based animal location data. The OzTrack project is supported
        by the NeCTAR e-Research Tools program.
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
        &rsaquo; <span class="active">About</span>
    </jsp:attribute>
    <jsp:body>
        ${text}
        <h2>Software information</h2>
        <p>OzTrack is freely available, open-source software. See the <a target="_blank" href="https://github.com/uq-eresearch/oztrack/">OzTrack project on GitHub</a>.</p>
        <p>The diagram below gives an overview of the software architecture for OzTrack:</p>
        <p>
            <img src="${pageContext.request.contextPath}/img/architecture.png" style="border: 1px solid #D0D0BB;"/>
        </p>
        <h3>Server components</h3>
        <p>OzTrack uses the following server components:</p>
        <ul class="icons">
            <li class="software"><a target="_blank" href="http://www.oracle.com/technetwork/java/index.html">Java</a>: general-purpose, object-oriented language</li>
            <li class="software"><a target="_blank" href="http://tomcat.apache.org/">Tomcat</a> or <a target="_blank" href="http://www.eclipse.org/jetty/">Jetty</a>: HTTP server and Java Servlet container</li>
            <li class="software"><a target="_blank" href="http://httpd.apache.org/">Apache HTTP Server</a>: open-source HTTP server</li>
            <li class="software"><a target="_blank" href="http://shibboleth.net/products/service-provider.html">Shibboleth Service Provider</a>: integrates with Apache HTTP Server to provide single-sign-on</li>
            <li class="software"><a target="_blank" href="http://www.postgresql.org/">PostgreSQL</a>: object-relational database management system</li>
            <li class="software"><a target="_blank" href="http://postgis.refractions.net/">PostGIS</a>: adds support for geographic objects to the PostgreSQL</li>
            <li class="software"><a target="_blank" href="http://www.r-project.org/">R</a>: language and environment for statistical computing and graphics</li>
            <li class="software"><a target="_blank" href="http://www.rforge.net/Rserve/">Rserve (Server, REngine Java client)</a>: TCP/IP server allowing other programs (e.g. Java) to use facilities of R</li>
        </ul>
        <h3>Java technologies</h3>
        <p>The OzTrack web application uses these Java technologies:</p>
        <ul class="icons">
            <li class="software"><a target="_blank" href="http://www.oracle.com/technetwork/java/javaee/servlet/index.html">Java Servlets, JSP, JSTL</a>: Java technologies for building Web-based applications</li>
            <li class="software"><a target="_blank" href="http://www.springsource.org/">Spring Framework (Context, Web MVC), Spring Security</a>: application development framework for Java</li>
            <li class="software"><a target="_blank" href="http://www.hibernate.org/">Hibernate</a>: Java persistence framework</li>
            <li class="software"><a target="_blank" href="http://www.hibernatespatial.org/">Hibernate Spatial</a>: extension to Hibernate for handling geographic data</li>
            <li class="software"><a target="_blank" href="http://www.geotools.org/">GeoTools (Main, XML, EPSG-HSQL, XSD-KML, Swing)</a>: Java library providing tools for geospatial data</li>
            <li class="software"><a target="_blank" href="http://quartz-scheduler.org/">Quartz</a>: Java job scheduling service</li>
            <li class="software"><a target="_blank" href="http://hc.apache.org/httpcomponents-client-ga/">Apache HttpComponents HttpClient</a>: Java implementation of client side of HTTP protocol</li>
            <li class="software"><a target="_blank" href="http://commons.apache.org/">Apache Commons (Lang, IO, Pool, Email, FileUpload)</a>: reusable Java components</li>
            <li class="software"><a target="_blank" href="http://poi.apache.org/">Apache POI</a>: Java API for Microsoft Documents</li>
            <li class="software"><a target="_blank" href="http://opencsv.sourceforge.net/">opencsv</a>: CSV (Comma-Separated Values) parser library for Java</li>
            <li class="software"><a target="_blank" href="http://freemarker.sourceforge.net/">FreeMarker</a>: Java template engine</li>
            <li class="software"><a target="_blank" href="https://github.com/samskivert/jmustache">JMustache</a>: Java implementation of the Mustache template language</li>
            <li class="software"><a target="_blank" href="http://www.json.org/java/index.html">JSON in Java</a>: Java implementaiton of the JSON data interchange format</li>
            <li class="software"><a target="_blank" href="http://www.google.com/recaptcha">reCAPCHA</a>: prevents website abuse from bots</li>
            <li class="software"><a target="_blank" href="http://flywaydb.org/">Flyway</a>: database migration framework for Java</li>
        </ul>
        <h3>R packages</h3>
        <p>The OzTrack analysis tools use these R packages:</p>
        <ul class="icons">
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/adehabitatHR/index.html">adehabitatHR</a>: Home range Estimation
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/adehabitatMA/index.html">adehabitatMA</a>: Tools to Deal with Raster Maps
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/alphahull/index.html">alphahull</a>: Generalization of the convex hull of a sample of points in the plane
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/rgdal/index.html">rgdal</a>: Bindings for the Geospatial Data Abstraction Library
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/sp/index.html">maptools</a>: classes and methods for spatial data
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/shapefiles/index.html">shapefiles</a>: Read and Write ESRI Shapefiles
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/sp/index.html">sp</a>: classes and methods for spatial data
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/raster/index.html">raster</a>: Geographic data analysis and modeling
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/spatstat/index.html">spatstat</a>: Spatial Point Pattern analysis, model-fitting, simulation, tests
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/Grid2Polygons/index.html">Grid2Polygons</a>: Convert Spatial Grids to Polygons
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/RColorBrewer/index.html">RColorBrewer</a>: ColorBrewer palettes
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/googleVis/index.html">googleVis</a>: Interface between R and the Google Chart Tools
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/plotKML/index.html">plotKML</a>: Visualization of spatial and spatio-temporal objects in Google Earth
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/spacetime/index.html">spacetime</a>: classes and methods for spatio-temporal data
            </li>
            <li class="software">
                <a target="_blank" href="http://cran.r-project.org/web/packages/plyr/index.html">plyr</a>: Tools for splitting, applying and combining data
            </li>
        </ul>
        <h3>Web client technologies</h3>
        <ul class="icons">
            <li class="software"><a target="_blank" href="http://jquery.com/">jQuery</a>: JavaScript Library for rapid web development</li>
            <li class="software"><a target="_blank" href="http://ckeditor.com/">CKEditor</a>: HTML text editor for web content creation</li>
            <li class="software"><a target="_blank" href="http://acko.net/blog/farbtastic-jquery-color-picker-plug-in/">Farbtastic</a>: JavaScript colour picker</li>
            <li class="software"><a target="_blank" href="http://openlayers.org/">OpenLayers</a>: JavaScript library for displaying map data in the Web browser</li>
            <li class="software"><a target="_blank" href="http://trac.osgeo.org/proj4js/">Proj4js</a>: JavaScript library to transform point coordinates from one coordinate system to another</li>
            <li class="software"><a target="_blank" href="http://momentjs.com/">Moment.js</a>: JavaScript library for parsing, validating, manipulating, and formatting dates.</li>
            <li class="software"><a target="_blank" href="http://twitter.github.com/bootstrap/">Bootstrap</a>: front-end framework for web development</li>
            <li class="software"><a target="_blank" href="http://vitalets.github.com/x-editable/">X-editable</a>: in-place editing with Twitter Bootstrap</li>
            <li class="software"><a target="_blank" href="http://lesscss.org/">LESS</a>: extends CSS with dynamic behavior such as variables, mixins, operations, and functions</li>
        </ul>
        <h2>Environmental layers</h2>
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
        <h2>Artwork credits</h2>
        <ul class="icons">
            <li class="artwork">This website uses the <a target="_blank" href="http://www.famfamfam.com/lab/icons/silk/">Silk icon set</a> by Mark James.</li>
        </ul>
    </jsp:body>
</tags:page>

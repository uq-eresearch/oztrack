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
    <jsp:attribute name="sidebar"/>
    <jsp:body>
        ${text}
        <h2>Software information</h2>
        <p>OzTrack is freely available, open-source software. See the <a href="https://github.com/uq-eresearch/oztrack/">OzTrack project on GitHub</a>.</p>
        <p>The diagram below gives an overview of the software architecture for OzTrack:</p>
        <p>
            <img src="${pageContext.request.contextPath}/img/architecture.png" style="border: 1px solid #D0D0BB;"/>
        </p>
        <p>The OzTrack analysis tools use the following open-source R packages:</p>
        <ul class="icons">
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/adehabitatHR/index.html">adehabitatHR</a>: Home range Estimation
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/adehabitatMA/index.html">adehabitatMA</a>: Tools to Deal with Raster Maps
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/alphahull/index.html">alphahull</a>: Generalization of the convex hull of a sample of points in the plane
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/rgdal/index.html">rgdal</a>: Bindings for the Geospatial Data Abstraction Library
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/sp/index.html">maptools</a>: classes and methods for spatial data
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/shapefiles/index.html">shapefiles</a>: Read and Write ESRI Shapefiles
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/sp/index.html">sp</a>: classes and methods for spatial data
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/raster/index.html">raster</a>: Geographic data analysis and modeling
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/spatstat/index.html">spatstat</a>: Spatial Point Pattern analysis, model-fitting, simulation, tests
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/Grid2Polygons/index.html">Grid2Polygons</a>: Convert Spatial Grids to Polygons
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/RColorBrewer/index.html">RColorBrewer</a>: ColorBrewer palettes
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/googleVis/index.html">googleVis</a>: Interface between R and the Google Chart Tools
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/plotKML/index.html">plotKML</a>: Visualization of spatial and spatio-temporal objects in Google Earth
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/spacetime/index.html">spacetime</a>: classes and methods for spatio-temporal data
            </li>
            <li class="software">
                <a href="http://cran.r-project.org/web/packages/plyr/index.html">plyr</a>: Tools for splitting, applying and combining data
            </li>
        </ul>
        <h2>Artwork credits</h2>
        <ul class="icons">
            <li class="artwork">This website uses the <a href="http://www.famfamfam.com/lab/icons/silk/">Silk icon set</a> by Mark James.</li>
        </ul>
    </jsp:body>
</tags:page>

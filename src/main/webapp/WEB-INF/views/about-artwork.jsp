<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Artwork credits">
    <jsp:attribute name="description">
        OzTrack is a free-to-use web-based platform for analysing and visualising
        individual-based animal location data. OzTrack incorporates artwork from
        a number of sources.
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
        &rsaquo; <span class="active">Artwork credits</span>
    </jsp:attribute>
    <jsp:body>
        <h1>Artwork credits</h1>
        <ul class="icons">
            <li class="artwork">Icons from <a target="_blank" href="http://www.famfamfam.com/lab/icons/silk/">Silk icons</a> by Mark James.</li>
            <li class="artwork">Icons from <a target="_blank" href="http://glyphicons.com/">Glyphicons</a>.</li>
        </ul>
    </jsp:body>
</tags:page>

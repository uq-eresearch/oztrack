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
    <jsp:attribute name="head">
        <style type="text/css">
            #about-buttons .btn {
                margin: 9px 0;
                height: 60px;
                line-height: 40px;
            }
        </style>
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
        <h1>About OzTrack</h1>
        ${settings.aboutText}
        <h2>Further information</h2>
        <div id="about-buttons" class="row">
            <c:if test="${not empty settings.peopleText}">
            <div class="span6">
                <a class="btn btn-large btn-block" href="${pageContext.request.contextPath}/about/people">People involved in OzTrack</a>
            </div>
            </c:if>
            <c:if test="${not empty settings.publicationsText}">
            <div class="span6">
                <a class="btn btn-large btn-block" href="${pageContext.request.contextPath}/about/publications">Publications</a>
            </div>
            </c:if>
            <div class="span6">
                <a class="btn btn-large btn-block" href="${pageContext.request.contextPath}/about/software">Software information</a>
            </div>
            <div class="span6">
                <a class="btn btn-large btn-block" href="${pageContext.request.contextPath}/about/layers">Environmental layers</a>
            </div>
            <div class="span6">
                <a class="btn btn-large btn-block" href="${pageContext.request.contextPath}/about/artwork">Artwork credits</a>
            </div>
            <div class="span6">
            </div>
        </div>
    </jsp:body>
</tags:page>

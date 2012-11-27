<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Update '${animal.animalName}'">
    <jsp:attribute name="description">
        Update animal '${animal.animalName}' in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/optimised/farbtastic.css" type="text/css" />
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/optimised/farbtastic.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuAnimals').addClass('active');
                $('#colorpicker').farbtastic('#colour');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}/animals">Animals</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/animals/${animal.id}">${animal.animalName}</a>
        &rsaquo; <span class="active">Edit</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <form:form cssClass="form-horizontal form-bordered" action="/animals/${animal.id}" commandName="animal" method="PUT">
            <fieldset>
            <div class="legend">Animal details</div>
            <div class="control-group">
                <label class="control-label" for="projectAnimalId">Animal ID</label>
                <div class="controls">
                    <form:input path="projectAnimalId" id="projectAnimalId"/>
                    <span class="help-inline">
                        <form:errors path="projectAnimalId" cssClass="formErrors"/>
                    </span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="animalName">Name</label>
                <div class="controls">
                    <form:input path="animalName" id="animalName"/>
                    <span class="help-inline">
                        <form:errors path="animalName" cssClass="formErrors"/>
                    </span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="speciesName">Species</label>
                <div class="controls">
                    <form:input path="speciesName" id="speciesName"/>
                    <span class="help-inline">
                        <form:errors path="speciesName" cssClass="formErrors"/>
                    </span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="animalDescription">Description</label>
                <div class="controls">
                    <form:textarea style="width: 400px; height: 100px;" path="animalDescription" id="animalDescription"/>
                    <span class="help-inline">
                        <form:errors path="animalDescription" cssClass="formErrors"/>
                    </span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="colour">Colour</label>
                <div class="controls">
                    <form:input path="colour" id="colour"
                        onclick="$('#colorpicker').fadeIn();"
                        onfocus="$('#colorpicker').fadeIn();"
                        onblur="$('#colorpicker').fadeOut();"
                        style="background-color: ${animal.colour};"/>
                    <div id="colorpicker" style="display: none; position: absolute; padding: 10px 13px 0 13px;"></div>
                    <span class="help-inline">
                        <form:errors path="colour" cssClass="formErrors"/>
                    </span>
                </div>
            </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="Update"/>
                <a class="btn" href="${pageContext.request.contextPath}/animals/${animal.id}">Cancel</a>
            </div>
        </form:form>
    </jsp:body>
</tags:page>

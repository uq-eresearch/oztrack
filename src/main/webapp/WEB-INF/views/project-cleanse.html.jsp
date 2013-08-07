<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.data.model.types.MapLayerType" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="/WEB-INF/functions.tld" prefix="oztrack" %>
<tags:page title="${project.title}: Edit Data" fluid="true">
    <jsp:attribute name="description">
        Edit data in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="navExtra">
        <a class="btn btn-inverse" href="/projects/${project.id}"><span class="icon-chevron-left icon-white"></span> Back to project</a>
    </jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/optimised/openlayers.css" type="text/css">
        <style type="text/css">
            #main {
                padding-bottom: 0;
            }
            ul#cleanse-list {
                margin: 10px 0;
                padding-left: 0;
            }
            ul#cleanse-list li {
                margin: 2px 0;
                list-style: none;
                background-repeat: no-repeat;
                background-position: left middle;
                line-height: 22px;
                padding: 0 0 5px 28px;
            }
            ul#cleanse-list li {
                background-image: url(/js/openlayers/theme/default/img/draw_polygon_on.png);
            }
            #projectMapOptions {
            }
            #animalList {
                height: 90px;
                border: 1px solid #ccc;
                overflow-y: scroll;
            }
            .animalCheckbox {
                float: left;
                width: 15px;
                margin: 0;
                padding: 0;
            }
            .animalCheckbox input[type="checkbox"] {
                margin: 0 0 2px 0;
            }
            .smallSquare {
                float: left;
                width: 12px;
                height: 12px;
                margin: 2px 5px;
                padding: 0;
            }
            .animalLabel {
                margin-left: 40px;
                padding: 0;
            }
            .layerInfoTitle {
                padding: 5px 0;
            }
            .layerExplanationExpander {
                display: block;
                padding: 4px 0 9px 0;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script src="${pageContext.request.scheme}://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/optimised/openlayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/project-map.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/project-cleanse.js"></script>
        <script type="text/javascript">
            function submitCleanseForm(operation, mode) {
                var params = {operation: operation, mode: mode};
                $.extend(params, OzTrack.serializeHash('#form-context,#form-' + mode));
                cleanseMap.submitCleanseRequest(operation, params);
            }
            function submitKalmanFilter() {
                var params = {
                    projectId: ${project.id},
                    analysisType: 'KALMAN',
                    fromDate: $('#fromDate').val(),
                    toDate: $('#toDate').val(),
                    animalIds:
                        $('input[name=animalIds]:not(:disabled):checked')
                            .map(function() {return $(this).val();})
                            .toArray()
                            .join(',')
                };
                $('#form-kalman-filter .paramField-KALMAN').each(function() {
                    if ($(this).attr('type') == 'checkbox') {
                        params[$(this).attr('name')] = $(this).is(':checked') ? 'true' : 'false';
                    }
                    else if ($(this).val()) {
                        params[$(this).attr('name')] = $(this).val();
                    }
                });
                cleanseMap.createKalmanFilterAnalysis(params);
            }
            function submitKalmanFilterSst() {
                var params = {
                    projectId: ${project.id},
                    analysisType: 'KALMAN_SST',
                    fromDate: $('#fromDate').val(),
                    toDate: $('#toDate').val(),
                    animalIds:
                        $('input[name=animalIds]:not(:disabled):checked')
                            .map(function() {return $(this).val();})
                            .toArray()
                            .join(',')
                };
                $('#form-kalman-filter-sst .paramField-KALMAN_SST').each(function() {
                    if ($(this).attr('type') == 'checkbox') {
                        params[$(this).attr('name')] = $(this).is(':checked') ? 'true' : 'false';
                    }
                    else if ($(this).val()) {
                        params[$(this).attr('name')] = $(this).val();
                    }
                });
                cleanseMap.createKalmanFilterSstAnalysis(params);
            }
            function deleteKalmanFilter(analysisId) {
                cleanseMap.deleteAnalysis(analysisId);
            }
            function deleteKalmanFilterSst(analysisId) {
                cleanseMap.deleteAnalysis(analysisId);
            }
            function applyKalmanFilter(analysisId) {
                $('#kalmanFilterApply-' + analysisId).prop('disabled', true);
                cleanseMap.applyKalmanFilterAnalysis(analysisId);
            }
            function applyKalmanFilterSst(analysisId) {
                $('#kalmanFilterSstApply-' + analysisId).prop('disabled', true);
                cleanseMap.applyKalmanFilterSstAnalysis(analysisId);
            }
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $("#projectMapOptionsTabs").tabs();
                $('#fromDateVisible').datepicker({
                    altField: "#fromDate",
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    defaultDate: new Date(${projectDetectionDateRange.minimum.time})
                }).change(function(e) {
                    cleanseMap.setFromDate($('#fromDate').val());
                    $('#toDateVisible').datepicker('hide');
                });
                $('#toDateVisible').datepicker({
                    altField: "#toDate",
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    defaultDate: new Date(${projectDetectionDateRange.maximum.time})
                }).change(function() {
                    cleanseMap.setToDate($('#toDate').val());
                    $('#toDateVisible').datepicker('hide');
                });
                <c:forEach items="${analysisTypeList}" var="analysisType">
                <c:forEach items="${analysisType.parameterTypes}" var="parameterType">
                <c:if test="${parameterType.dataType == 'date'}">
                $('#${parameterType.identifier}Visible').datepicker({
                    altField: '#${parameterType.identifier}'
                });
                </c:if>
                </c:forEach>
                </c:forEach>
                <c:forEach items="${projectAnimalsList}" var="animal">
                $('#select-animal-${animal.id}').change(function() {
                    $('#select-animal-all').prop('checked', $('.select-animal:not(:checked)').length == 0);
                    cleanseMap.setAnimalVisible("${animal.id}", this.checked);
                });
                </c:forEach>
                $('#select-animal-all').prop('checked', $('.select-animal:not(:checked)').length == 0);
                $('#select-animal-all').change(function (e) {
                    var checked = $(this).prop('checked');
                    $('.select-animal').prop('checked', checked);
                    <c:forEach items="${projectAnimalsList}" var="animal">
                    cleanseMap.setAnimalVisible("${animal.id}", checked);
                    </c:forEach>
                });
                $('#kalmanFilterRun').click(function(e) {
                    $('#kalmanFilterRun').prop('disabled', true);
                    $('#kalmanFilterCancel').prop('disabled', false).fadeIn();
                });
                $('#kalmanFilterSstRun').click(function(e) {
                    $('#kalmanFilterSstRun').prop('disabled', true);
                    $('#kalmanFilterSstCancel').prop('disabled', false).fadeIn();
                });
                $('#kalmanFilterCancel').click(function(e) {
                    $('#kalmanFilterCancel').prop('disabled', true);
                    cleanseMap.deleteCurrentAnalysis();
                    $('#kalmanFilterCancel').fadeOut();
                    $('#kalmanFilterRun').prop('disabled', false);
                });
                $('#kalmanFilterSstCancel').click(function(e) {
                    $('#kalmanFilterSstCancel').prop('disabled', true);
                    cleanseMap.deleteCurrentAnalysis();
                    $('#kalmanFilterSstCancel').fadeOut();
                    $('#kalmanFilterSstRun').prop('disabled', false);
                });
                $('#accordion-body-multi-polygon').on('show', function() {
                    cleanseMap.setPolygonControlActivated(true);
                });
                $('#accordion-body-multi-polygon').on('hide', function() {
                    cleanseMap.setPolygonControlActivated(false);
                });
                $('.layerExplanationExpander').click(function(e) {
                    e.preventDefault();
                    var prev = $(this).prev();
                    var maxHeight = prev.css('max-height');
                    var newMaxHeight = (maxHeight === 'none') ? '54px' : 'none';
                    prev.css('max-height', newMaxHeight);
                });
                cleanseMap = null;
                onResize();
                cleanseMap = new OzTrack.CleanseMap('projectMap', {
                    project: {
                        id: <c:out value="${project.id}"/>,
                        <c:if test="${(project.access == 'OPEN') and (project.dataLicence != null)}">
                        dataLicence: {
                            title: '${project.dataLicence.title}',
                            infoUrl: '${project.dataLicence.infoUrl}',
                            imageUrl: '${pageContext.request.scheme}://${fn:substringAfter(project.dataLicence.imageUrl, "://")}'
                        },
                        </c:if>
                        crosses180: ${project.crosses180},
                        bounds: new OpenLayers.Bounds(
                            ${projectBoundingBox.envelopeInternal.minX}, ${projectBoundingBox.envelopeInternal.minY},
                            ${projectBoundingBox.envelopeInternal.maxX}, ${projectBoundingBox.envelopeInternal.maxY}
                        ),
                        minDate: new Date(${projectDetectionDateRange.minimum.time}),
                        maxDate: new Date(${projectDetectionDateRange.maximum.time})
                    },
                    animals: [
                        <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                        <c:set var="animalBoundingBox" value="${animalBoundingBoxes[animal.id]}"/>
                        {
                            id: ${animal.id},
                            name: '${oztrack:escapeJS(animal.animalName)}',
                            <c:if test="${animalBoundingBox != null}">
                            <c:set var="env" value="${animalBoundingBox.envelopeInternal}"/>
                            bounds: new OpenLayers.Bounds(${env.minX}, ${env.minY}, ${env.maxX}, ${env.maxY})
                            </c:if>
                        }<c:if test="${!animalStatus.last}">,
                        </c:if>
                        </c:forEach>
                    ],
                    onKalmanFilterDelete: function(analysis) {
                        $('#kalmanFilterInfo-' + analysis.id).fadeOut({complete: function() {$(this).remove();}});
                    },
                    onKalmanFilterSstDelete: function(analysis) {
                        $('#kalmanFilterSstInfo-' + analysis.id).fadeOut({complete: function() {$(this).remove();}});
                    },
                    onKalmanFilterError: function(analysis) {
                        $('#kalmanFilterCancel').prop('disabled', true)
                        $('#kalmanFilterDelete-' + analysis.id).prop('disabled', false);
                        $('#kalmanFilterCancel').fadeOut();
                        $('#kalmanFilterRun').prop('disabled', false);
                    },
                    onKalmanFilterSstError: function(analysis) {
                        $('#kalmanFilterSstCancel').prop('disabled', true)
                        $('#kalmanFilterSstDelete-' + analysis.id).prop('disabled', false);
                        $('#kalmanFilterSstCancel').fadeOut();
                        $('#kalmanFilterSstRun').prop('disabled', false);
                    },
                    onKalmanFilterSuccess: function(analysis) {
                        $('#kalmanFilterRun').prop('disabled', false);
                        $('#kalmanFilterCancel').prop('disabled', true).hide();
                        $('#kalmanFilterDelete-' + analysis.id).prop('disabled', false);
                        $('#kalmanFilterApply-' + analysis.id).prop('disabled', false);
                    },
                    onKalmanFilterSstSuccess: function(analysis) {
                        $('#kalmanFilterSstRun').prop('disabled', false);
                        $('#kalmanFilterSstCancel').prop('disabled', true).hide();
                        $('#kalmanFilterSstDelete-' + analysis.id).prop('disabled', false);
                        $('#kalmanFilterSstApply-' + analysis.id).prop('disabled', false);
                    },
                    onUpdateInfoFromKalmanFilterCreate: function(layerName, analysis, fromDate, toDate) {
                        <tags:analysis-info-create
                            analysisTypeList="${analysisTypeList}"
                            headerActionsJsExpr="
                                $('<div>')
                                    .addClass('btn-group')
                                    .append($('<button>')
                                        .attr('id', 'kalmanFilterDelete-' + analysis.id)
                                        .addClass('kalmanFilterDelete')
                                        .addClass('btn')
                                        .prop('disabled', true)
                                        .attr('onclick', 'deleteKalmanFilter(' + analysis.id + ');')
                                        .append($('<i>').addClass('icon-trash'))
                                    )
                                    .append($('<button>')
                                        .attr('id', 'kalmanFilterApply-' + analysis.id)
                                        .addClass('kalmanFilterApply')
                                        .addClass('btn')
                                        .prop('disabled', true)
                                        .attr('onclick', 'applyKalmanFilter(' + analysis.id + ');')
                                        .append($('<i>').addClass('icon-ok'))
                                        .append(' Replace track')
                                    )
                            "
                            parentIdJsExpr="'kalmanFilterInfoParent'"
                            childIdJsExpr="'kalmanFilterInfo-' + analysis.id"
                            statsIdJsExpr="'kalmanFilterInfoStats-' + analysis.id"/>
                    },
                    onUpdateInfoFromKalmanFilterSstCreate: function(layerName, analysis, fromDate, toDate) {
                        <tags:analysis-info-create
                            analysisTypeList="${analysisTypeList}"
                            headerActionsJsExpr="
                                $('<div>')
                                    .addClass('btn-group')
                                    .append($('<button>')
                                        .attr('id', 'kalmanFilterSstDelete-' + analysis.id)
                                        .addClass('kalmanFilterSstDelete')
                                        .addClass('btn')
                                        .prop('disabled', true)
                                        .attr('onclick', 'deleteKalmanFilterSst(' + analysis.id + ');')
                                        .append($('<i>').addClass('icon-trash'))
                                    )
                                    .append($('<button>')
                                        .attr('id', 'kalmanFilterSstApply-' + analysis.id)
                                        .addClass('kalmanFilterSstApply')
                                        .addClass('btn')
                                        .prop('disabled', true)
                                        .attr('onclick', 'applyKalmanFilterSst(' + analysis.id + ');')
                                        .append($('<i>').addClass('icon-ok'))
                                        .append(' Replace track')
                                    )
                            "
                            parentIdJsExpr="'kalmanFilterSstInfoParent'"
                            childIdJsExpr="'kalmanFilterSstInfo-' + analysis.id"
                            statsIdJsExpr="'kalmanFilterSstInfoStats-' + analysis.id"/>
                    },
                    onUpdateInfoFromKalmanFilterSuccess: function(analysis, animalAttributes) {
                        $('#kalmanFilterDelete-' + analysis.id).prop('disabled', false);
                        $('#kalmanFilterApply-' + analysis.id).prop('disabled', false);
                        <tags:analysis-info-success
                            project="${project}"
                            analysisTypeList="${analysisTypeList}"
                            childIdJsExpr="'kalmanFilterInfo-' + analysis.id"
                            statsIdJsExpr="'kalmanFilterInfoStats-' + analysis.id"
                            helpPopoverIdJsExpr="'kalmanFilterInfoHelpPopover'"/>
                    },
                    onUpdateInfoFromKalmanFilterSstSuccess: function(analysis, animalAttributes) {
                        $('#kalmanFilterSstDelete-' + analysis.id).prop('disabled', false);
                        $('#kalmanFilterSstApply-' + analysis.id).prop('disabled', false);
                        <tags:analysis-info-success
                            project="${project}"
                            analysisTypeList="${analysisTypeList}"
                            childIdJsExpr="'kalmanFilterSstInfo-' + analysis.id"
                            statsIdJsExpr="'kalmanFilterSstInfoStats-' + analysis.id"
                            helpPopoverIdJsExpr="'kalmanFilterSstInfoHelpPopover'"/>
                    },
                    onReset: function() {
                        $('#cleanse-select').children().remove();
                        $('#cleanse-list').children().remove();
                        $('#kalmanFilterCancel').fadeOut();
                        $('#kalmanFilterSstCancel').fadeOut();
                    },
                    onPolygonFeatureAdded: function(id, title, wkt) {
                        $('#cleanse-list').append(
                            $('<li>')
                                .attr('id', 'cleanse-li-' + id)
                                .append(title)
                                .append(' (')
                                .append(
                                    $('<a>')
                                        .attr('href', 'javascript:void(0)')
                                        .attr('onclick', 'cleanseMap.deletePolygonFeature(\'' + id + '\');')
                                        .attr('onmouseover', 'cleanseMap.selectPolygonFeature(\'' + id + '\', true);')
                                        .attr('onmouseout', 'cleanseMap.selectPolygonFeature(\'' + id + '\', false);')
                                        .append('unselect')
                                )
                                .append(')')
                        );
                        $('#cleanse-select').append(
                            $('<option>')
                                .attr('id', 'cleanse-option-' + id)
                                .attr('value', wkt)
                                .attr('selected', 'selected')
                                .append(title)
                        );
                    },
                    onDeletePolygonFeature: function(id) {
                        $('*[id=\'cleanse-li-' + id + '\']').remove();
                        $('*[id=\'cleanse-option-' + id + '\']').remove();
                    }
                });
            });
            function onResize() {
                var mainHeight = $(window).height() - $('#header').outerHeight();
                $('#projectMapOptions').height(mainHeight);
                var panelPadding =
                    parseInt($('#projectMapOptions .ui-tabs-panel').css('padding-top')) +
                    parseInt($('#projectMapOptions .ui-tabs-panel').css('padding-bottom'));
                $('#projectMapOptions .ui-tabs-panel').height(
                    $('#projectMapOptions').innerHeight() -
                    $('#projectMapOptions .ui-tabs-nav').outerHeight() -
                    panelPadding
                );
                $('#projectMap').height(mainHeight);
                if (cleanseMap) {
                    cleanseMap.updateSize();
                }
            }
            $(window).resize(onResize);
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="mapTool">
        <div id="projectMapOptions">
        <div id="projectMapOptionsInner">
        <div id="projectMapOptionsTabs">
            <ul>
                <li><a href="#dataCleansing">Edit Tracks</a></li>
            </ul>
            <div id="dataCleansing">
                <form id="form-context" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                <fieldset>
                <div class="control-group" style="margin-bottom: 9px;">
                    <div style="margin-bottom: 9px; font-weight: bold;">
                        <span>Dates</span>
                        <div class="help-popover" title="Dates">
                            <p>Filter detections to be deleted/restored by date range.</p>
                            <p>If left blank, all detections in the project will be included.</p>
                        </div>
                    </div>
                    <div class="controls">
                        <input id="fromDate" name="fromDate" type="hidden"/>
                        <input id="toDate" name="toDate" type="hidden"/>
                        <input id="fromDateVisible" type="text" class="datepicker" placeholder="From" style="margin-bottom: 0px; width: 80px;"/> -
                        <input id="toDateVisible" type="text" class="datepicker" placeholder="To" style="margin-bottom: 0px;  width: 80px;"/>
                    </div>
                </div>
                <div class="control-group" style="margin-bottom: 9px;">
                    <div style="margin-bottom: 9px; font-weight: bold;">
                        <span>Animals</span>
                        <div class="help-popover" title="Animals">
                            <p>Filter detections to be deleted/restored by animal.</p>
                            <p>Choose "Select all" to include all animals in the project.</p>
                        </div>
                    </div>
                    <div id="animalList" class="controls">
                        <div style="background-color: #d8e0a8;">
                        <div class="animalCheckbox">
                            <input
                                id="select-animal-all"
                                type="checkbox"
                                style="width: 15px;" />
                        </div>
                        <div class="smallSquare" style="background-color: transparent;"></div>
                        <div class="animalLabel">Select all</div>
                        </div>
                        <div style="clear: both;"></div>
                        <c:forEach items="${projectAnimalsList}" var="animal">
                        <a style="display: inline-block; float: right;" href="javascript:cleanseMap.zoomToAnimal(${animal.id});"><i class="icon-zoom-in"></i></a>
                        <div class="animalCheckbox">
                            <input
                                id="select-animal-${animal.id}"
                                class="select-animal"
                                name="animalIds"
                                type="checkbox"
                                value="${animal.id}"
                                style="width: 15px;"
                                checked="checked" />
                        </div>
                        <div class="smallSquare" style="background-color: ${animal.colour};"></div>
                        <div class="animalLabel">${animal.animalName}</div>
                        <div style="clear: both;"></div>
                        </c:forEach>
                    </div>
                </div>
                </fieldset>
                </form>
                <div style="margin-bottom: 9px; font-weight: bold;">Action</div>
                <div class="accordion" id="accordion-action">
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-multi-polygon">
                                Polygon selection
                            </a>
                        </div>
                        <div id="accordion-body-multi-polygon" class="accordion-body collapse in">
                            <div class="accordion-inner">
                                <form id="form-multi-polygon" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <p>
                                        Select detections for removal from the project by drawing polygons around them.
                                        Click to start drawing, click again to draw each side of your selected area,
                                        and then double-click to finish drawing. You can draw as many polygons as are required.
                                    </p>
                                    <div class="controls">
                                        <select id="cleanse-select" name="polygon" multiple="multiple" style="display: none;">
                                        </select>
                                        <ul id="cleanse-list"></ul>
                                        <div>
                                            <button class="btn btn-primary" onclick="submitCleanseForm('delete', 'multi-polygon');">Delete selected</button>
                                            <button class="btn" onclick="submitCleanseForm('undelete', 'multi-polygon');">Restore selected</button>
                                        </div>
                                    </div>
                                </div>
                                </fieldset>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-speed-filter">
                                Speed filter
                            </a>
                        </div>
                        <div id="accordion-body-speed-filter" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-speed-filter" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <div class="layerExplanation" style="max-height: 54px; overflow-y: hidden;">
                                    <p>
                                        Specify a maximum speed. All detections that imply the animal has exceeded the specified
                                        maximum speed will be deleted, following the algorythm described in McConnell (1992) and
                                        implemented in the trip R package (Sumner 2006).
                                    </p>
                                    <p style="font-weight: bold;">References</p>
                                    <p>
                                        McConnell, B.J., Chambers, C. & Fedak, M.A. (1992) Foraging ecology of southern elephant
                                        seals in relation to the bathymetry and productivity of the Southern Ocean.
                                        Antarctic Science 4: 393-398.
                                    </p>
                                    <p>
                                        Sumner, M. (2013) trip: Spatial analysis of animal track data. R package version 1.1-15.
                                        <a href="http://CRAN.R-project.org/package=trip">http://CRAN.R-project.org/package=trip</a>
                                    </p>
                                    </div>
                                    <a class="layerExplanationExpander" href="#read-more">
                                        Read more
                                    </a>
                                    <div class="controls">
                                        <div class="input-append">
                                            <input id="maxSpeed" name="maxSpeed" type="text" class="input-small" placeholder="Max speed" >
                                            <span class="add-on">km/h</span>
                                        </div>
                                        <div>
                                            <button class="btn btn-primary" onclick="submitCleanseForm('delete', 'speed-filter');">Apply filter</button>
                                        </div>
                                    </div>
                                </div>
                                </fieldset>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-argos-class">
                                Argos location class
                            </a>
                        </div>
                        <div id="accordion-body-argos-class" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-argos-class" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <p>
                                        Specify a minimum Argos location class.
                                        All detections with classes representing a lower location accuracy will be deleted.
                                        For example, selecting 1 will delete detections of class 0, A, B, or Z.
                                    </p>
                                    <div class="controls">
                                        <div>
                                            <select id="minArgosClass" name="minArgosClass" style="width: auto;">
                                                <option value=""></option>
                                                <c:forEach items="${argosClasses}" var="argosClass">
                                                <option value="${argosClass.code}">${argosClass.code} - ${argosClass.description}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div>
                                            <button class="btn btn-primary" onclick="submitCleanseForm('delete', 'argos-class');">Apply filter</button>
                                        </div>
                                    </div>
                                </div>
                                </fieldset>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-dop">
                                Dilution of Precision (DOP)
                            </a>
                        </div>
                        <div id="accordion-body-dop" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-dop" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <p>
                                        Specify a maximum Dilution of Precision (DOP) value.
                                        All detections with DOP values higher than this maximum will be deleted.
                                    </p>
                                    <div class="controls">
                                        <div>
                                            <input id="maxDop" name="maxDop" type="text" class="input-small" placeholder="Max DOP" >
                                            <div class="help-popover" title="DOP values">
                                                <p>
                                                    The Dilution of Precision (DOP) value represents the expected error of a GPS position
                                                    based on the position of GPS satellites relative to the receiver's location.
                                                    DOP values can be interpreted as:
                                                    ideal (&lt; 1); excellent (1-2); good (2-5);
                                                    moderate (5-10); fair (10-20); poor (&gt; 20).
                                                </p>
                                                <p>
                                                    For more information see <a target="_blank" href="https://en.wikipedia.org/wiki/Dilution_of_precision_(GPS)">Wikipedia</a>.
                                            </div>
                                        </div>
                                        <div>
                                            <button class="btn btn-primary" onclick="submitCleanseForm('delete', 'dop');">Apply filter</button>
                                        </div>
                                    </div>
                                </div>
                                </fieldset>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-kalman-filter">
                                Kalman filter
                            </a>
                        </div>
                        <div id="accordion-body-kalman-filter" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-kalman-filter" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <c:if test="${not empty kalmanAnalysisType.explanation}">
                                    <div class="layerExplanation" style="max-height: 54px; overflow-y: hidden;">
                                        ${kalmanAnalysisType.explanation}
                                    </div>
                                    <a class="layerExplanationExpander" href="#read-more">
                                        Read more
                                    </a>
                                    </c:if>
                                    <div class="controls">
                                        <tags:analysis-param-fields analysisType="${kalmanAnalysisType}"/>
                                    </div>
                                </div>
                                </fieldset>
                                <div style="margin-top: 9px;">
                                    <button id="kalmanFilterRun" class="btn btn-primary" onclick="submitKalmanFilter();">Run filter</button>
                                    <button id="kalmanFilterCancel" class="btn" style="display: none;">Cancel</button>
                                </div>
                                <div id="kalmanFilterInfoParent">
                                </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-kalman-filter-sst">
                                Kalman filter (SST)
                            </a>
                        </div>
                        <div id="accordion-body-kalman-filter-sst" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-kalman-filter-sst" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <c:if test="${not empty kalmanSstAnalysisType.explanation}">
                                    <div class="layerExplanation" style="max-height: 54px; overflow-y: hidden;">
                                        ${kalmanSstAnalysisType.explanation}
                                    </div>
                                    <a class="layerExplanationExpander" href="#read-more">
                                        Read more
                                    </a>
                                    </c:if>
                                    <div class="controls">
                                        <tags:analysis-param-fields analysisType="${kalmanSstAnalysisType}"/>
                                    </div>
                                </div>
                                </fieldset>
                                <div style="margin-top: 9px;">
                                    <button id="kalmanFilterSstRun" class="btn btn-primary" onclick="submitKalmanFilterSst();">Run filter</button>
                                    <span id="kalmanFilterSstSingleAnimal" style="display: none;">Can only filter one animal at a time</span>
                                    <button id="kalmanFilterSstCancel" class="btn" style="display: none;">Cancel</button>
                                </div>
                                <div id="kalmanFilterSstInfoParent">
                                </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group" style="margin-top: 15px;">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-delete-all">
                                Delete all <span style="font-size: 11px; color: #888;">(for selected dates and animals)</span>
                            </a>
                        </div>
                        <div id="accordion-body-delete-all" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-delete-all" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <p>
                                        Delete all detections matching the selected dates and animals.
                                        This will delete all points currently visible on the map but not others in the project.
                                    </p>
                                    <div class="controls">
                                        <div>
                                            <button class="btn btn-primary" onclick="submitCleanseForm('delete', 'delete-all');">Delete all</button>
                                        </div>
                                    </div>
                                </div>
                                </fieldset>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-restore-all">
                                Restore all <span style="font-size: 11px; color: #888;">(for selected dates and animals)</span>
                            </a>
                        </div>
                        <div id="accordion-body-restore-all" class="accordion-body collapse">
                            <div class="accordion-inner">
                                <form id="form-restore-all" class="form-vertical" style="margin: 0;" onsubmit="return false;">
                                <fieldset>
                                <div class="control-group">
                                    <p>
                                        Restore all detections matching the selected dates and animals.
                                        This will restore all points currently visible on the map but not others in the project.
                                    </p>
                                    <p>
                                        All points output from a Kalman filter will be removed.
                                    </p>
                                    <div class="controls">
                                        <div>
                                            <button class="btn btn-primary" onclick="submitCleanseForm('undelete', 'restore-all');">Restore all</button>
                                        </div>
                                    </div>
                                </div>
                                </fieldset>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <p id="responseDialog"></div>
            </div>
        </div>
        </div>
        </div>
        <div id="projectMap"></div>
        <div style="clear:both;"></div>
        </div>
    </jsp:body>
</tags:page>

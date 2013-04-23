<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.data.model.types.MapLayerType" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Edit Data" fluid="true">
    <jsp:attribute name="description">
        Edit data in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="navExtra">
        <a id="projectMapOptionsBack" href="/projects/${project.id}"><span class="icon-chevron-left icon-white"></span> Back to project</a>
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
        </style>
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script src="${pageContext.request.scheme}://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/optimised/openlayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/project-cleanse.js"></script>
        <script type="text/javascript">
            function submitCleanseForm(operation) {
                jQuery('.cleanse-response').hide();
                jQuery.ajax({
                    url: '${pageContext.request.contextPath}/projects/${project.id}/cleanse',
                    type: 'POST',
                    data: 'operation=' + operation + '&' + jQuery('#cleanseForm').serialize(),
                    beforeSend: function(jqXHR, settings) {
                        cleanseMap.increaseLoadingCounter();
                    },
                    success: function(data, textStatus, jqXHR) {
                        cleanseMap.reset();
                        var message = null;
                        if ((operation == 'delete') || (operation == 'delete-all') || (operation == 'speed-filter')) {
                            var numDeleted = jQuery(data).find('num-deleted').text();
                            message = numDeleted + ' points deleted';
                        }
                        else if ((operation == 'undelete') || (operation == 'undelete-all')) {
                            var numUndeleted = jQuery(data).find('num-undeleted').text();
                            message = numUndeleted + ' points restored';
                        }
                        $('#responseDialog')
                            .text(message)
                            .dialog({
                                title: 'Complete',
                                modal: true,
                                resizable: false,
                                buttons: {
                                    'Close': function() {
                                        $(this).dialog('close');
                                    }
                                }
                            });
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        var message = jQuery(jqXHR.responseText).find('error').text() || 'Error processing request';
                        $('#responseDialog')
                            .text(message)
                            .dialog({
                                title: 'Error',
                                modal: true,
                                resizable: false,
                                buttons: {
                                    'Close': function() {
                                        $(this).dialog('close');
                                    }
                                }
                            });
                    },
                    complete: function(jqXHR, textStatus) {
                        cleanseMap.decreaseLoadingCounter();
                    }
                });
                return false;
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
                cleanseMap = null;
                onResize();
                cleanseMap = new OzTrack.CleanseMap('projectMap', {
                    projectId: <c:out value="${project.id}"/>,
                    <c:if test="${(project.access == 'OPEN') and (project.dataLicence != null)}">
                    dataLicence: {
                        title: '${project.dataLicence.title}',
                        infoUrl: '${project.dataLicence.infoUrl}',
                        imageUrl: '${pageContext.request.scheme}://${fn:substringAfter(project.dataLicence.imageUrl, "://")}'
                    },
                    </c:if>
                    fromDate: new Date(${projectDetectionDateRange.minimum.time}),
                    toDate: new Date(${projectDetectionDateRange.maximum.time}),
                    animalIds: [
                        <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                        ${animal.id}<c:if test="${!animalStatus.last}">,
                        </c:if>
                        </c:forEach>
                    ],
                    projectBounds: new OpenLayers.Bounds(
                        ${projectBoundingBox.envelopeInternal.minX}, ${projectBoundingBox.envelopeInternal.minY},
                        ${projectBoundingBox.envelopeInternal.maxX}, ${projectBoundingBox.envelopeInternal.maxY}
                    ),
                    onReset: function() {
                        jQuery('#cleanse-select').children().remove();
                        jQuery('#cleanse-list').children().remove();
                    },
                    onPolygonFeatureAdded: function(id, title, wkt) {
                        jQuery('#cleanse-list').append(
                            jQuery('<li>')
                                .attr('id', 'cleanse-li-' + id)
                                .append(title)
                                .append(' (')
                                .append(
                                    jQuery('<a>')
                                        .attr('href', 'javascript:void(0)')
                                        .attr('onclick', 'cleanseMap.deletePolygonFeature(\'' + id + '\');')
                                        .attr('onmouseover', 'cleanseMap.selectPolygonFeature(\'' + id + '\', true);')
                                        .attr('onmouseout', 'cleanseMap.selectPolygonFeature(\'' + id + '\', false);')
                                        .append('unselect')
                                )
                                .append(')')
                        );
                        jQuery('#cleanse-select').append(
                            jQuery('<option>')
                                .attr('id', 'cleanse-option-' + id)
                                .attr('value', wkt)
                                .attr('selected', 'selected')
                                .append(title)
                        );
                    },
                    onDeletePolygonFeature: function(id) {
                        jQuery('*[id=\'cleanse-li-' + id + '\']').remove();
                        jQuery('*[id=\'cleanse-option-' + id + '\']').remove();
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
                <form id="cleanseForm" class="form-veritcal" onsubmit="return false;">
                <fieldset>
                <div style="margin-bottom: 9px; font-weight: bold;">Filter</div>
                <div class="accordion" id="accordion-filter">
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" href="#accordion-body-dates">
                                Date Range
                            </a>
                        </div>
                        <div id="accordion-body-dates" class="accordion-body collapse">
                            <div class="accordion-inner">
                            <div class="controls">
                                <input id="fromDate" name="fromDate" type="hidden"/>
                                <input id="toDate" name="toDate" type="hidden"/>
                                <input id="fromDateVisible" type="text" class="datepicker" placeholder="From" style="margin-bottom: 3px; width: 80px;"/> -
                                <input id="toDateVisible" type="text" class="datepicker" placeholder="To" style="margin-bottom: 3px;  width: 80px;"/>
                            </div>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" href="#accordion-body-animals">
                                Animals
                            </a>
                        </div>
                        <div id="accordion-body-animals" class="accordion-body collapse">
                            <div class="accordion-inner">
                            <div id="animalList" class="controls">
                                <div style="background-color: #ddd;">
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
                                <div class="animalCheckbox">
                                    <input
                                        id="select-animal-${animal.id}"
                                        class="select-animal"
                                        name="animal"
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
                        </div>
                    </div>
                </div>
                <div style="margin-bottom: 9px; font-weight: bold;">Action</div>
                <div class="accordion" id="accordion-action">
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-polygon">
                                Polygon Selection
                            </a>
                        </div>
                        <div id="accordion-body-polygon" class="accordion-body collapse in">
                            <div class="accordion-inner">
                            <p>
                                <small style="color: #555;">Select points for removal from the project by drawing polygons around them.
                                Click to start drawing and click again to draw each side of your selected area.
                                Double-click to finish drawing. You can draw as many polygons as are required.</small>
                            </p>
                            <div class="controls">
                                <select id="cleanse-select" name="polygon" multiple="multiple" style="display: none;">
                                </select>
                                <ul id="cleanse-list">
                                </ul>
                            </div>
                            <div style="margin-top: 18px;">
                                <button class="btn btn-primary" onclick="submitCleanseForm('delete');">Delete selected</button>
                                <button class="btn" onclick="submitCleanseForm('undelete');">Restore selected</button>
                            </div>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-speed-filter">
                                Speed Filter
                            </a>
                        </div>
                        <div id="accordion-body-speed-filter" class="accordion-body collapse">
                            <div class="accordion-inner">
                            <div class="control-group" style="margin-bottom: 9px;">
                                <p>
                                    <small style="color: #555;">If you enter a maximum speed, only those points implying
                                    that the animal has exceeded the maximum speed will be deleted.</small>
                                </p>
                                <div class="controls">
                                    <div class="input-append">
                                        <input id="maxSpeed" name="maxSpeed" type="text" class="input-small" placeholder="Max speed" >
                                        <span class="add-on">km/h</span>
                                    </div>
                                </div>
                            </div>
                            <div style="margin-top: 18px;">
                                <button class="btn btn-primary" onclick="submitCleanseForm('speed-filter');">Apply filter</button>
                            </div>
                            </div>
                        </div>
                    </div>
                    <div class="accordion-group">
                        <div class="accordion-heading">
                            <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion-action" href="#accordion-body-bulk">
                                Bulk Operation
                            </a>
                        </div>
                        <div id="accordion-body-bulk" class="accordion-body collapse">
                            <div class="accordion-inner">
                            <div class="control-group" style="margin-bottom: 9px;">
                                <p>
                                    <small style="color: #555;">Delete or restore all points satisfying the filter specified above.</small>
                                </p>
                            </div>
                            <div style="margin-top: 18px;">
                                <button class="btn btn-primary" onclick="submitCleanseForm('delete-all');">Delete all</button>
                                <button class="btn" onclick="submitCleanseForm('undelete-all');">Restore all</button>
                            </div>
                            </div>
                        </div>
                    </div>
                </div>
                </fieldset>
                </form>
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

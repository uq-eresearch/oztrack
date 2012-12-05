<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="isoDateFormatPattern" value="yyyy-MM-dd"/>
<c:set var="dateTimeFormatPattern" value="dd/MM/yyyy' at 'HH:mm"/>
<tags:page title="${project.title}: View Tracks" fluid="true">
    <jsp:attribute name="description">
        View and analyse animal tracking data in the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="head">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/optimised/openlayers.css" type="text/css">
        <style type="text/css">
            #main {
                padding-bottom: 0;
            }
            #projectMapOptions .ui-accordion-content {
                padding: 10px;
            }
            #projectMapHelp {
                display: none;
            }
            #projectMapHelp p {
                text-align: justify;
                margin: 1em 0;
            }
            #mapToolForm {
                padding-left:0px;
                padding-top:0px;
            }
            #animalsFilter {
                height: 90px;
                border: 1px solid #ccc;
                overflow-y: scroll;
            }
            .animalsFilterCheckbox {
                float: left;
                width: 15px;
                margin: 0;
                padding: 0;
            }
            .animalsFilterCheckbox input[type="checkbox"] {
                margin: 0 0 2px 0;
            }
            .animalsFilterSmallSquare {
                float: left;
                width: 12px;
                height: 12px;
                margin: 2px 5px;
                padding: 0;
            }
            .animalsFilterLabel {
                margin-left: 40px;
                padding: 0;
            }
            .animalCheckbox {
                float: left;
                width: 15px;
                margin: 5px 0;
            }
            .smallSquare {
                display: block;
                height: 14px;
                width: 14px;
                float: left;
                margin: 5px;
            }
            .animalHeader {
                padding: 0;
                height: 24px;
                line-height: 24px;
                margin-bottom: 0.5em;
            }
            .animalLabel {
                font-weight: bold;
                margin-left: 40px;
                margin-right: 65px;
                white-space: nowrap;
                overflow: hidden;
            }
            a.animalInfoToggle {
                text-decoration: none;
            }
            .animalInfo {
                margin-left: 8px;
                margin-right: 8px;
                margin-top: 0;
                margin-bottom: 0;
            }
            .animalInfo table {
                margin-top: 5px;
            }
            .layerInfo {
                margin: 0 0 0.5em 0;
                padding: 5px;
            }
            .layerInfoTitle {
                padding: 2px;
                border-bottom: 1px solid #ccc;
                background-color: #D8E0A8;
            }
            .layerInfoLabel {
                width: 7em;
            }
            .citation {
                font-style: italic;
                text-align: left;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script src="${pageContext.request.scheme}://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/optimised/openlayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/project-analysis.js"></script>
        <script type="text/javascript">
            function showParamTable(queryType) {
                $('.paramTable').hide();
                $('#paramTable-' + queryType).fadeIn('slow');
            }
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuAnalysis').addClass('active');
                $("#projectMapOptionsAccordion").accordion();
                $('#fromDateVisible').datepicker({
                    altField: "#fromDate",
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    defaultDate: new Date(${projectDetectionDateRange.minimum.time})
                });
                $('#toDateVisible').datepicker({
                    altField: "#toDate",
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    defaultDate: new Date(${projectDetectionDateRange.maximum.time})
                });
                <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                $('input[id=select-animal-${animal.id}]').change(function() {
                    analysisMap.toggleAllAnimalFeatures("${animal.id}", this.checked);
                });
                </c:forEach>

                $('#select-animal-all').prop('checked', $('.select-animal:not(:checked)').length == 0);
                $('.select-animal').change(function (e) {
                    $('#select-animal-all').prop('checked', $('.select-animal:not(:checked)').length == 0);
                });
                $('#select-animal-all').change(function (e) {
                    $('.select-animal').prop('checked', $(this).prop('checked')).trigger('change');
                });

                $('#filter-animal-all').prop('checked', $('.filter-animal:not(:checked)').length == 0);
                $('.filter-animal').change(function (e) {
                    $('#filter-animal-all').prop('checked', $('.filter-animal:not(:checked)').length == 0);
                });
                $('#filter-animal-all').change(function (e) {
                    $('.filter-animal').prop('checked', $(this).prop('checked'));
                });

                $('#queryTypeSelect-MCP').trigger('click');
                $('#projectMapHelpLink').click(function() {
                    var elem = $('#projectMapHelp');
                    if (elem.dialog('isOpen') === true) {
                        elem.dialog('close');
                    }
                    else {
                        elem.dialog({
                            title: 'Analysis Tools Help',
                            width: $('#projectMap').width() - 5,
                            height: $('#projectMap').height() - 5,
                            modal: false,
                            resizable: true,
                            zIndex: 9999999,
                            position: { 
                                my: 'center center',
                                at: 'center center',
                                of: $('#projectMap')
                            }
                        });
                    }
                });
                var projectBounds = new OpenLayers.Bounds(
                    ${projectBoundingBox.envelopeInternal.minX}, ${projectBoundingBox.envelopeInternal.minY},
                    ${projectBoundingBox.envelopeInternal.maxX}, ${projectBoundingBox.envelopeInternal.maxY}
                );
                analysisMap = null;
                onResize();
                analysisMap = createAnalysisMap('projectMap', {
                    projectId: <c:out value="${project.id}"/>,
                    animalIds: [
                        <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                        ${animal.id}<c:if test="${!animalStatus.last}">,
                        </c:if>
                        </c:forEach>
                    ],
                    projectBounds: projectBounds,
                    animalBounds: {
                        <c:forEach items="${animalBoundingBoxes}" var="animalBoundingBoxEntry" varStatus="animalBoundingBoxEntryStatus">
                        ${animalBoundingBoxEntry.key}: new OpenLayers.Bounds(
                            ${animalBoundingBoxEntry.value.envelopeInternal.minX}, ${animalBoundingBoxEntry.value.envelopeInternal.minY},
                            ${animalBoundingBoxEntry.value.envelopeInternal.maxX}, ${animalBoundingBoxEntry.value.envelopeInternal.maxY}
                        )<c:if test="${!animalBoundingBoxEntryStatus.last}">,
                        </c:if>
                        </c:forEach>
                    },
                    animalColours: {
                        <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                        ${animal.id}: '${animal.colour}'<c:if test="${!animalStatus.last}">,
                        </c:if>
                        </c:forEach>
                    },
                    minDate: new Date(${projectDetectionDateRange.minimum.time}),
                    maxDate: new Date(${projectDetectionDateRange.maximum.time}),
                    onAnalysisCreate: function(layerName, analysisUrl) {
                        addPreviousAnalysis(layerName, analysisUrl, new Date());
                    },
                    onAnalysisError: function(message) {
                        jQuery('#errorDialog')
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
                    onAnalysisSuccess: function() {
                        $("#projectMapOptionsAccordion").accordion('activate', '#animalPanelHeader');
                    }
                });
                <c:forEach items="${previousAnalyses}" var="analysis">
                addPreviousAnalysis(
                    '${analysis.analysisType.displayName}',
                    '${pageContext.request.contextPath}/projects/${analysis.project.id}/analyses/${analysis.id}',
                    new Date(${analysis.createDate.time})
                );
                </c:forEach>
            });
            function addPreviousAnalysis(layerName, analysisUrl, analysisCreateDate) {
                $('#previousAnalysesList').prepend($('<li>')
                    .addClass('previousAnalysesListItem analysis')
                    .append($('<a>')
                        .attr('href', 'javascript:void(0);')
                        .text(layerName)
                        .popover({
                            placement: 'bottom',
                            trigger: 'hover',
                            html: true,
                            title: layerName,
                            content: function() {
                                var div = $('<div>').append('<img src="${pageContext.request.contextPath}/img/ui-anim_basic_16x16.gif" />');
                                $.ajax({
                                    url: analysisUrl,
                                    type: 'GET',
                                    error: function(xhr, textStatus, errorThrown) {
                                    },
                                    complete: function (xhr, textStatus) {
                                        if (textStatus == 'success') {
                                            var analysis = $.parseJSON(xhr.responseText);
                                            div.empty();
                                            var table = $('<table>');
                                            if (analysis.params.fromDate) {
                                                table.append(
                                                    '<tr>' +
                                                    '<td class="layerInfoLabel">Date From:</td>' +
                                                    '<td>' + analysis.params.fromDate + '</td>' +
                                                    '</tr>'
                                                );
                                            }
                                            if (analysis.params.toDate) {
                                                table.append(
                                                    '<tr>' +
                                                    '<td class="layerInfoLabel">Date To:</td>' +
                                                    '<td>' + analysis.params.toDate + '</td>' +
                                                    '</tr>'
                                                );
                                            }
                                            table.append(
                                                '<tr>' +
                                                '<td class="layerInfoLabel">Animals: </td>' +
                                                '<td>' + analysis.params.animalNames.join(', ') + '</td>' +
                                                '</tr>'
                                            );
                                            <c:forEach items="${analysisTypeList}" var="analysisType">
                                            if (analysis.params.queryType == '${analysisType}') {
                                                <c:forEach items="${analysisType.parameterTypes}" var="parameterType">
                                                if (analysis.params.${parameterType.identifier}) {
                                                    table.append(
                                                        '<tr>' +
                                                        '<td class="layerInfoLabel">${parameterType.displayName}: </td>' +
                                                        '<td>' + analysis.params.${parameterType.identifier} + ' ${parameterType.units}</td>' +
                                                        '</tr>'
                                                    );
                                                }
                                                </c:forEach>
                                            }
                                            </c:forEach>
                                            div.append(table);
                                        }
                                    }
                                });
                                return div;
                            }
                        })
                        .click(function(e) {
                            analysisMap.addAnalysisLayer(analysisUrl, layerName);
                        })
                    )
                    .append(' (' + dateToISO8601(analysisCreateDate) + ' at ' + analysisCreateDate.toLocaleTimeString() + ')')
                );
            }
            function onResize() {
                var mainHeight = $(window).height() - $('#header').height() - $('#crumbs').height() - 21;
                $('#projectMapOptions').height(mainHeight);
                $('#projectMapOptions .ui-accordion-content').height($('#projectMapOptions').height() - (70 + $('#projectMapOptionsAccordion > h3').length * 30));
                $('#projectMap').height(mainHeight);
                if (analysisMap) {
                    analysisMap.updateSize();
                }
            }
            $(window).resize(onResize);
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <span class="active">View Tracks</span>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbsRight">
        <a class="btn" href="/projects/${project.id}">« Back to project</a>
    </jsp:attribute>
    <jsp:body>
        <div id="mapTool" class="mapTool">

        <div id="projectMapOptions">
        <div id="projectMapOptionsInner">
        <div id="projectMapOptionsAccordion">

            <h3 id="animalPanelHeader"><a href="javascript:void(0);">Analysis Results</a></h3>

            <div id="animalPanel">

                <div class="animalHeader" style="margin-bottom: 10px; border-bottom: 1px solid #ccc;">
                <div class="animalCheckbox">
                    <input
                        id="select-animal-all"
                        type="checkbox"
                        style="float: left; margin: 0;" />
                </div>
                <div class="smallSquare" style="background-color: transparent;"></div>
                <div>Select all</div>
                </div>

                <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                    <c:set var="showAnimalInfo" value="${animalStatus.index == 0}"/>
                    <div class="animalHeader">
                        <div class="btn-group" style="float: right;">
                            <button
                                id="buttonShowHide${animal.id}"
                                class="btn btn-mini"
                                onclick="var infoElem = $(this).parent().parent().next(); $(this).text(infoElem.is(':visible') ? 'Show' : 'Hide'); infoElem.slideToggle();">
                                ${showAnimalInfo ? 'Hide' : 'Show'}
                            </button>
                            <button class="btn btn-mini dropdown-toggle" data-toggle="dropdown">
                                <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu pull-right">
                                <li><a href="javascript:void(0);" onclick="analysisMap.zoomToAnimal(${animal.id});">Zoom to animal</a></li>
                                <li><a href="${pageContext.request.contextPath}/exportKML?projectId=${project.id}&animalId=${animal.id}">Export as KML</a></li>
                            </ul>
                        </div>

                        <div class="animalCheckbox">
                            <input id="select-animal-${animal.id}" class="select-animal" style="float: left; margin: 0;" type="checkbox" name="animalCheckbox" value="${animal.id}" checked="checked">
                        </div>

                        <div class="smallSquare" style="background-color: ${animal.colour};"></div>

                        <div class="animalLabel">
                            <a class="animalInfoToggle" href="javascript:void(0);" onclick="$('#buttonShowHide${animal.id}').click();">${animal.animalName}</a>
                        </div>
                    </div>
                    <div id="animalInfo-${animal.id}" class="animalInfo" style="display: ${showAnimalInfo ? 'block' : 'none'};">
                    </div>
                </c:forEach>
            </div>


            <h3><a id="homeRangeCalculatorLink" href="javascript:void(0);">Run New Analysis</a></h3>

            <div id="homeRangeCalculatorPanel">

                <form id="mapToolForm" class="form-vertical" method="POST" onsubmit="return false;">
                    <input id="projectId" type="hidden" value="${project.id}"/>
                    <fieldset>
                    <div class="control-group" style="margin-bottom: 9px;">
                        <div style="margin-bottom: 9px; font-weight: bold;">Date Range</div>
                        <div class="controls">
                            <input id="fromDate" type="hidden"/>
                            <input id="toDate" type="hidden"/>
                            <input id="fromDateVisible" type="text" class="datepicker" placeholder="From" style="margin-bottom: 3px; width: 80px;"/> -
                            <input id="toDateVisible" type="text" class="datepicker" placeholder="To" style="margin-bottom: 3px; width: 80px;"/>
                        </div>
                    </div>
                    <div class="control-group" style="margin-bottom: 9px;">
                    <div style="margin-bottom: 9px; font-weight: bold;">Animals</div>
                        <div id="animalsFilter" class="controls">
                            <div style="background-color: #d8e0a8;">
                            <div class="animalsFilterCheckbox">
                                <input
                                    id="filter-animal-all"
                                    type="checkbox"
                                    style="width: 15px;" />
                            </div>
                            <div class="animalsFilterSmallSquare" style="background-color: transparent;"></div>
                            <div class="animalsFilterLabel">Select all</div>
                            </div>
                            <div style="clear: both;"></div>
                            <c:forEach items="${projectAnimalsList}" var="animal" varStatus="animalStatus">
                            <div class="animalsFilterCheckbox">
                                <input
                                    id="filter-animal-${animal.id}"
                                    class="filter-animal"
                                    name="animal"
                                    type="checkbox"
                                    value="${animal.id}"
                                    style="width: 15px;"
                                    <c:if test="${animalStatus.first}">checked="checked"</c:if> />
                            </div>
                            <div class="animalsFilterSmallSquare" style="background-color: ${animal.colour};"></div>
                            <div class="animalsFilterLabel">${animal.animalName}</div>
                            <div style="clear: both;"></div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="control-group" style="margin-bottom: 9px;">
                        <div style="margin-bottom: 9px; font-weight: bold;">Layer Type</div>
                        <div class="controls">
                            <table class="queryType">
                                <c:forEach items="${mapLayerTypeList}" var="mapLayerType">
                                <tr>
                                    <td style="padding: 0 5px; vertical-align: top;">
                                        <input type="radio"
                                            name="queryTypeSelect"
                                            id="queryTypeSelect-${mapLayerType}"
                                            value="${mapLayerType}"
                                            onClick="updateParamTable('${mapLayerType}')"
                                        />
                                    </td>
                                    <td id="${mapLayerType}">
                                        <label style="margin: 2px 0 0 0;" for="queryTypeSelect-${mapLayerType}"><c:out value="${mapLayerType.displayName}"/></label>
                                    </td>
                                </tr>
                                </c:forEach>
                                <c:forEach items="${analysisTypeList}" var="analysisType">
                                <tr>
                                    <td style="padding: 0 5px; vertical-align: top;">
                                        <input type="radio"
                                            name="queryTypeSelect"
                                            id="queryTypeSelect-${analysisType}"
                                            value="${analysisType}"
                                            onClick="showParamTable('${analysisType}')"
                                        />
                                    </td>
                                    <td id="${analysisType}">
                                        <label style="margin: 2px 0 0 0;" for="queryTypeSelect-${analysisType}"><c:out value="${analysisType.displayName}"/></label>
                                        <table id="paramTable-${analysisType}" class="paramTable" style="display: none; margin: 6px 0;">
                                            <c:set var="foundAdvancedParameterType" value="false"/>
                                            <c:forEach items="${analysisType.parameterTypes}" var="parameterType">
                                            <c:if test="${!foundAdvancedParameterType and parameterType.advanced}">
                                            <c:set var="foundAdvancedParameterType" value="true"/>
                                            <tr>
                                                <td colspan="2">
                                                    <div style="margin-bottom: 3px;">
                                                        <a href="javascript:void(0);" onclick="$(this).closest('tr').nextAll().fadeToggle();">Advanced parameters</a>
                                                    </div>
                                                </td>
                                            </tr>
                                            </c:if>
                                            <tr <c:if test="${parameterType.advanced}">style="display: none;"</c:if>>
                                                <td style="padding-right: 10px;">${parameterType.displayName}</td>
                                                <td class="${(not empty parameterType.units) ? 'input-append' : ''}">
                                                    <c:choose>
                                                    <c:when test="${parameterType.options != null}">
                                                    <select class="paramField-${analysisType}" style="width: auto;" name="${parameterType.identifier}">
                                                    <c:forEach items="${parameterType.options}" var="option">
                                                    <option
                                                        value="${option.value}"
                                                        <c:if test="${parameterType.defaultValue == option.value}">selected="selected"</c:if>
                                                        >${option.title}</option>
                                                    </c:forEach>
                                                    </select>
                                                    </c:when>
                                                    <c:when test="${parameterType.dataType == 'boolean'}">
                                                    <input
                                                        class="paramField-${analysisType} checkbox"
                                                        name="${parameterType.identifier}"
                                                        type="checkbox"
                                                        <c:if test="${parameterType.defaultValue == 'true'}">checked="checked"</c:if>
                                                        value="true"
                                                        style="margin: 4px 1px;" />
                                                    </c:when>
                                                    <c:otherwise>
                                                    <input
                                                        class="paramField-${analysisType} input-mini"
                                                        name="${parameterType.identifier}"
                                                        type="text"
                                                        <c:if test="${not empty parameterType.defaultValue}">
                                                        value="${parameterType.defaultValue}"
                                                        </c:if>
                                                        style="margin-bottom: 3px; text-align: right;"/>
                                                    <c:if test="${not empty parameterType.units}">
                                                    <span class="add-on">${parameterType.units}</span>
                                                    </c:if>
                                                    </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            </c:forEach>
                                        </table>
                                    </td>
                                </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div>
                    </fieldset>
                    <div style="margin-top: 18px;">
                        <input id="projectMapSubmit" class="btn btn-primary" type="button" value="Calculate" onclick="analysisMap.addProjectMapLayer();" />
                    </div>
                </form>
            </div>

            <c:if test="${not empty project.analyses}">
            <h3><a href="javascript:void(0);">Previous Analyses</a></h3>
            <div id="previousAnalysesPanel">
                <ul id="previousAnalysesList" class="icons">
                </ul>
            </div>
            </c:if>
        </div>
        </div>
        <div style="padding: 2px 5px 5px 5px;">
        <a id="projectMapHelpLink" class="btn btn-block" href="javascript:void(0);">Help</a>
        </div>
        </div>

        <div id="projectMap"></div>

        <div style="clear:both;"></div>

        </div>
        
        <div id="errorDialog"></div>
        <div id="projectMapHelp">
            <h2>Trajectory</h2>
            <p>
                The trajectory is the animal movement path created from the location fixes (detections)
                in chronological order. OzTrack plots the trajectory from the first fix in the
                uploaded file, until the last unless the date range has been specified. The
                trajectory can be viewed on the OzTrack mapping feature and the minimum distance
                moved by the animal along this trajectory is calculated and displayed in the
                Analysis Results window.
            </p>

            <h2>Minimum Convex polygon</h2>
            <p>
                Otherwise known as a convex hull, this approach uses the smallest area convex set
                that contains the location data. At 100% this will equivalent the area covered by
                all locations within the dataset. Resetting the computation at a lower % value
                will remove outliers from the computation, resulting in this % of locations in
                the final MCP. This calculation is undertaken within R using the adehabitat
                package (Calenge 2008). For further details about using an MCP home-range
                estimator see
            </p>

            <p class="citation">
                Worton, B.J. (1995)
                A convex hull-based estimator of home-range size.
                Biometrics, 51, 1206-1215.
            </p>

            <h2>Alpha-hull</h2>
            <p>
                The alpha hull home range estimation is a generalisation of the convex hull but
                objectively crops low use areas from the polygon surface. Alpha hulls are generated
                by connecting all locations as a Delauney triangulation, then systematically
                removing vertices until only those vertices that are shorter in length than the
                chosen parameter value alpha are retained. The smaller the value of alpha, the
                finer the resolution of the hull and the greater the exposure of non-use areas.
                As alpha increases, the polygon surface will increase until it is equivalent to a
                100% minimum convex polygon. For further details about using the alpha hull
                home-range density estimator see Burgman & Fox (2003).
            </p>

            <p class="citation">
                Burgman, M.A. &amp; Fox, J.C. (2003)
                Bias in species range estimates from minimum convex polygons:
                implications for conservation and options for improved planning.
                Animal Conservation, 6, 19-28.
            </p>

            <h2>Kernel Utilization Distribution</h2>
            <p>
                The fixed kernel density estimator is a non-parametric method of home-range analysis,
                which uses the utilization distribution to estimate the probability that an animal
                will be found at a specific geographical location. OzTrack provides the capacity to
                calculate and view the kernel UD at any level between 1 and 100%. 95% and 50% level
                are those most commonly adopted as the home-range and core-area UD, respectively.
            </p>
            <p>
                <b>H smoothing value</b>: The kernel UD accurately estimates areas of high use by
                the tagged animal, providing that the level of smoothing is appropriate. There are
                a number of different smoothing parameters that have been adopted in kernel estimates,
                and no single parameter will perform well in all conditions. OzTrack offers three
                options for selecting the kernel smoothing parameter. Two of these are automatically
                generated using either the href (or ad hoc) method, or the least-squares
                cross-validation (LSCV) algorithm. The third option is for the user to define a set
                numerical value (in meters). As h decreases, the kernel will become less continuous
                and more fragmented revealing increasing detail within the home range. These
                calculations are undertaken within R using the adehabitat library of functions
                (Calenge 2008). For further details about using Kernel UD for estimating home-range see;
            </p>

            <p class="citation">
                Seaman, D.E., Powell, R.A. (1996)
                An evaluation of the accuracy of kernel density estimators for home range analysis.
                Ecology, Vol. 77, 2075-2085.
            </p>

            <p class="citation">
                Silverman, B.W. (1986) Density estimation for statistics and data analysis. Chapman and Hall, London, UK
            </p>

            <p class="citation">
                Worton, B.J. (1989) Kernel methods for estimating the utilization distribution in home-range studies
            </p>
            
            <h2>Heat Map</h2>
            <p>
                This generates a grid over the study area and uses a coloured gradient to visually
                identify areas of high usage by the tagged animal. These can be applied to either
                points or connectivity lines between points.  The size of the grid cells (in meters)
                can be specified. This OzTrack tool utilises the spatstat package in R
                (Baddeley &amp; Turner, 2005).
            </p>
            <p class="citation">
                Baddeley, A. &amp; Turner,  R. (2005)
                spatstat: An R package for analyzing spatial point patterns.
                Journal of Statistical Software, 12,6
            </p>
        </div>
    </jsp:body>
</tags:page>

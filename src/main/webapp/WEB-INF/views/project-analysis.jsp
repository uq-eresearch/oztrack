<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.data.model.types.MapQueryType" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="${project.title}: Analysis Tools">
    <jsp:attribute name="head">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/style.css"/>" type="text/css">
        <link rel="stylesheet" href="<c:url value="/js/openlayers/theme/default/google.css"/>" type="text/css">
        <style type="text/css">
            #projectMapOptions .ui-accordion-content {
                padding: 1em 10px;
            }
            #animalPanel, #homeRangeCalculatorPanel, #projectMapHelp {
                height:350px;
            }
            #animalHeader {
                float: left;
                width: 215px;
                padding: 0;
                font-size: 11px;
            }
            #projectMapHelp p {
                text-align: justify;
                margin: 1em 0;
            }
            #mapToolForm {
                padding-left:0px;
                padding-top:0px;
            }
            .animalInfo {
                float:left;
                width: 100%;
                font-size: 11px;
                margin-left: 8px;
                margin-right: 8px;
                margin-top: 0;
                margin-bottom: 0;
            }
            
            .animalInfo table {
                margin-top:5px;
            }
            
            .animalInfo .label {
                width:7em;
            }
            .animalLabel {
                float: left;
                width: 105px;
                margin-bottom: 5px;
                font-weight: bold;
                margin-right: 5px;
            }
            .layerInfo {
                margin: 0.5em 0;
            }
            .smallSquare {
                display:block;
                height:14px;
                width:14px;
                float:left;
                margin-right:5px;
                margin-left:5px;
            }
            .citation {
                font-weight: bold;
                text-align: left;
            }
            .animalCheckbox {
                float:left; width:15px;
            }
            .zoom {
                float: right;
            }   
            .animalInfoToggle {
                float:right;
            }
        </style>
        <script src="http://maps.google.com/maps/api/js?v=3.9&sensor=false"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/proj4js/proj4js-compressed.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/OpenLayers.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/openlayers/LoadingPanel.js"></script>
        <script type="text/javascript" src="<c:url value="/js/project-analysis.js"/>"></script>
        <script type="text/javascript">
            function updateParamTable(mapQueryType) {
                $('#paramTable').hide();
                $('#percentRow').hide();
                $('#percent').val('');
                $('#hRow').hide();
                $('#h').val('');
                $('#alphaRow').hide();
                $('#alpha').val('');
                $('#gridSizeRow').hide();
                $('#gridSize').val('');
                if (mapQueryType == 'MCP') {
                    $('#percentRow').show();
                    $('#percent').val('100');
                }
                if (mapQueryType == 'KUD') {
                    $('#percentRow').show();
                    $('#percent').val('95');
                    $('#hRow').show();
                    $('#h').val('href');
                }
                if (mapQueryType == 'AHULL') {
                    $('#alphaRow').show();
                    $('#alpha').val('100');
                }
                if ((mapQueryType == 'HEATMAP_POINT') || (mapQueryType == 'HEATMAP_LINE')) {
                    $('#gridSizeRow').show();
                    $('#gridSize').val('100');
                }
                $('#paramTable').appendTo('#' + mapQueryType).fadeIn('slow');            
            }
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $("#projectMapOptions").accordion({fillSpace: true});
                $('#fromDatepicker').datepicker();
                $('#toDatepicker').datepicker();
                analysisMap = createAnalysisMap('projectMap', {
                    projectId: <c:out value="${project.id}"/>
                });
                srsSelector = createSrsSelector({
                    onSrsSelected: function(id) {
                        jQuery('#projectionCode').val(id);
                    },
                    srsList: [
                        <c:forEach items= "${srsList}" var="srs" varStatus="status">
                        {
                            id: '<c:out value="${srs.identifier}"/>',
                            title: '<c:out value="${srs.title}"/>',
                            bounds: [
                                <c:out value="${srs.bounds.envelopeInternal.minX}"/>,
                                <c:out value="${srs.bounds.envelopeInternal.minY}"/>,
                                <c:out value="${srs.bounds.envelopeInternal.maxX}"/>,
                                <c:out value="${srs.bounds.envelopeInternal.maxY}"/>
                            ]
                        }<c:if test="${not status.last}">,</c:if>
                        </c:forEach>
                    ]
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <a href="<c:url value="/projects"/>">Animal Tracking</a>
        &rsaquo; <a href="<c:url value="/projects/${project.id}"/>">${project.title}</a>
        &rsaquo; <span class="active">Analysis Tools</span>
    </jsp:attribute>
    <jsp:body>
        <div class="mapTool">
		
	    <div id="projectMapOptions">
	
	        <h3 id="projectTitle"><a href="#"><c:out value="${project.title}"/></a></h3>
	
	        <div id="animalPanel">
			
	             <c:forEach items="${projectAnimalsList}" var="animal">
	 		
					<div id="animalHeader">	            	
		            	<div class="animalCheckbox">
		            		<input style="float:left;" type="checkbox" name="animalCheckbox" id="select-animal-${animal.id}" value="${animal.id}">
		                    <script type="text/javascript">
		                        $('input[id=select-animal-${animal.id}]').change(function() {
		                                analysisMap.toggleAllAnimalFeatures("${animal.id}",this.checked);
		                        });
		                    </script>
		            	</div>
	            	
	            		<div class="smallSquare" id="legend-colour-${animal.id}"></div>
	            		
		            	<div class="animalLabel">
		            		${animal.animalName}
		            	</div>
		            	
	            		<div class="animalInfoToggle">
							<a style="" href="#"><span class="ui-icon ui-icon-triangle-1-s"></span></a>
	            		</div>
	
		            	<div class="zoom">
		        			<a href="#" onclick="analysisMap.zoomToAnimal(${animal.id});">Zoom</a>
		            	</div>
	            		
	                </div>
	                <div id="animalInfo-${animal.id}" class="animalInfo">
	                     <a style="float: right; margin-right: 18px; margin-top: 0.5em;" href="<c:url value="/exportKML"><c:param name="projectId" value="${project.id}"/><c:param name="animalId" value="${animal.id}"/></c:url>">KML</a> 
					</div>
	                
	            </c:forEach>
	        </div>
	        
	        
	        <h3><a href="#">Home Range Calculator</a></h3>
	        
		    <div id="homeRangeCalculatorPanel">
		        
		        <form id="mapToolForm" class="form-vertical" method="POST" onsubmit="analysisMap.addProjectMapLayer(); return false;">
                    <input id="projectId" type="hidden" value="${project.id}"/>
                    <fieldset>
                    <div class="control-group" style="margin-bottom: 9px;">
        	            <div style="margin-bottom: 9px; font-weight: bold;">Date Range</div>
                        <div class="controls">
            	            <table>
            		            <tr>
            		                <td style="padding-right: 5px;"><label class="control-label" for="fromDatePicker">From</label></td>
            		                <td><input id="fromDatepicker" type="text" class="input-medium" style="margin: 0;"/></td>
            		            </tr>
            		            <tr>
            		                <td style="padding-right: 5px;"><label class="control-label" for="toDatePicker">To</label></td>
            		                <td><input id="toDatepicker" type="text" class="input-medium" style="margin: 0;"/></td>
            		            </tr>
            	            </table>
                        </div>
					</div>
                    <div class="control-group" style="margin-bottom: 9px;">
                        <div style="margin-bottom: 9px; font-weight: bold;">Layer Type</div>
                        <div class="controls">
        					<table class="mapQueryType" style="margin-top:5px;">
        		                <c:forEach items="${mapQueryTypeList}" var="mapQueryType">
        	                        <tr>
        	                        <td style="padding: 0 5px; vertical-align: top;">
                                        <input type="radio"
                                            name="mapQueryTypeSelect"
                                            id="mapQueryTypeSelect-${mapQueryType}"
                                            value="${mapQueryType}"
                                            onClick="updateParamTable('${mapQueryType}')"
                                        />
                                    </td>
        	                        <td id="${mapQueryType}">
                                        <label style="margin: 0;" for="mapQueryTypeSelect-${mapQueryType}"><c:out value="${mapQueryType.displayName}"/></label>
                                    </td>
        	                        </tr>
        		                </c:forEach>
        	                </table>
                            <table id="paramTable" style="display: none; margin-left:5px;">
                                <tr id="percentRow">
                                    <td>percent:</td>
                                    <td><input id="percent" name="percent" type="text" class="input-mini" style="text-align: right;"/></td>
                                </tr>
                                <tr id="hRow">
                                    <td>h value:</td>
                                    <td><input id="h" name="h" type="text" class="input-mini" style="text-align: right;"/></td>
                                </tr>
                                <tr id="alphaRow">
                                    <td>alpha:</td>
                                    <td><input id="alpha" name="alpha" type="text" class="input-mini" style="text-align: right;"/></td>
                                </tr>
                                <tr id="gridSizeRow">
                                    <td>grid size (m):</td>
                                    <td><input id="gridSize" name="gridSize" type="text" class="input-mini" style="text-align: right;"/></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    <div class="control-group" style="margin-bottom: 9px;">
    					<div style="margin-bottom: 9px; font-weight: bold;">Spatial Reference System (SRS)</div>
                        <div class="controls">
        					<!--
                                Defaults to EPSG:3577 (GDA94 / Australian Albers)
                                Australia-wide geoscience and statistical mapping.
                                Link: http://spatialreference.org/ref/epsg/3577/
                            -->
                            <div>
                                <input id="projectionCode" type="text" class="input-medium" value="EPSG:3577"/>
                            </div>
                            <div>
                                <a href="javascript:void(0)" onclick="srsSelector.showDialog();">Select Australian SRS</a><br>
            					<a href="javascript:void(0)" onclick="window.open('http://spatialreference.org/ref/epsg', 'popup', 'width=600,height=400,scrollbars=yes');">Search for international SRS codes</a>
                            </div>
                        </div>
                    </div>
                    </fieldset>
                    <div style="margin-top: 18px;">
                        <input class="btn btn-primary" type="submit" id="projectMapSubmit" value="Calculate"/>
                    </div>
		          </form>      
		        </div>
	    
				<h3><a href="#">Help</a></h3>
				<div id="projectMapHelp">
				
				  <h2>Trajectories</h2>
				<p>	The trajectory is the animal movement path created from the location fixes in 
				    chronological order.  OzTrack plots the trajectory from the first fix in the 
				    uploaded file, until the last unless the date range is specified. The trajectory 
				    can be viewed on the OzTrack mapping feature and the minimum distance moved by the 
				    animal along this trajectory is calculated and displayed in the navigation window. 
				    This trajectory information can be downloaded in the animal .csv files. 
					Each feature that has been selected is displayed as an overlapping layer on the web 
					browser. Users have the ability to switch between layers to view data, based on 
					animals, date, type for analysis undertaken. 
				</p>		
	
				<h2>Home Range Calculator</h2>
				<p>The home range calculator provides a number of home-range analyses tools to define 
				animal space use. Generally, animals utilise areas disproportionately within their 
				home-range, and these can be identified visualised and quantified within OzTtrack. 
				 Prior to analysis the uploaded animal location data can be filtered  by date range, 
				 study animal or by the precision estimate for each location fix. It returns a 
				 visualisation of the generated home range within the OzTrack viewfinder window and 
				 returns the area (km2) covered by the home range. This information is displayed in 
				 the navigation window or can be downloaded as a .csv file, or the viewfinder can be 
				 saved as a . jpg image. OzTrack provides users with the capacity to overlay and assess 
				 a multitude of home-range plots within the viewfinder, thus optimising the home-range 
				 selection process.
				</p>
				
				<p><b>Add a new layer: </b>Select desired home range analysis tool and <i>Calculate</i> 
				to calculate and project  the home range into the OzTrack viewfinder.</p>
				
				<p><b>Date range: </b>Specify the period of interest for visualisation and home range 
				calculations.</p>
				
				<h2>Minimum Convex polygon</h2>
				<p>
				The MCP is one of the simplest methods for determining home-range size.  This approach 
				uses the smallest area convex set that contains the data. The MCP home-range  is limited 
				as it may contain areas that the animal does not visit. More sophisticated methods of 
				home-range calculation are available,  it is however, the most popular method used. 
				</p>
				<h2>Peeled convex hull</h2>
				<p>
				Convex hull peeling provides a way of ordering the convex hull to remove location 
				outliers which exclude excursive activity and identifies areas of greater use. These 
				areas are probably more representative of refuges and resources. Oztrack provides 
				peeling at the 95% and at the 50% level (i.e. 50% is core home range and contains 
				at most 50% of the data points).
				</p>
				
				<span class="citation">
				Worton, B.J. (1995) A convex hull-based estimator of home-range size. 
				Biometrics, 51, 1206-1215. 
				</span>
				<br>
				
				<h2>Alpha-hull</h2>
				<p>
				The alpha hull is an improvement on the mcp technique. The home range calculated 
				by this technique closer represents space use by the animal by objectively cropping low 
				use areas (i.e. areas which are not visited by tracked animals) out of the mcp.
				</p>
				
				<span class="citation">
				Burgman, M.A. & Fox, J.C. (2003) Bias in species range estimates from minimum 
				convex polygons: implications for conservation and options for improved planning. 
				Animal Conservation, 6, 19-28.
				</span>
				<br>
				
				<h2>Kernel UD</h2>
				<p>
				The kernel density estimator is a non-parametric method of home-range analysis, which uses the 
				utilization distribution to estimate the probability that an animal will be found at a specific 
				geographical location. The major difference between the MCP and the kernel UD (utilization 
				distribution) is that the kernel UD defines the relative frequency of location fixes over time, 
				which the MCP does not. It has therefore been offered as a more accurate means of estimating home range.
				</p>
				<p>
				The kernel UD accurately estimates areas of high use by the tagged animal, providing that the level of 
				smoothing is appropriate.  There are a number of different smoothing parameters that have been adopted 
				in kernel estimates, and no single parameter will perform well in all conditions. OzTrack offers two 
				of the most widely used automatic methods for choosing the smoothing parameter. The first of these is 
				the least-squares cross-validation (LSCV). This is most commonly used but is limited to large sample 
				sizes (&gt; 50), and may result in fragmentation of the home-range. We therefore provide an alternative 
				ad-hoc method where the smoothing parameter value (h) may be set to any given numerical value to alter 
				the home-range size between over-fragmented and the selection of non-use areas.  A large value of h 
				obscures all but the most prominent features, whereas a small h value shows fine detail within the home 
				range.
				</p>
				<p> 
				OzTrack provides the capacity to calculate and view the kernel UD at the 95% and 50% level. These 
				levels are those most commonly adopted as the home-range and core-area UD, respectively. 
				</p>
				
				<span class="citation">
				Seaman, D.E., Powell, R.A. (1996) An evaluation of the accuracy of kernel 
				density estimators for home range analysis. Ecology, Vol. 77, 2075-2085. 
				</span>
				
				<br><br>
				
				<span class="citation">
				Silverman, B.W. (1986) Density estimation for statistics and data analysis. Chapman and Hall, London, UK
				</span>
				
				<br><br>
				
				<span class="citation">
				Worton, B.J. (1989) Kernel methods for estimating the utilization distribution in home-range studies
				</span>
	
				<br><br>
				
				<b>Start and end points</b> 
				<p>
				This highlights the first fix of a track in green and the last fix in red. 
				</p>
				
				<b>To view in GoogleEarth:  </b>
				<p>
					To speed up processing time of the web browser we have opted not to show point files within
                    the web-based browser of OZ-track. IF users wish to view there data as a point file we offer
                    the option to create a kml file  which will show the animal tracks as points as animations
                    within Google Earth. Each point will have date and time information.  
				</p>
				
				</div>
	
	           <h3><a href="#">Project Menu</a></h3>
               <tags:project-menu project="${project}"/>
	    </div>
		
		<div id="projectMap"></div>
		<div style="clear:both;"></div>
		</div>
    </jsp:body>
</tags:page>

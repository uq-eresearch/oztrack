package org.oztrack.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.wms.WebMapServer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContext;
import org.geotools.map.WMSLayer;
import org.geotools.referencing.CRS;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory;
import org.opengis.style.GraphicalSymbol;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.util.MapUtils;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
import org.oztrack.view.AnimalStartEndFeatureBuilder;
import org.oztrack.view.AnimalTrajectoryFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vividsolutions.jts.geom.Polygon;

@Controller
public class ProjectImageController {
    private final String detectionMarkName = "Cross";
    private final double detectionMarkOpacity = 0.8;
    private final int detectionGraphicSize = 8;

    private final double trajectoryLineWidth = 1.0;
    private final double trajectoryLineOpacity = 0.8;

    private final Color borderColour = new Color(0x888888);
    private final double borderWidth = 0.5;
    private final double borderOpacity = 0.6;

    private final String startEndMarkName = "Circle";
    private final double startEndMarkStrokeWidth = 1.2;
    private final double startEndMarkOpacity = 1.0;
    private final Color startMarkColour = new Color(0x00CD00);
    private final Color endMarkColour = new Color(0xCD0000);
    private final int startEndGraphicSize = 4;

    private final Color[] colours = new Color[] {
        new Color(0x8DD3C7),
        new Color(0xFFFFB3),
        new Color(0xBEBADA),
        new Color(0xFB8072),
        new Color(0x80B1D3),
        new Color(0xFDB462),
        new Color(0xB3DE69),
        new Color(0xFCCDE5),
        new Color(0xD9D9D9),
        new Color(0xBC80BD),
        new Color(0xCCEBC5),
        new Color(0xFFED6F)
    };

    private final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    private final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{id}/image", method=RequestMethod.GET, produces="image/png")
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public void getView(
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="mapQueryType", required=false) List<String> mapQueryTypes,
        @RequestParam(value="includeBaseLayer", defaultValue="false") Boolean includeBaseLayer,
        HttpServletResponse response
    ) throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        Polygon projectBoundingBox = projectDao.getBoundingBox(project);
        ReferencedEnvelope mapBounds = new ReferencedEnvelope(projectBoundingBox.getEnvelopeInternal(), CRS.decode("EPSG:4326"));
        Dimension mapDimension = MapUtils.calculateMapDimension(mapBounds, 600);
        MapContext mapContext = new DefaultMapContext();
        mapContext.setAreaOfInterest(mapBounds);
        if (includeBaseLayer) {
            mapContext.addLayer(buildBaseLayer(mapBounds, mapDimension));
        }
        for (String mapQueryTypeString : mapQueryTypes) {
            MapQueryType mapQueryType = MapQueryType.valueOf(mapQueryTypeString);
            SimpleFeatureCollection featureCollection = null;
            Style style = null;
            switch (mapQueryType) {
                case POINTS: {
                    List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                    featureCollection = new AnimalDetectionsFeatureBuilder(positionFixList, true).buildFeatureCollection();
                    style = buildDetectionsStyle(project.getAnimals());
                    break;
                }
                case LINES: {
                    List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                    featureCollection = new AnimalTrajectoryFeatureBuilder(positionFixList).buildFeatureCollection();
                    style = buildTrajectoryStyle(project.getAnimals());
                    break;
                }
                case START_END: {
                    List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                    featureCollection = new AnimalStartEndFeatureBuilder(positionFixList).buildFeatureCollection();
                    style = buildStartEndStyle();
                    break;
                }
                default:
                    throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
            }
            FeatureLayer featureLayer = new FeatureLayer(featureCollection, style);
            mapContext.addLayer(featureLayer);
        }
        MapUtils.writePng(mapContext, mapDimension, response.getOutputStream());
        mapContext.dispose();
    }

    private WMSLayer buildBaseLayer(ReferencedEnvelope mapBounds, Dimension mapDimension) throws Exception {
        URL capabilitiesURL = new URL("http://www.ga.gov.au/wms/getmap?dataset=national&request=getCapabilities");
        String owsLayerName = "politicalpl";
        WebMapServer wms = new WebMapServer(capabilitiesURL);
        org.geotools.data.ows.Layer owsLayer = new org.geotools.data.ows.Layer(owsLayerName);
        for (org.geotools.data.ows.Layer layer : wms.getCapabilities().getLayerList()) {
            if (layer.getName() != null && layer.getName().equals(owsLayerName)) {
                owsLayer = layer;
                break;
            }
        }
        WMSLayer wmsLayer = new WMSLayer(wms, owsLayer);
        return wmsLayer;
    }

    private Style buildDetectionsStyle(List<Animal> animals) {
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        for (Animal animal : animals) {
            featureTypeStyle.rules().add(buildAnimalDetectionRule(animal));
        }
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private Rule buildAnimalDetectionRule(Animal animal) {
        Color colour = colours[(int) (animal.getId() % colours.length)];
        GraphicalSymbol mark = styleFactory.mark(
            filterFactory.literal(detectionMarkName),
            styleFactory.createFill(filterFactory.literal(colour), filterFactory.literal(detectionMarkOpacity)),
            styleFactory.createStroke(filterFactory.literal(borderColour), filterFactory.literal(borderWidth), filterFactory.literal(borderOpacity))
        );
        Graphic graphic = styleFactory.graphic(
            Arrays.asList(mark),
            filterFactory.literal(detectionMarkOpacity),
            filterFactory.literal(detectionGraphicSize),
            filterFactory.literal(0),
            styleFactory.anchorPoint(filterFactory.literal(0), filterFactory.literal(0)),
            styleFactory.displacement(filterFactory.literal(0), filterFactory.literal(0))
        );
        Symbolizer symbolizer = styleFactory.createPointSymbolizer(graphic, null);
        Rule rule = styleFactory.createRule();
        rule.setFilter(filterFactory.equals(filterFactory.property("animalId"), filterFactory.literal(animal.getId())));
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    private Style buildTrajectoryStyle(List<Animal> animals) {
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        featureTypeStyle.rules().add(buildAnimalTrajectoryBorderRule());
        for (Animal animal : animals) {
            featureTypeStyle.rules().add(buildAnimalTrajectoryRule(animal));
        }
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private Rule buildAnimalTrajectoryRule(Animal animal) {
        Color colour = colours[(int) (animal.getId() % colours.length)];
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(colour), filterFactory.literal(trajectoryLineWidth), filterFactory.literal(trajectoryLineOpacity));
        LineSymbolizer symbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.setFilter(filterFactory.equals(filterFactory.property("animalId"), filterFactory.literal(animal.getId())));
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    private Rule buildAnimalTrajectoryBorderRule() {
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(borderColour), filterFactory.literal(trajectoryLineWidth + (borderWidth * 2)), filterFactory.literal(borderOpacity));
        LineSymbolizer symbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    private Style buildStartEndStyle() {
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle(new Rule[] {
            buildStartEndRule("start", startMarkColour),
            buildStartEndRule("end", endMarkColour)
        });
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private Rule buildStartEndRule(String identifier, Color color) {
        GraphicalSymbol mark = styleFactory.mark(
            filterFactory.literal(startEndMarkName),
            null,
            styleFactory.createStroke(filterFactory.literal(color), filterFactory.literal(startEndMarkStrokeWidth), filterFactory.literal(startEndMarkOpacity))
        );
        Graphic graphic = styleFactory.graphic(
            Arrays.asList(mark),
            filterFactory.literal(startEndMarkOpacity),
            filterFactory.literal(startEndGraphicSize),
            filterFactory.literal(0),
            styleFactory.anchorPoint(filterFactory.literal(0), filterFactory.literal(0)),
            styleFactory.displacement(filterFactory.literal(0), filterFactory.literal(0))
        );
        Symbolizer symbolizer = styleFactory.createPointSymbolizer(graphic, null);
        Rule rule = styleFactory.createRule();
        rule.setFilter(filterFactory.equals(filterFactory.property("identifier"), filterFactory.literal(identifier)));
        rule.symbolizers().add(symbolizer);
        return rule;
    }
}

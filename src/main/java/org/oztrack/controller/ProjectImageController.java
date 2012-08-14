package org.oztrack.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContext;
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

@Controller
public class ProjectImageController {
    private static final Color[] colours = new Color[] {
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
        HttpServletResponse response
    ) throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        ReferencedEnvelope mapBounds = new ReferencedEnvelope(project.getBoundingBox().getEnvelopeInternal(), CRS.decode("EPSG:" + 4326));
        Dimension mapDimension = MapUtils.calculateMapDimension(mapBounds, 600);
        MapContext mapContext = new DefaultMapContext();
        mapContext.setAreaOfInterest(mapBounds);
        for (String mapQueryTypeString : mapQueryTypes) {
            MapQueryType mapQueryType = MapQueryType.valueOf(mapQueryTypeString);
            SimpleFeatureCollection featureCollection = null;
            Style style = null;
            switch (mapQueryType) {
                case POINTS: {
                    List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                    featureCollection = new AnimalDetectionsFeatureBuilder(positionFixList).buildFeatureCollection();
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

    private Style buildDetectionsStyle(List<Animal> animals) {
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle();
        for (Animal animal : animals) {
            featureTypeStyle.rules().add(buildAnimalDetectionRule(animal));
        }
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    public Rule buildAnimalDetectionRule(Animal animal) {
        Color colour = colours[(int) (animal.getId() % colours.length)];
        GraphicalSymbol mark = styleFactory.mark(
            filterFactory.literal("Cross"),
            styleFactory.createFill(filterFactory.literal(colour), filterFactory.literal(0.8)),
            null
        );
    
        Graphic graphic = styleFactory.graphic(
            Arrays.asList(mark),
            filterFactory.literal(1.0),
            filterFactory.literal(8),
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
        for (Animal animal : animals) {
            featureTypeStyle.rules().add(buildAnimalTrajectoryRule(animal));
        }
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    public Rule buildAnimalTrajectoryRule(Animal animal) {
        Color colour = colours[(int) (animal.getId() % colours.length)];
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(colour), filterFactory.literal(1.0), filterFactory.literal(1.0));
        LineSymbolizer symbolizer = styleFactory.createLineSymbolizer(stroke, null);
        Rule rule = styleFactory.createRule();
        rule.setFilter(filterFactory.equals(filterFactory.property("animalId"), filterFactory.literal(animal.getId())));
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    private Style buildStartEndStyle() {
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle(new Rule[] {
            buildStartEndRule("start", new Color(0x00CD00)),
            buildStartEndRule("end", new Color(0xCD0000))
        });
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    private Rule buildStartEndRule(String identifier, Color color) {
        GraphicalSymbol mark = styleFactory.mark(
            filterFactory.literal("Circle"),
            null,
            styleFactory.createStroke(filterFactory.literal(color), filterFactory.literal(1.2), filterFactory.literal(1.0))
        );
    
        Graphic graphic = styleFactory.graphic(
            Arrays.asList(mark),
            filterFactory.literal(1.0),
            filterFactory.literal(4),
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
package org.oztrack.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
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
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
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
public class ProjectMapImageController {
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

    private Dimension mapDimension = new Dimension(600, 450);

    private final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    private final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

    @Autowired
    private OzTrackConfiguration configuration;

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

    @RequestMapping(value="/projects/{id}/map-image", method=RequestMethod.GET, produces="image/png")
    @PreAuthorize("hasPermission(#project, 'read')")
    public void getView(
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="layers", required=false) List<String> layerNames,
        HttpServletResponse response
    ) throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);

        if ((layerNames == null) || layerNames.isEmpty()) {
            layerNames = Arrays.asList("base", "trajectory", "detections", "startEnd");
        }

        ReferencedEnvelope mapBounds = new ReferencedEnvelope(projectDao.getBoundingBox(project, false).getEnvelopeInternal(), CRS.decode("EPSG:4326"));

        // Expand either width or height of bounding box to match aspect ratio of image
        double mapDimensionWidthToHeightRatio = mapDimension.getWidth() / mapDimension.getHeight();
        double mapBoundsWidthToHeightRatio = mapBounds.getWidth() / mapBounds.getHeight();
        if (mapBoundsWidthToHeightRatio < mapDimensionWidthToHeightRatio) {
            mapBounds.expandBy(((mapBounds.getHeight() * mapDimensionWidthToHeightRatio) - mapBounds.getWidth()) / 2d, 0d);
        }
        else {
            mapBounds.expandBy(0d, ((mapBounds.getWidth() / mapDimensionWidthToHeightRatio) - mapBounds.getHeight()) / 2d);
        }

        // Expand bounding box to include margin so points aren't right on edge of image
        double padding = 0.05d;
        mapBounds.expandBy(mapBounds.getWidth() * padding, mapBounds.getHeight() * padding);

        ArrayList<BufferedImage> imageLayers = new ArrayList<BufferedImage>();
        MapContent mapContext = new MapContent();
        mapContext.getViewport().setBounds(mapBounds);
        for (String layerName : layerNames) {
            if (layerName.equals("base")) {
                imageLayers.add(buildBaseLayerImage(mapBounds, mapDimension));
            }
            else if (layerName.equals("trajectory")) {

                mapContext.addLayer(new FeatureLayer(
                    new AnimalTrajectoryFeatureBuilder(positionFixList).buildFeatureCollection(),
                    buildTrajectoryStyle(project.getAnimals())
                ));
            }
            else if (layerName.equals("detections")) {
                mapContext.addLayer(new FeatureLayer(
                    new AnimalDetectionsFeatureBuilder(positionFixList, true).buildFeatureCollection(),
                    buildDetectionsStyle(project.getAnimals())
                ));
            }
            else if (layerName.equals("startEnd")) {
                mapContext.addLayer(new FeatureLayer(
                    new AnimalStartEndFeatureBuilder(positionFixList).buildFeatureCollection(),
                    buildStartEndStyle()
                ));
            }
            else {
                throw new RuntimeException("Unsupported map layer: " + layerName);
            }
        }
        imageLayers.add(MapUtils.getBufferedImage(mapContext, mapDimension));
        mapContext.dispose();

        BufferedImage combinedImage = new BufferedImage(mapDimension.width, mapDimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combinedImage.getGraphics();
        for (BufferedImage image : imageLayers) {
            g.drawImage(image, 0, 0, null);
        }
        ImageIO.write(combinedImage, "PNG", response.getOutputStream());
    }

    private BufferedImage buildBaseLayerImage(ReferencedEnvelope mapBounds, Dimension mapDimension) throws Exception {
        final String geoServerBaseUrl = configuration.getGeoServerLocalUrl();
        final String layerName = "oztrack:gebco_08";
        final String styleName = "oztrack_elevation";
        final String format = "image/png";
        return MapUtils.getWMSLayerImage(geoServerBaseUrl, format, layerName, styleName, mapBounds, mapDimension);
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
        GraphicalSymbol mark = styleFactory.mark(
            filterFactory.literal(detectionMarkName),
            styleFactory.createFill(filterFactory.literal(animal.getColour()), filterFactory.literal(detectionMarkOpacity)),
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
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(animal.getColour()), filterFactory.literal(trajectoryLineWidth), filterFactory.literal(trajectoryLineOpacity));
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

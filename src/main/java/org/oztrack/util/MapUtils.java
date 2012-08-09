package org.oztrack.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.Envelope;
import org.opengis.style.GraphicalSymbol;

public class MapUtils {
    public static Style buildPointStyle(String markName, int size, Color color, double opacity) {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
    
        GraphicalSymbol mark = styleFactory.mark(
            filterFactory.literal(markName),
            styleFactory.createFill(filterFactory.literal(color), filterFactory.literal(opacity)),
            null
        );
    
        Graphic graphic = styleFactory.graphic(
            Arrays.asList(mark),
            filterFactory.literal(1.0),
            filterFactory.literal(size),
            filterFactory.literal(0),
            styleFactory.anchorPoint(filterFactory.literal(0), filterFactory.literal(0)),
            styleFactory.displacement(filterFactory.literal(0), filterFactory.literal(0))
        );
    
        Symbolizer symbolizer = styleFactory.createPointSymbolizer(graphic, null);
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(symbolizer);
        FeatureTypeStyle featureTypeStyle = styleFactory.createFeatureTypeStyle(new Rule[] {rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);
        return style;
    }

    public static Dimension calculateMapDimension(Envelope bounds, int width) {
        return new Dimension(width, (int) Math.round(width * (bounds.getSpan(1) / bounds.getSpan(0))));
    }

    public static void writePng(MapContext mapContext, Dimension mapDimension, OutputStream out) throws IOException { 
        BufferedImage image = new BufferedImage(mapDimension.width, mapDimension.height, BufferedImage.TYPE_4BYTE_ABGR);
        GTRenderer renderer = new StreamingRenderer();
        renderer.setContext(mapContext);
        renderer.paint(image.createGraphics(), new Rectangle(mapDimension), mapContext.getAreaOfInterest());
        ImageIO.write(image, "png", out);
    }
}
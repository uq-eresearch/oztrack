package org.oztrack.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.geometry.Envelope;

public class MapUtils {
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
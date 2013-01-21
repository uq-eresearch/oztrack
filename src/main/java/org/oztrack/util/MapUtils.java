package org.oztrack.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

public class MapUtils {
    public static BufferedImage getBufferedImage(MapContext mapContext, Dimension mapDimension) {
        BufferedImage image = new BufferedImage(mapDimension.width, mapDimension.height, BufferedImage.TYPE_4BYTE_ABGR);
        Map<RenderingHints.Key, Object> renderingHintsMap = new HashMap<RenderingHints.Key, Object>();
        renderingHintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RenderingHints hints = new RenderingHints(renderingHintsMap);
        GTRenderer renderer = new StreamingRenderer();
        renderer.setJava2DHints(hints);
        renderer.setContext(mapContext);
        renderer.paint(image.createGraphics(), new Rectangle(mapDimension), mapContext.getAreaOfInterest());
        return image;
    }

    public static BufferedImage getWMSLayerImage(
        String geoServerBaseUrl,
        String format,
        String layerName,
        String styleName,
        ReferencedEnvelope mapBounds,
        Dimension mapDimension
    ) throws Exception {
        String url = geoServerBaseUrl + "/oztrack/wms";
        url += "?SERVICE=WMS";
        url += "&VERSION=1.1.1";
        url += "&REQUEST=GetMap";
        url += "&FORMAT=" + URLEncoder.encode(format, "UTF-8");
        url += "&LAYERS=" + URLEncoder.encode(layerName, "UTF-8");
        url += "&STYLES=" + URLEncoder.encode(styleName, "UTF-8");
        url += "&SRS=" + URLEncoder.encode(CRS.toSRS(mapBounds.getCoordinateReferenceSystem()), "UTF-8");
        url += "&BBOX=" + String.format("%f,%f,%f,%f", mapBounds.getMinX(), mapBounds.getMinY(), mapBounds.getMaxX(), mapBounds.getMaxY());
        url += "&WIDTH=" + mapDimension.width;
        url += "&HEIGHT=" + mapDimension.height;
        return ImageIO.read(new URL(url));
    }
}
package org.oztrack.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetMapRequest;
import org.geotools.data.wms.response.GetMapResponse;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.ows.ServiceException;
import org.geotools.referencing.CRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.geometry.Envelope;

public class MapUtils {
    public static Dimension calculateMapDimension(Envelope bounds, int width, int height) {
        double heightToWidthRatio = bounds.getSpan(1) / bounds.getSpan(0);
        return
            (heightToWidthRatio > 1)
            ? new Dimension((int) Math.round(height / heightToWidthRatio), height)
            : new Dimension(width, (int) Math.round(width * heightToWidthRatio));
    }

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
        String capabilitiesURL,
        String layerName,
        String styleName,
        ReferencedEnvelope mapBounds,
        Dimension mapDimension
    ) throws IOException, ServiceException, MalformedURLException {
        WebMapServer wms = new WebMapServer(new URL(capabilitiesURL));
        GetMapRequest request = wms.createGetMapRequest();
        request.setFormat("image/png");
        request.setDimensions(mapDimension);
        request.setTransparent(false);
        request.setSRS(CRS.toSRS(mapBounds.getCoordinateReferenceSystem()));
        request.setBBox(mapBounds);
        request.addLayer(layerName, styleName);
        GetMapResponse response = wms.issueRequest(request);
        BufferedImage image = ImageIO.read(response.getInputStream());
        return image;
    }
}
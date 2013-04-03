package org.oztrack.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageUtils {
    public static void resize(
        InputStream originalImageInputStream,
        OutputStream resizedOutputStream,
        Integer maxWidth,
        Integer maxHeight
    )
    throws IOException {
        Image originalImage = ImageIO.read(originalImageInputStream);

        final int width = originalImage.getWidth(null);
        final int height = originalImage.getHeight(null);

        final double widthScale = (maxWidth == null) ? 1d : (maxWidth.doubleValue() / width);
        final double heightScale = (maxHeight == null) ? 1d : (maxHeight.doubleValue() / height);
        final double scale = Math.min(widthScale, heightScale);

        final int scaledWidth = (int) (width * scale);
        final int scaledHeight = (int) (height * scale);

        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledBI.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        ImageIO.write(scaledBI, "jpg", resizedOutputStream);
    }
}

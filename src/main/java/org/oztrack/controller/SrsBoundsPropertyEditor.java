package org.oztrack.controller;

import java.beans.PropertyEditorSupport;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

final class SrsBoundsPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        }
        String[] parts = text.split("\\s*,\\s*");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Bounding box must have four values (minX, minY, maxX, maxY)");
        }
        Double boundsMinX;
        Double boundsMinY;
        Double boundsMaxX;
        Double boundsMaxY;
        try {
            boundsMinX = Double.valueOf(parts[0]);
            boundsMinY = Double.valueOf(parts[1]);
            boundsMaxX = Double.valueOf(parts[2]);
            boundsMaxY = Double.valueOf(parts[3]);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Bounding box coordinates must all be numbers");
        }
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        LinearRing boundsLinearRing = geometryFactory.createLinearRing(new Coordinate[] {
            new Coordinate(boundsMinX, boundsMinY),
            new Coordinate(boundsMaxX, boundsMinY),
            new Coordinate(boundsMaxX, boundsMaxY),
            new Coordinate(boundsMinX, boundsMaxY),
            new Coordinate(boundsMinX, boundsMinY)
        });
        Polygon boundsPolygon = geometryFactory.createPolygon(boundsLinearRing, null);
        setValue(boundsPolygon);
    }

    @Override
    public String getAsText() {
        Polygon polygon = (Polygon) getValue();
        if (polygon == null) {
            return null;
        }
        Envelope envelopeInternal = polygon.getEnvelopeInternal();
        return String.format(
            "%f, %f, %f, %f",
            envelopeInternal.getMinX(),
            envelopeInternal.getMinY(),
            envelopeInternal.getMaxX(),
            envelopeInternal.getMaxY()
        );
    }
}
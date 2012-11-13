package org.oztrack.data.model.types;

public enum MapLayerType {
    LINES("Trajectory"),
    POINTS("Detections"),
    START_END("Start and End Points");

    private final String displayName;

    private MapLayerType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
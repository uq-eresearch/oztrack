package org.oztrack.data.model.types;

public enum MapQueryType {
    PROJECTS("All OzTrack Projects"),
    POINTS("Detections"),
    START_END("Start and End Points"),
    LINES("Trajectory"),
    MCP("Minimum Convex Polygon"),
    KUD("Kernel Utilization Distribution"),
    AHULL("Alpha Hull"),
    HEATMAP_POINT("Heat Map (Point Intensity)"),
    HEATMAP_LINE("Heat Map (Line Intensity)");

    private final String displayName;

    MapQueryType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
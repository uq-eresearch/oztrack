package org.oztrack.data.model.types;

public enum MapQueryType {
    PROJECTS("All OzTrack Projects"),
    POINTS("Detections"),
    START_END("Start and End Points"),
    LINES("Trajectory"),
    MCP("Minimum Convex Polygon"),
    KUD("Kernel Utilization Distribution"),
    AHULL("Alpha Hull");

    private final String displayName;

    MapQueryType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
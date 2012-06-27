package org.oztrack.data.model.types;

public enum ProjectType {
    GPS("GPS Based Telemetry");

    private final String displayName;

    ProjectType(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
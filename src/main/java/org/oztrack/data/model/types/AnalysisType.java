package org.oztrack.data.model.types;

import java.util.Arrays;
import java.util.List;

public enum AnalysisType {
    MCP(
        "Minimum Convex Polygon",
        Arrays.asList(
            new AnalysisParameterType("percent", "Percent", "%", "100", false)
        )
    ),
    KUD(
        "Kernel Utilization Distribution",
        Arrays.asList(
            new AnalysisParameterType("percent", "Percent", "%", "95", false),
            new AnalysisParameterType("h", "h value", null, "href", false),
            new AnalysisParameterType("gridSize", "Grid size", "m", "50", true),
            new AnalysisParameterType("extent", "Extent", null, "1", true)
        )
    ),
    AHULL(
        "Alpha Hull",
        Arrays.asList(
            new AnalysisParameterType("alpha", "Alpha", null, "100", false)
        )
    ),
    HEATMAP_POINT(
        "Heat Map (Point Intensity)",
        Arrays.asList(
            new AnalysisParameterType("gridSize", "Grid size", "m", "100", false)
        )
    ),
    HEATMAP_LINE(
        "Heat Map (Line Intensity)",
        Arrays.asList(
            new AnalysisParameterType("gridSize", "Grid size", "m", "100", false)
        )
    );

    private final String displayName;
    private final List<AnalysisParameterType> parameterTypes;

    private AnalysisType(final String display, List<AnalysisParameterType> parameterTypes) {
        this.displayName = display;
        this.parameterTypes = parameterTypes;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<AnalysisParameterType> getParameterTypes() {
        return parameterTypes;
    }
}
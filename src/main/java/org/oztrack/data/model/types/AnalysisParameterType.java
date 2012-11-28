package org.oztrack.data.model.types;

public class AnalysisParameterType {
    private final String identifier;
    private final String displayName;
    private final String units;
    private final String defaultValue;
    private final boolean advanced;

    public AnalysisParameterType(
        String identifier,
        String displayName,
        String units,
        String defaultValue,
        boolean advanced
    ) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.units = units;
        this.defaultValue = defaultValue;
        this.advanced = advanced;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUnits() {
        return units;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isAdvanced() {
        return advanced;
    }
}

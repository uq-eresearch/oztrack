package org.oztrack.data.model.types;

public class AnalysisResultAttributeType {
    private final String identifier;
    private final String displayName;
    private final String dataType;
    private final String units;
    private final Integer numDecimalPlaces;

    public AnalysisResultAttributeType(
        String identifier,
        String displayName,
        String dataType,
        String units,
        Integer numDecimalPlaces
    ) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.dataType = dataType;
        this.units = units;
        this.numDecimalPlaces = numDecimalPlaces;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getUnits() {
        return units;
    }

    public Integer getNumDecimalPlaces() {
        return numDecimalPlaces;
    }
}

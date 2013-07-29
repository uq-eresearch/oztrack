package org.oztrack.data.model.types;

import org.apache.commons.lang3.StringUtils;

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

    public Class<?> getDataTypeClass() {
        try {
            return Class.forName("java.lang." + StringUtils.capitalize(getDataType()));
        }
        catch (ClassNotFoundException e) {
        }
        return Object.class;
    }

    public String getUnits() {
        return units;
    }

    public Integer getNumDecimalPlaces() {
        return numDecimalPlaces;
    }

    public Object getAttributeValueObject(String value) {
        String stringValue = StringUtils.isNotBlank(value) ? value : null;
        return
            ((stringValue == null) || stringValue.equals("NA") || stringValue.equals("NULL")) ? null
            : getDataType().equals("double") ? Double.valueOf(stringValue)
            : getDataType().equals("boolean") ?  Boolean.valueOf(stringValue)
            : stringValue;
    }
}

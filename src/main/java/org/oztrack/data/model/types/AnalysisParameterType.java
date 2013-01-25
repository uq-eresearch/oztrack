package org.oztrack.data.model.types;

import java.util.List;

public class AnalysisParameterType {
    private final String identifier;
    private final String displayName;
    private final String explanation;
    private final String dataType;
    private final String units;
    private final String defaultValue;
    private final boolean advanced;
    private final List<AnalysisParameterOption> options;

    public AnalysisParameterType(
        String identifier,
        String displayName,
        String explanation,
        String dataType,
        String units,
        String defaultValue,
        boolean advanced,
        List<AnalysisParameterOption> options
    ) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.explanation = explanation;
        this.dataType = dataType;
        this.units = units;
        this.defaultValue = defaultValue;
        this.advanced = advanced;
        this.options = options;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getDataType() {
        return dataType;
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

    public List<AnalysisParameterOption> getOptions() {
        return options;
    }

    public boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        if (options != null) {
            for (AnalysisParameterOption option : options) {
                if (option.getValue().equals(value)) {
                    return true;
                }
            }
            return false;
        }
        if (dataType.equals("double")) {
            try {
                double doubleValue = Double.parseDouble(value);
                return !Double.isNaN(doubleValue) && !Double.isInfinite(doubleValue);
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        if (dataType.equals("boolean")) {
            return value.equals("false") || value.equals("true");
        }
        if (dataType.equals("string")) {
            return true;
        }
        return false;
    }
}

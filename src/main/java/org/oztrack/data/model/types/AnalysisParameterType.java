package org.oztrack.data.model.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AnalysisParameterType {
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
        if (dataType.equals("date")) {
            try {
                isoDateFormat.parse(value);
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public Object getParameterValueObject(String valueString, boolean useDefault) {
        String stringValue =
            StringUtils.isNotBlank(valueString) ? valueString
            : useDefault ? getDefaultValue()
            : null;
        if (stringValue == null) {
            return null;
        }
        if (getDataType().equals("double")) {
            return Double.valueOf(stringValue);
        }
        if (getDataType().equals("boolean")) {
            return Boolean.valueOf(stringValue);
        }
        if (getDataType().equals("date")) {
            try {
                return isoDateFormat.parse(stringValue);
            }
            catch (ParseException e) {
                return null;
            }
        }
        return stringValue;
    }
}

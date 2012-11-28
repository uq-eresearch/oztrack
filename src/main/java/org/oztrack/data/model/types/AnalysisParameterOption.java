package org.oztrack.data.model.types;

public class AnalysisParameterOption {
    private final String value;
    private final String title;

    public AnalysisParameterOption(String value, String title) {
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}

package org.oztrack.data.model.types;

public enum ProjectVisitType {
    SUMMARY("summary page visits"),
    ANALYSIS("analysis page visits"),
    DATA_PAGE("raw data page visits"),
    DATA_DOWNLOAD("raw data downloads");
    
    private String title;
    
    private ProjectVisitType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

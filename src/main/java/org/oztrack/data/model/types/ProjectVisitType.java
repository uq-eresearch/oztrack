package org.oztrack.data.model.types;

public enum ProjectVisitType {
    SUMMARY("summary page"),
    ANALYSIS("analysis page");
    
    private String title;
    
    private ProjectVisitType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

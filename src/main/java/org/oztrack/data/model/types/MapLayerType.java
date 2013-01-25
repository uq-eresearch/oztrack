package org.oztrack.data.model.types;

public enum MapLayerType {
    LINES(
        "Trajectory",
        "The trajectory is the animal movement path created from the location " +
        "fixes in chronological order. Unless the date range has been specified, " +
        "OzTrack plots the trajectory from the first to the last location fix in " +
        "the uploaded file."
    ),
    POINTS(
        "Detections",
        "Detections are a collection of data points containing temporal (i.e. " +
        "the time and date of collection) and spatial data (i.e. the geographical " +
        "coordinates). Unless the date range has been specified, OzTrack plots " +
        "all the detections in the uploaded file."
    ),
    START_END(
        "Start and End Points",
        "This function adds colouration to the points that indicate the start " +
        "(green) and end (red) of the animal track data-set."
    );

    private final String displayName;
    private final String explanation;

    private MapLayerType(String display, String explanation) {
        this.displayName = display;
        this.explanation = explanation;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getExplanation() {
        return explanation;
    }
}
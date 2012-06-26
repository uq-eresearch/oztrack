package org.oztrack.data.model.types;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/08/11
 * Time: 11:53 AM
 * Types to define the functions run in R
 */


public enum MapQueryType {

     ALL_PROJECTS("All OzTrack Projects")
	,ALL_POINTS("All Detections")
    ,ALL_LINES("All Trajectories")
    ,POINTS("Detections")
    ,LINES ("Trajectory")
    ,MCP("Minimum Convex Polygon")
    ,KUD("Kernel Utilization Distribution")
    ,AHULL("Alpha Hull");

    private final String displayName;

    MapQueryType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

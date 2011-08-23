package org.oztrack.data.model.types;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/08/11
 * Time: 11:53 AM
 * Types to define the functions run in R
 */


public enum MapQueryType {

     ALL_POINTS("All Points in this Project")
//    ,POINTS("Points")
//    ,TRAJECTORIES("Trajectories")
    ,MCP100("Multiple Convex Polygon 100%")
    ,MCP95("Multiple Convex Polygon 95%")
    ,MCP50("Multiple Convex Polygon 50%");

    private final String displayName;

    MapQueryType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

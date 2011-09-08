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
    ,ALL_LINES("All Tracks in this Project (as Lines)")
//    ,POINTS("Points")
    ,LINES ("Lines")
    ,MCP100("Minimum Convex Polygon 100%")
    ,MCP95("Minimum Convex Polygon 95%")
    ,MCP50("Minimum Convex Polygon 50%")
    ,CHARHULL100("Home Range Estimation 100% (Delaunay Triangulation)")
    ,CHARHULL95("Home Range Estimation 95% (Delaunay Triangulation)")
    ,CHARHULL50("Home Range Estimation 50% (Delaunay Triangulation)");

    private final String displayName;

    MapQueryType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

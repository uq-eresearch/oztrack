package org.oztrack.data.model.types;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/08/11
 * Time: 11:53 AM
 */
public enum SearchQueryType {

     POINTS("Points")
    ,TRAJECTORIES("Trajectories")
    ,MCP100("Multiple Convex Polygon 100%")
    ,MCP95("Multiple Convex Polygon 95%")
    ,MCP50("Multiple Convex Polygon 50%");

    private final String displayName;

    SearchQueryType(final String display) {
        this.displayName = display;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

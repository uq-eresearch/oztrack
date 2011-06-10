package org.oztrack.data.model.types;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 7/06/11
 * Time: 10:38 AM
 */

public enum ProjectType {

     PASSIVE_ACOUSTIC("Passive Acoustic Telemetry")
    ,ACTIVE_ACOUSTIC("Active Acoustic Telemetry")
    ,GPS("GPS Based Telemetry")
    ,ARGOS("ARGOS Telemetry")
    ,RADIO("Radio Telemetry");


    private final String displayName;

    ProjectType(final String display) {
        this.displayName = display;
    }

/*    @Override
    public String toString() {
        return this.displayName;
    }
*/
    public String getDisplayName() {
        return this.displayName;
    }


}

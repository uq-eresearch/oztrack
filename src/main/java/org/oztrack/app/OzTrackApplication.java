package org.oztrack.app;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:19 AM
 */
public class OzTrackApplication {

    private static OzTrackConfiguration applicationContext;

    public void setApplicationContext(OzTrackConfiguration applicationContext) {
        OzTrackApplication.applicationContext = applicationContext;
    }

    public static OzTrackConfiguration getApplicationContext() {
        return applicationContext;
    }
}

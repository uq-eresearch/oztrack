package org.oztrack.app;

public class OzTrackApplication {
    private static OzTrackConfiguration applicationContext;

    public void setApplicationContext(OzTrackConfiguration applicationContext) {
        OzTrackApplication.applicationContext = applicationContext;
    }

    public static OzTrackConfiguration getApplicationContext() {
        return applicationContext;
    }
}
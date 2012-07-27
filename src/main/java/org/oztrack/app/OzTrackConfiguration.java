package org.oztrack.app;

public interface OzTrackConfiguration {
    String getDataSpaceURL();
    String getDataSpaceUsername();
    String getDataSpacePassword();
    String getDataDir();
    boolean isAafEnabled();
}
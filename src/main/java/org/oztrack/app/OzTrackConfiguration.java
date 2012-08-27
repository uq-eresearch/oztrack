package org.oztrack.app;

public interface OzTrackConfiguration {
    String getDataSpaceURL();
    String getDataSpaceUsername();
    String getDataSpacePassword();
    String getDataDir();
    String getMailServerHostName();
    Integer getMailServerPort();
    String getMailFromName();
    String getMailFromEmail();
    Integer getPasswordResetExpiryDays();
    boolean isAafEnabled();
    String getGoogleAnalyticsTrackingID();
}
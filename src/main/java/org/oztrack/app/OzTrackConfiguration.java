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
    boolean isDataLicencingEnabled();
    String getGoogleAnalyticsTrackingID();
    String getGoogleAnalyticsDomainName();
    String getRecaptchaPublicKey();
    String getRecaptchaPrivateKey();
}
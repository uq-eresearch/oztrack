package org.oztrack.app;

import java.util.Date;

public interface OzTrackConfiguration {
    String getBaseUrl();
    String getGeoServerLocalUrl();
    boolean isDataSpaceEnabled();
    String getDataSpaceUrl();
    String getDataSpaceUsername();
    String getDataSpacePassword();
    String getDataDir();
    String getMailServerHostName();
    Integer getMailServerPort();
    String getMailFromName();
    String getMailFromEmail();
    Integer getPasswordResetExpiryDays();
    boolean isAafEnabled();
    String getRecaptchaPublicKey();
    String getRecaptchaPrivateKey();
    Date getClosedAccessDisableDate();
    Date getNonIncrementalEmbargoDisableDate();
}
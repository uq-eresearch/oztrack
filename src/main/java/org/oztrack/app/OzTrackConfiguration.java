package org.oztrack.app;

import java.util.Date;

public interface OzTrackConfiguration {
    String getBaseUrl();
    Boolean getTestServer();
    String getGeoServerLocalUrl();
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
    Integer getProjectDeleteRestrictedAfterDays();
    Integer getDataFileDeleteRestrictedAfterDays();
    String getRserveLogFile();
    Integer getRserveOomAdj();
    OzTrackOaiPmhConfiguration getOaiPmhConfiguration();
}
package org.oztrack.app;

import java.util.Date;

public interface OzTrackConfiguration {
    String getBaseUrl();
    Boolean getTestServer();
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
    String getRserveLogFile();
    Integer getRserveOomAdj();
    boolean isOaiPmhEnabled();
    String getOaiPmhOaiPmhIdentifierPrefix();
    String getOaiPmhObjectIdentifierPrefix();
    String getOaiPmhRifCsGroup();
    String getOaiPmhRepositoryCreator();
    String getOaiPmhRepositoryServiceTitle();
    String getOaiPmhRepositoryServiceDescription();
    Date getOaiPmhRepositoryServiceCreateDate();
    Date getOaiPmhRepositoryServiceUpdateDate();
    String getOaiPmhOaiPmhServiceTitle();
    String getOaiPmhOaiPmhServiceDescription();
    Date getOaiPmhOaiPmhServiceCreateDate();
    Date getOaiPmhOaiPmhServiceUpdateDate();
    String getOaiPmhOaiPmhServiceAdminEmail();
    String getOaiPmhRepositoryCollectionTitle();
    String getOaiPmhRepositoryCollectionDescription();
    Date getOaiPmhRepositoryCollectionCreateDate();
    Date getOaiPmhRepositoryCollectionUpdateDate();
}
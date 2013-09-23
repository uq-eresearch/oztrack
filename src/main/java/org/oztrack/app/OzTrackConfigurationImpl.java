package org.oztrack.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private String baseUrl;
    private Boolean testServer;
    private String geoServerLocalUrl;
    private boolean dataSpaceEnabled;
    private String dataSpaceUrl;
    private String dataSpaceUsername;
    private String dataSpacePassword;
    private String dataDir;
    private String mailServerHostName;
    private Integer mailServerPort;
    private String mailFromName;
    private String mailFromEmail;
    private Integer passwordResetExpiryDays;
    private boolean aafEnabled;
    private String recaptchaPublicKey;
    private String recaptchaPrivateKey;
    private Date closedAccessDisableDate;
    private Date nonIncrementalEmbargoDisableDate;
    private String rserveLogFile;
    private Integer rserveOomAdj;
    private boolean oaiPmhEnabled;
    private String oaiPmhOaiPmhIdentifierPrefix;
    private String oaiPmhObjectIdentifierPrefix;
    private String oaiPmhRifCsGroup;
    private String oaiPmhRepositoryCreator;
    private String oaiPmhRepositoryServiceTitle;
    private String oaiPmhRepositoryServiceDescription;
    private Date oaiPmhRepositoryServiceCreateDate;
    private Date oaiPmhRepositoryServiceUpdateDate;
    private String oaiPmhOaiPmhServiceTitle;
    private String oaiPmhOaiPmhServiceDescription;
    private Date oaiPmhOaiPmhServiceCreateDate;
    private Date oaiPmhOaiPmhServiceUpdateDate;
    private String oaiPmhOaiPmhServiceAdminEmail;
    private String oaiPmhRepositoryCollectionTitle;
    private String oaiPmhRepositoryCollectionDescription;
    private Date oaiPmhRepositoryCollectionCreateDate;
    private Date oaiPmhRepositoryCollectionUpdateDate;

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Boolean getTestServer() {
        return testServer;
    }

    public void setTestServer(Boolean testServer) {
        this.testServer = testServer;
    }

    @Override
    public String getGeoServerLocalUrl() {
        return geoServerLocalUrl;
    }

    public void setGeoServerLocalUrl(String geoServerLocalUrl) {
        this.geoServerLocalUrl = geoServerLocalUrl;
    }

    @Override
    public boolean isDataSpaceEnabled() {
        return dataSpaceEnabled;
    }

    public void setDataSpaceEnabled(boolean dataSpaceEnabled) {
        this.dataSpaceEnabled = dataSpaceEnabled;
    }

    @Override
    public String getDataSpaceUrl() {
        return dataSpaceUrl;
    }

    public void setDataSpaceUrl(String dataSpaceUrl) {
        this.dataSpaceUrl = dataSpaceUrl;
    }

    @Override
    public String getDataSpaceUsername() {
        return dataSpaceUsername;
    }

    public void setDataSpaceUsername(String dataSpaceUsername) {
        this.dataSpaceUsername = dataSpaceUsername;
    }

    @Override
    public String getDataSpacePassword() {
        return dataSpacePassword;
    }

    public void setDataSpacePassword(String dataSpacePassword) {
        this.dataSpacePassword = dataSpacePassword;
    }

    @Override
    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    @Override
    public String getMailServerHostName() {
        return mailServerHostName;
    }

    public void setMailServerHostName(String mailServerHostName) {
        this.mailServerHostName = mailServerHostName;
    }

    @Override
    public Integer getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(Integer mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    @Override
    public String getMailFromName() {
        return mailFromName;
    }

    public void setMailFromName(String mailFromName) {
        this.mailFromName = mailFromName;
    }

    @Override
    public String getMailFromEmail() {
        return mailFromEmail;
    }

    public void setMailFromEmail(String mailFromEmail) {
        this.mailFromEmail = mailFromEmail;
    }

    @Override
    public Integer getPasswordResetExpiryDays() {
        return passwordResetExpiryDays;
    }

    public void setPasswordResetExpiryDays(Integer passwordResetExpiryDays) {
        this.passwordResetExpiryDays = passwordResetExpiryDays;
    }

    @Override
    public boolean isAafEnabled() {
        return aafEnabled;
    }

    public void setAafEnabled(boolean aafEnabled) {
        this.aafEnabled = aafEnabled;
    }

    @Override
    public String getRecaptchaPublicKey() {
        return recaptchaPublicKey;
    }

    public void setRecaptchaPublicKey(String recaptchaPublicKey) {
        this.recaptchaPublicKey = recaptchaPublicKey;
    }

    @Override
    public String getRecaptchaPrivateKey() {
        return recaptchaPrivateKey;
    }

    public void setRecaptchaPrivateKey(String recaptchaPrivateKey) {
        this.recaptchaPrivateKey = recaptchaPrivateKey;
    }

    @Override
    public Date getClosedAccessDisableDate() {
        return closedAccessDisableDate;
    }

    public void setClosedAccessDisableDate(Date closedAccessDisableDate) {
        this.closedAccessDisableDate = closedAccessDisableDate;
    }

    public void setClosedAccessDisableDateString(String closedAccessDisableDateString) throws ParseException {
        this.closedAccessDisableDate = isoDateTimeFormat.parse(closedAccessDisableDateString);
    }

    @Override
    public Date getNonIncrementalEmbargoDisableDate() {
        return nonIncrementalEmbargoDisableDate;
    }

    public void setNonIncrementalEmbargoDisableDate(Date nonIncrementalEmbargoDisableDate) {
        this.nonIncrementalEmbargoDisableDate = nonIncrementalEmbargoDisableDate;
    }

    public void setNonIncrementalEmbargoDisableDateString(String nonIncrementalEmbargoDisableDateString) throws ParseException {
        this.nonIncrementalEmbargoDisableDate = isoDateTimeFormat.parse(nonIncrementalEmbargoDisableDateString);
    }

    @Override
    public String getRserveLogFile() {
        return rserveLogFile;
    }

    public void setRserveLogFile(String rserveLogFile) {
        this.rserveLogFile = rserveLogFile;
    }

    @Override
    public Integer getRserveOomAdj() {
        return rserveOomAdj;
    }

    public void setRserveOomAdj(Integer rserveOomAdj) {
        this.rserveOomAdj = rserveOomAdj;
    }

    @Override
    public boolean isOaiPmhEnabled() {
        return oaiPmhEnabled;
    }

    public void setOaiPmhEnabled(boolean oaiPmhEnabled) {
        this.oaiPmhEnabled = oaiPmhEnabled;
    }

    @Override
    public String getOaiPmhOaiPmhIdentifierPrefix() {
        return oaiPmhOaiPmhIdentifierPrefix;
    }

    public void setOaiPmhOaiPmhIdentifierPrefix(String oaiPmhOaiPmhIdentifierPrefix) {
        this.oaiPmhOaiPmhIdentifierPrefix = oaiPmhOaiPmhIdentifierPrefix;
    }

    @Override
    public String getOaiPmhObjectIdentifierPrefix() {
        return oaiPmhObjectIdentifierPrefix;
    }

    public void setOaiPmhObjectIdentifierPrefix(String oaiPmhObjectIdentifierPrefix) {
        this.oaiPmhObjectIdentifierPrefix = oaiPmhObjectIdentifierPrefix;
    }

    @Override
    public String getOaiPmhRifCsGroup() {
        return oaiPmhRifCsGroup;
    }

    public void setOaiPmhRifCsGroup(String oaiPmhRifCsGroup) {
        this.oaiPmhRifCsGroup = oaiPmhRifCsGroup;
    }

    @Override
    public String getOaiPmhRepositoryCreator() {
        return oaiPmhRepositoryCreator;
    }

    public void setOaiPmhRepositoryCreator(String oaiPmhRepositoryCreator) {
        this.oaiPmhRepositoryCreator = oaiPmhRepositoryCreator;
    }

    @Override
    public String getOaiPmhRepositoryServiceTitle() {
        return oaiPmhRepositoryServiceTitle;
    }

    public void setOaiPmhRepositoryServiceTitle(String oaiPmhRepositoryServiceTitle) {
        this.oaiPmhRepositoryServiceTitle = oaiPmhRepositoryServiceTitle;
    }

    @Override
    public String getOaiPmhRepositoryServiceDescription() {
        return oaiPmhRepositoryServiceDescription;
    }

    public void setOaiPmhRepositoryServiceDescription(String oaiPmhRepositoryServiceDescription) {
        this.oaiPmhRepositoryServiceDescription = oaiPmhRepositoryServiceDescription;
    }

    @Override
    public Date getOaiPmhRepositoryServiceCreateDate() {
        return oaiPmhRepositoryServiceCreateDate;
    }

    public void setOaiPmhRepositoryServiceCreateDate(Date oaiPmhRepositoryServiceCreateDate) {
        this.oaiPmhRepositoryServiceCreateDate = oaiPmhRepositoryServiceCreateDate;
    }

    public void setOaiPmhRepositoryServiceCreateDateString(String oaiPmhRepositoryServiceCreateDateString) throws ParseException {
        this.oaiPmhRepositoryServiceCreateDate = utcDateTimeFormat.parse(oaiPmhRepositoryServiceCreateDateString);
    }

    @Override
    public Date getOaiPmhRepositoryServiceUpdateDate() {
        return oaiPmhRepositoryServiceUpdateDate;
    }

    public void setOaiPmhRepositoryServiceUpdateDate(Date oaiPmhRepositoryServiceUpdateDate) {
        this.oaiPmhRepositoryServiceUpdateDate = oaiPmhRepositoryServiceUpdateDate;
    }

    public void setOaiPmhRepositoryServiceUpdateDateString(String oaiPmhRepositoryServiceUpdateDateString) throws ParseException {
        this.oaiPmhRepositoryServiceUpdateDate = utcDateTimeFormat.parse(oaiPmhRepositoryServiceUpdateDateString);
    }

    @Override
    public String getOaiPmhOaiPmhServiceTitle() {
        return oaiPmhOaiPmhServiceTitle;
    }

    public void setOaiPmhOaiPmhServiceTitle(String oaiPmhOaiPmhServiceTitle) {
        this.oaiPmhOaiPmhServiceTitle = oaiPmhOaiPmhServiceTitle;
    }

    @Override
    public String getOaiPmhOaiPmhServiceDescription() {
        return oaiPmhOaiPmhServiceDescription;
    }

    public void setOaiPmhOaiPmhServiceDescription(String oaiPmhOaiPmhServiceDescription) {
        this.oaiPmhOaiPmhServiceDescription = oaiPmhOaiPmhServiceDescription;
    }

    @Override
    public Date getOaiPmhOaiPmhServiceCreateDate() {
        return oaiPmhOaiPmhServiceCreateDate;
    }

    public void setOaiPmhOaiPmhServiceCreateDate(Date oaiPmhOaiPmhServiceCreateDate) {
        this.oaiPmhOaiPmhServiceCreateDate = oaiPmhOaiPmhServiceCreateDate;
    }

    public void setOaiPmhOaiPmhServiceCreateDateString(String oaiPmhOaiPmhServiceCreateDateString) throws ParseException {
        this.oaiPmhOaiPmhServiceCreateDate = utcDateTimeFormat.parse(oaiPmhOaiPmhServiceCreateDateString);
    }

    @Override
    public Date getOaiPmhOaiPmhServiceUpdateDate() {
        return oaiPmhOaiPmhServiceUpdateDate;
    }

    public void setOaiPmhOaiPmhServiceUpdateDate(Date oaiPmhOaiPmhServiceUpdateDate) {
        this.oaiPmhOaiPmhServiceUpdateDate = oaiPmhOaiPmhServiceUpdateDate;
    }

    public void setOaiPmhOaiPmhServiceUpdateDateString(String oaiPmhOaiPmhServiceUpdateDateString) throws ParseException {
        this.oaiPmhOaiPmhServiceUpdateDate = utcDateTimeFormat.parse(oaiPmhOaiPmhServiceUpdateDateString);
    }

    @Override
    public String getOaiPmhOaiPmhServiceAdminEmail() {
        return oaiPmhOaiPmhServiceAdminEmail;
    }

    public void setOaiPmhOaiPmhServiceAdminEmail(String oaiPmhOaiPmhServiceAdminEmail) {
        this.oaiPmhOaiPmhServiceAdminEmail = oaiPmhOaiPmhServiceAdminEmail;
    }

    @Override
    public String getOaiPmhRepositoryCollectionTitle() {
        return oaiPmhRepositoryCollectionTitle;
    }

    public void setOaiPmhRepositoryCollectionTitle(String oaiPmhRepositoryCollectionTitle) {
        this.oaiPmhRepositoryCollectionTitle = oaiPmhRepositoryCollectionTitle;
    }

    @Override
    public String getOaiPmhRepositoryCollectionDescription() {
        return oaiPmhRepositoryCollectionDescription;
    }

    public void setOaiPmhRepositoryCollectionDescription(String oaiPmhRepositoryCollectionDescription) {
        this.oaiPmhRepositoryCollectionDescription = oaiPmhRepositoryCollectionDescription;
    }

    @Override
    public Date getOaiPmhRepositoryCollectionCreateDate() {
        return oaiPmhRepositoryCollectionCreateDate;
    }

    public void setOaiPmhRepositoryCollectionCreateDate(Date oaiPmhRepositoryCollectionCreateDate) {
        this.oaiPmhRepositoryCollectionCreateDate = oaiPmhRepositoryCollectionCreateDate;
    }

    @Override
    public Date getOaiPmhRepositoryCollectionUpdateDate() {
        return oaiPmhRepositoryCollectionUpdateDate;
    }

    public void setOaiPmhRepositoryCollectionCreateDateString(String oaiPmhRepositoryCollectionCreateDateString) throws ParseException {
        this.oaiPmhRepositoryCollectionCreateDate = utcDateTimeFormat.parse(oaiPmhRepositoryCollectionCreateDateString);
    }

    public void setOaiPmhRepositoryCollectionUpdateDate(Date oaiPmhRepositoryCollectionUpdateDate) {
        this.oaiPmhRepositoryCollectionUpdateDate = oaiPmhRepositoryCollectionUpdateDate;
    }

    public void setOaiPmhRepositoryCollectionUpdateDateString(String oaiPmhRepositoryCollectionUpdateDateString) throws ParseException {
        this.oaiPmhRepositoryCollectionUpdateDate = utcDateTimeFormat.parse(oaiPmhRepositoryCollectionUpdateDateString);
    }

    public SimpleDateFormat getIsoDateTimeFormat() {
        return isoDateTimeFormat;
    }
}
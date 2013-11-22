package org.oztrack.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private String baseUrl;
    private Boolean testServer;
    private String geoServerLocalUrl;
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
    private OzTrackOaiPmhConfiguration oaiPmhConfiguration;

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
    public OzTrackOaiPmhConfiguration getOaiPmhConfiguration() {
        return oaiPmhConfiguration;
    }

    public void setOaiPmhConfiguration(OzTrackOaiPmhConfiguration oaiPmhConfiguration) {
        this.oaiPmhConfiguration = oaiPmhConfiguration;
    }
}
package org.oztrack.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private String baseURL;
    private String dataSpaceURL;
    private String dataSpaceUsername;
    private String dataSpacePassword;
    private String dataDir;
    private String mailServerHostName;
    private Integer mailServerPort;
    private String mailFromName;
    private String mailFromEmail;
    private Integer passwordResetExpiryDays;
    private boolean aafEnabled;
    private boolean dataLicencingEnabled;
    private String googleAnalyticsTrackingID;
    private String googleAnalyticsDomainName;
    private String recaptchaPublicKey;
    private String recaptchaPrivateKey;
    private Date closedAccessDisableDate;

    @Override
    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public String getDataSpaceURL() {
        return dataSpaceURL;
    }

    public void setDataSpaceURL(String dataSpaceURL) {
        this.dataSpaceURL = dataSpaceURL;
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
    public boolean isDataLicencingEnabled() {
        return dataLicencingEnabled;
    }

    public void setDataLicencingEnabled(boolean dataLicencingEnabled) {
        this.dataLicencingEnabled = dataLicencingEnabled;
    }

    @Override
    public String getGoogleAnalyticsTrackingID() {
        return googleAnalyticsTrackingID;
    }

    public void setGoogleAnalyticsTrackingID(String googleAnalyticsTrackingID) {
        this.googleAnalyticsTrackingID = googleAnalyticsTrackingID;
    }

    @Override
    public String getGoogleAnalyticsDomainName() {
        return googleAnalyticsDomainName;
    }

    public void setGoogleAnalyticsDomainName(String googleAnalyticsDomainName) {
        this.googleAnalyticsDomainName = googleAnalyticsDomainName;
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

    public String getClosedAccessDisableDateString() {
        return isoDateTimeFormat.format(closedAccessDisableDate);
    }

    public void setClosedAccessDisableDateString(String closedAccessDisableDateString) throws ParseException {
        this.closedAccessDisableDate = isoDateTimeFormat.parse(closedAccessDisableDateString);
    }
}
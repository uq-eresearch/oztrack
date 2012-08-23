package org.oztrack.app;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
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
}
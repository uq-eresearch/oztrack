package org.oztrack.app;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private String applicationTitle;
    private String applicationEmail;
    private String version;
    private String uriPrefix;
    private String smtpServer;
    private String dataDir;
    private boolean aafEnabled;
    private String dataSpaceURL;
    private String dataSpaceUsername;
    private String dataSpacePassword;
    private String serverProxyName;

    public void setApplicationTitle(String registryTitle) {
        this.applicationTitle = registryTitle;
    }

    @Override
    public String getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationEmail(String registryEmail) {
        this.applicationEmail = registryEmail;
    }

    @Override
    public String getApplicationEmail() {
        return applicationEmail;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    @Override
    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    @Override
    public String getSmtpServer() {
        return smtpServer;
    }
    
    @Override
    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

	@Override
    public boolean isAafEnabled() {
        return aafEnabled;
    }

    public void setAafEnabled(boolean aafEnabled) {
        this.aafEnabled = aafEnabled;
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
	public String getServerProxyName() {
		return serverProxyName;
	}

	public void setServerProxyName(String serverProxyName) {
		this.serverProxyName = serverProxyName;
	}
}

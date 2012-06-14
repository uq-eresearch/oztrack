package org.oztrack.app;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private String applicationTitle;
    private String applicationEmail;
    private String version;
    private String uriPrefix;
    private String smtpServer;
    private String dataDir;
    private String dataSpaceURL;
    private String dataSpaceUsername;
    private String dataSpacePassword;
    private String serverProxyName;

    public void setApplicationTitle(String registryTitle) {
        this.applicationTitle = registryTitle;
    }

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationEmail(String registryEmail) {
        this.applicationEmail = registryEmail;
    }

    public String getApplicationEmail() {
        return applicationEmail;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getSmtpServer() {
        return smtpServer;
    }
    
    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

	public String getDataSpaceURL() {
		return dataSpaceURL;
	}

	public void setDataSpaceURL(String dataSpaceURL) {
		this.dataSpaceURL = dataSpaceURL;
	}

	public String getDataSpaceUsername() {
		return dataSpaceUsername;
	}

	public void setDataSpaceUsername(String dataSpaceUsername) {
		this.dataSpaceUsername = dataSpaceUsername;
	}

	public String getDataSpacePassword() {
		return dataSpacePassword;
	}

	public void setDataSpacePassword(String dataSpacePassword) {
		this.dataSpacePassword = dataSpacePassword;
	}

	public String getServerProxyName() {
		return serverProxyName;
	}

	public void setServerProxyName(String serverProxyName) {
		this.serverProxyName = serverProxyName;
	}


}

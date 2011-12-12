package org.oztrack.app;

import org.oztrack.data.access.manager.DaoManager;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:19 AM
 */
public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private String applicationTitle;
    private String applicationEmail;
    private String version;
    private String uriPrefix;
    private DaoManager daoManager;
    private String smtpServer;
    private AuthenticationManager authenticationManager;
    private String dataDir;
    private String dataSpaceURL;
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

    public void setDaoManager(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    public DaoManager getDaoManager() {
        return daoManager;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getSmtpServer() {
        return smtpServer;
    }
    
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
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

	public String getServerProxyName() {
		return serverProxyName;
	}

	public void setServerProxyName(String serverProxyName) {
		this.serverProxyName = serverProxyName;
	}


}

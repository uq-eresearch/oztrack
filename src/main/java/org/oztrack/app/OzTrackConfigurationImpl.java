package org.oztrack.app;

public class OzTrackConfigurationImpl implements OzTrackConfiguration {
    private String dataSpaceURL;
    private String dataSpaceUsername;
    private String dataSpacePassword;
    private String dataDir;
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
    public boolean isAafEnabled() {
        return aafEnabled;
    }

    public void setAafEnabled(boolean aafEnabled) {
        this.aafEnabled = aafEnabled;
    }
}
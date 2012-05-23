package org.oztrack.app;

import org.oztrack.data.access.manager.DaoManager;

public interface OzTrackConfiguration {
    String getApplicationTitle();
    String getApplicationEmail();
    String getVersion();
    String getUriPrefix();
    DaoManager getDaoManager();
    String getSmtpServer();
    AuthenticationManager getAuthenticationManager();
    String getDataDir();
    String getDataSpaceURL();
    String getDataSpaceUsername();
    String getDataSpacePassword();
    String getServerProxyName();
}

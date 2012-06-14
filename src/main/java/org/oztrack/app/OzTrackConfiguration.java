package org.oztrack.app;


public interface OzTrackConfiguration {
    String getApplicationTitle();
    String getApplicationEmail();
    String getVersion();
    String getUriPrefix();
    String getSmtpServer();
    String getDataDir();
    String getDataSpaceURL();
    String getDataSpaceUsername();
    String getDataSpacePassword();
    String getServerProxyName();
}

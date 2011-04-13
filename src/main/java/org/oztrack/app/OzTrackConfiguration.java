package org.oztrack.app;

import org.oztrack.data.access.manager.DaoManager;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:23 AM
 */
public interface OzTrackConfiguration {
    String getApplicationTitle();

    String getApplicationEmail();

    String getVersion();

    String getUriPrefix();

    DaoManager getDaoManager();

    String getSmtpServer();
    
    AuthenticationManager getAuthenticationManager();
    
    
}

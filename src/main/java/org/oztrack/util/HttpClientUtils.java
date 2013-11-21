package org.oztrack.util;

import java.net.ProxySelector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;

public class HttpClientUtils {
    /**
     * Applies HTTP proxy settings from JVM: see system properties
     * http.proxyHost, http.proxyPort, and http.nonProxyHosts.
     */
    public static DefaultHttpClient createDefaultHttpClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
            httpClient.getConnectionManager().getSchemeRegistry(),
            ProxySelector.getDefault()
        );
        httpClient.setRoutePlanner(routePlanner);
        return httpClient;
    }
}
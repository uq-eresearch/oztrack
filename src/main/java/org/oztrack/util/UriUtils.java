package org.oztrack.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class UriUtils {
    private static final Logger logger = Logger.getLogger(UriUtils.class);

    /**
     * Check whether the supplied URI is within same web application as that of the request.
     *
     * Requires that the authority and path components of the request URI and supplied URI match.
     *
     * Returns false if <code>uri</code> is <code>null</code>.
     *
     *   foo://username:password@example.com:8042/over/there/index.html?type=a&name=b#nose
     *   \_/   \_______________/ \_________/ \__/\____________________/ \___________/ \__/
     *    |           |               |       |           |                  |         |
     *  scheme     userinfo        hostname  port        path               query   fragment
     *   name  \________________________________/
     *                         |
     *                     authority
     */
    public static boolean isWithinWebApp(HttpServletRequest request, String uri) {
        if (uri == null) {
            return false;
        }
        try {
            URI requestURI = new URI(request.getRequestURL().toString());
            URI refererURI = requestURI.resolve(new URI(uri));
            if (
                refererURI.getAuthority().equals(requestURI.getAuthority()) &&
                refererURI.getPath().startsWith(request.getContextPath())
            ) {
                return true;
            }
        }
        catch (URISyntaxException e) {
            logger.error("Error building redirect URI from referer: " + uri);
        }
        return true;
    }

}

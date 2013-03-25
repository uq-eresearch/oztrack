package org.oztrack.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
    protected final Log logger = LogFactory.getLog(getClass());

    @RequestMapping(value="/login", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getView(
        Model model,
        HttpServletRequest request
    ) {
        model.addAttribute("redirectUrl", getRedirectUrl(request, request.getHeader("Referer")));
        return "login";
    }

    /**
     * Get redirect URL from Referer header if it links within this web application;
     * otherwise, return null. The check for being within this application requires
     * that the authority and path components of the request and referer URIs match.
     *
     *   foo://username:password@example.com:8042/over/there/index.html?type=a&name=b#nose
     *   \_/   \_______________/ \_________/ \__/\____________________/ \___________/ \__/
     *    |           |               |       |           |                  |         |
     *  scheme     userinfo        hostname  port        path               query   fragment
     *   name  \________________________________/
     *                         |
     *                     authority
     */
    private String getRedirectUrl(HttpServletRequest request, String referer) {
        if (referer == null) {
            return null;
        }
        try {
            URI requestURI = new URI(request.getRequestURL().toString());
            URI refererURI = requestURI.resolve(new URI(referer));
            if (
                refererURI.getAuthority().equals(requestURI.getAuthority()) &&
                refererURI.getPath().startsWith(request.getServletContext().getContextPath())
            ) {
                return refererURI.toString();
            }
        }
        catch (URISyntaxException e) {
            logger.error("Error building redirect URI from referer: " + referer);
        }
        return null;
    }
}

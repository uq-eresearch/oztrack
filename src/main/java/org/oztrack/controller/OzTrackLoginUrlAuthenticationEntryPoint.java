package org.oztrack.controller;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class OzTrackLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public OzTrackLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) {
        try {
            String enc = (request.getCharacterEncoding() != null) ? request.getCharacterEncoding() : "UTF-8";
            return getLoginFormUrl() + "?redirect=" + URLEncoder.encode(request.getRequestURI(), enc);
        }
        catch (Exception e) {
            return getLoginFormUrl();
        }
    }
}

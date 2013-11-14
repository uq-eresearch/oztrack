package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Forces HTTPS if the user has an authenticated session.
 */
public class ForceHttpsInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getScheme().equals("http")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (permissionEvaluator.haveAuthenticatedUser(authentication)) {
                StringBuffer originalUrlBuffer = request.getRequestURL();
                if (request.getQueryString() != null) {
                    originalUrlBuffer.append(request.getQueryString());
                }
                String originalUrl = originalUrlBuffer.toString();
                String redirectUrl = originalUrl.replaceFirst("^http:", "https:");
                response.sendRedirect(redirectUrl);
                return false;
            }
        }
        return true;
    }
}
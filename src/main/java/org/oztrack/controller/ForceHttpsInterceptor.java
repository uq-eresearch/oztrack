package org.oztrack.controller;

import java.util.List;

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

    private List<String> paths;

    public ForceHttpsInterceptor(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getScheme().equals("http")) {
            boolean shouldForceHttps = false;
            for (String path : paths) {
                if (request.getRequestURI().matches("^" + request.getContextPath() + path + "$")) {
                    shouldForceHttps = true;
                    break;
                }
            }
            if (!shouldForceHttps) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                shouldForceHttps = permissionEvaluator.haveAuthenticatedUser(authentication);
            }
            if (shouldForceHttps) {
                StringBuffer originalUrlBuffer = request.getRequestURL();
                if (request.getQueryString() != null) {
                    originalUrlBuffer.append("?");
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
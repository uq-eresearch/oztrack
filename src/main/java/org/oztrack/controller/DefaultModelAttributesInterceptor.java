package org.oztrack.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.model.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Sets default model attributes used by OzTrack.
 *
 * <ul>
 *     <li>currentUser: based on user ID obtained from Spring Security</li>
 *     <li>customJs: based on setting from database</li>
 *     <li>testServer: whether this instance of OzTrack is running as a test server</li>
 * </ul>
 */
public class DefaultModelAttributesInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @Autowired
    private SettingsDao settingsDao;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        Map<String, Object> model = modelAndView.getModel();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.put("currentUser", permissionEvaluator.getAuthenticatedUser(authentication));
        Settings settings = settingsDao.getSettings();
        if (StringUtils.isNotBlank(settings.getCustomJs())) {
            model.put("customJs", settings.getCustomJs());
        }
        model.put("testServer", configuration.getTestServer());
    }
}
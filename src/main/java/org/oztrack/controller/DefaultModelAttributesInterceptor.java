package org.oztrack.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Settings;
import org.oztrack.data.model.User;
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
 * </ul>
 */
public class DefaultModelAttributesInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserDao userDao;

    @Autowired
    private SettingsDao settingsDao;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        Map<String, Object> model = modelAndView.getModel();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && authentication.isAuthenticated()) {
            User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
            model.put("currentUser", currentUser);
        }
        Settings settings = settingsDao.getSettings();
        if (StringUtils.isNotBlank(settings.getCustomJs())) {
            model.put("customJs", settings.getCustomJs());
        }
    }
}
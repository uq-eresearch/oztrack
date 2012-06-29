package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.app.Constants;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Sets the "currentUser" model attribute based on user ID obtained from Spring Security.
 */
public class CurrentUserInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserDao userDao;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication != null) && authentication.isAuthenticated()) {
            User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
            modelAndView.getModel().put(Constants.CURRENT_USER, currentUser);
        }
    }
}
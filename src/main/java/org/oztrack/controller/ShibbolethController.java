package org.oztrack.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.util.OzTrackUtil;
import org.oztrack.util.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShibbolethController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value="/login/shibboleth", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    @Transactional
    public String handleLogin(
        @RequestHeader(value="eppn", required=false) String aafId,
        @RequestParam(value="redirect", required=false) String redirectUrl,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        if (!configuration.isAafEnabled()) {
            throw new RuntimeException("AAF authentication is disabled");
        }

        if (logger.isDebugEnabled()) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    logger.debug(headerName + ": " + headerValue);
                }
            }
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                for (String parameterValue : request.getParameterValues(parameterName)) {
                    logger.debug(parameterName + "=" + parameterValue);
                }
            }
        }

        if ((aafId == null) || aafId.isEmpty()) {
            throw new RuntimeException("No AAF credentials were supplied.");
        }

        // Existing user logging-in with previously provided AAF ID
        User existingUser = userDao.getByAafId(aafId);
        if (existingUser != null) {
            SecurityContextHolder.getContext().setAuthentication(OzTrackAuthenticationProvider.buildAuthentication(existingUser));
            existingUser.getLoginDates().add(new Date());
            userDao.save(existingUser);
            if (UriUtils.isWithinWebApp(request, redirectUrl)) {
                return "redirect:" + redirectUrl;
            }
            else {
                return "redirect:/";
            }
        }

        // Existing user already logged in and providing a new AAF ID
        User currentUser = OzTrackUtil.getCurrentUser(SecurityContextHolder.getContext().getAuthentication(), userDao);
        if (currentUser != null) {
            String enc = (response.getCharacterEncoding() != null) ? response.getCharacterEncoding() : null;
            return "redirect:/users/" + currentUser.getId() + "/edit?aafId=" + URLEncoder.encode(aafId, enc);
        }

        // New user registering an account based on their AAF ID
        return "redirect:/users/new";
    }
}
package org.oztrack.controller;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.util.OzTrackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String handleLogin(@RequestHeader(value="eppn", required=false) String aafId) throws Exception {
        if (!configuration.isAafEnabled()) {
            throw new RuntimeException("AAF authentication is disabled");
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
            return "redirect:/";
        }

        // Existing user already logged in and providing a new AAF ID
        User currentUser = OzTrackUtil.getCurrentUser(SecurityContextHolder.getContext().getAuthentication(), userDao);
        if (currentUser != null) {
            return "redirect:/users/" + currentUser.getId() + "/edit";
        }

        // New user registering an account based on their AAF ID
        return "redirect:/users/new";
    }
}
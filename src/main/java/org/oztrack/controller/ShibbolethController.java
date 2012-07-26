package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ShibbolethController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private UserDao userDao;
    
    @RequestMapping(value="/login/shibboleth", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String handleLogin(
        Model model,
        @RequestHeader(value="eppn", required=true) String eppn,
        @RequestHeader(value="cn", required=true) String commonName,
        @RequestHeader(value="o", required=true) String organisation
    ) throws Exception {
        if ((eppn == null) || eppn.isEmpty()) {
            model.addAttribute("errorMessage", "No AAF credentials were supplied.");
        }
        else {
            User user = userDao.getByAafId(eppn);
            if (user != null) {
                SecurityContextHolder.getContext().setAuthentication(OzTrackAuthenticationProvider.authenticate(user));
                model.addAttribute("eppn", eppn);
                model.addAttribute("commonName", commonName);
                model.addAttribute("organisation", organisation);
            }
            else {
                model.addAttribute("errorMessage", "Supplied AAF ID not associated with any OzTrack account.");
            }
        }
        return "shibboleth-login";
    }
}

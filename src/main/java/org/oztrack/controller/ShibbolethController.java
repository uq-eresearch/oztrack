package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
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
        @RequestHeader(value="eppn", required=true) String aafId,
        @RequestHeader(value="title", required=false) String title,
        @RequestHeader(value="givenname", required=false) String givenName,
        @RequestHeader(value="sn", required=false) String surname,
        @RequestHeader(value="description", required=false) String description,
        @RequestHeader(value="mail", required=false) String email,
        @RequestHeader(value="o", required=false) String organisation
    ) throws Exception {
        if (!OzTrackApplication.getApplicationContext().isAafEnabled()) {
            throw new RuntimeException("AAF authentication is disabled");
        }
        if ((aafId == null) || aafId.isEmpty()) {
            throw new RuntimeException("No AAF credentials were supplied.");
        }
        User existingUser = userDao.getByAafId(aafId);
        if (existingUser != null) {
            SecurityContextHolder.getContext().setAuthentication(OzTrackAuthenticationProvider.buildAuthentication(existingUser));
            return "redirect:/";
        }
        else {
            User newUser = new User();
            newUser.setAafId(aafId);
            newUser.setTitle(title);
            newUser.setFirstName(givenName);
            newUser.setLastName(surname);
            newUser.setDataSpaceAgentDescription(description);
            newUser.setEmail(email);
            newUser.setOrganisation(organisation);
            newUser.setUsername(aafId);
            model.addAttribute("user", newUser);
            return "user-form";
        }
    }
}

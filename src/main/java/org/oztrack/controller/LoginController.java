package org.oztrack.controller;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.util.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    AuthenticationManager authenticationManager;

    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String handleGet(
        @RequestHeader(value="Referer", required=false) String referer,
        @RequestParam(value="redirect", required=false) String redirectUrl,
        Model model,
        HttpServletRequest request
    ) {
        model.addAttribute("redirectUrl",
            UriUtils.isWithinWebApp(request, redirectUrl) ? redirectUrl :
            UriUtils.isWithinWebApp(request, referer) ? referer :
            null
        );
        return "login";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String handlePost(
        @RequestParam(value="username", required=false) String username,
        @RequestParam(value="password", required=false) String password,
        @RequestParam(value="redirect", required=false) String redirectUrl,
        Model model,
        HttpServletRequest request
    ) {
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (Exception e) {
            model.addAttribute("username", username);
            model.addAttribute("redirectUrl", redirectUrl);
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
        }
        if (UriUtils.isWithinWebApp(request, redirectUrl)) {
            return "redirect:" + redirectUrl;
        }
        else {
            return "redirect:/";
        }
    }
}

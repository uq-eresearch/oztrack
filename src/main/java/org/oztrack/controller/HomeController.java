package org.oztrack.controller;

import java.io.IOException;

import org.oztrack.data.access.SettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    @Autowired
    private SettingsDao settingsDao;
    
    @ModelAttribute("text")
    public String getText() throws Exception {
        return settingsDao.getSettings().getHomeText();
    }

    @RequestMapping(value="/home", method=RequestMethod.GET)
    public RedirectView redirectOldHomeUrl() throws IOException {
        RedirectView redirectView = new RedirectView("/", true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setExposeModelAttributes(false);
        redirectView.setExposePathVariables(false);
        return redirectView;
    }
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getHomeView() {
        return "home";
    }
}
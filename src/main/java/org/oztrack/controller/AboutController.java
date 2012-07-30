package org.oztrack.controller;

import org.oztrack.data.access.SettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AboutController {
    @Autowired
    private SettingsDao settingsDao;

    @InitBinder("text")
    public void initTextBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("text")
    public String getText() throws Exception {
        return settingsDao.getSettings().getAboutText();
    }

    @RequestMapping(value="/about", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String handleRequest() {
        return "about";
    }
}
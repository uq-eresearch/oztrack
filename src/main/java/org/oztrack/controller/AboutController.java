package org.oztrack.controller;

import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.model.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AboutController {
    @Autowired
    private SettingsDao settingsDao;

    @InitBinder("settings")
    public void initTextBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("settings")
    public Settings getSettings() throws Exception {
        return settingsDao.getSettings();
    }

    @RequestMapping(value="/about", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String handleAboutRequest() {
        return "about";
    }

    @RequestMapping(value="/about/{section:people|publications|software|layers|artwork}", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String handleAboutSectionRequest(@PathVariable("section") String section) {
        return "about-" + section;
    }
}
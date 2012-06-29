package org.oztrack.controller;

import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.model.Settings;
import org.oztrack.validator.SettingsFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SettingsController {
    @Autowired
    private SettingsDao settingsDao;
    
    @ModelAttribute("settings")
    public Settings getSettings() throws Exception {
        return settingsDao.getSettings();
    }

    @RequestMapping(value="/settings", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String handleRequest() {
        return "settings";
    }
    
    @RequestMapping(value="/settings", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processSubmit(
        @ModelAttribute(value="settings") Settings settings,
        BindingResult bindingResult
    ) throws Exception {
        new SettingsFormValidator().validate(settings, bindingResult);
        if (bindingResult.hasErrors()) {
            return "settings";
        }
        settingsDao.update(settings);
        return "settings";
    }
}
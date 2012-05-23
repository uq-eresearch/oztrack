package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.model.Settings;
import org.oztrack.data.model.User;
import org.oztrack.validator.SettingsFormValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SettingsController {
    @ModelAttribute("settings")
    public Settings getSettings() throws Exception {
        SettingsDao settingsDao = OzTrackApplication.getApplicationContext().getDaoManager().getSettingsDao();
        return settingsDao.getSettings();
    }

    @RequestMapping(value="/settings", method=RequestMethod.GET)
    public String handleRequest(HttpSession session) {
        User sessionUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (sessionUser == null || !sessionUser.getAdmin()) {
            return "redirect:login";
        }
        return "settings";
    }
    
    @RequestMapping(value="/settings", method=RequestMethod.POST)
    public String processSubmit(
        HttpSession session,
        @ModelAttribute(value="settings") Settings settings,
        BindingResult bindingResult
    ) throws Exception {
        User sessionUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (sessionUser == null || !sessionUser.getAdmin()) {
            return "redirect:login";
        }
        new SettingsFormValidator().validate(settings, bindingResult);
        if (bindingResult.hasErrors()) {
            return "settings";
        }
        SettingsDao settingsDao = OzTrackApplication.getApplicationContext().getDaoManager().getSettingsDao();
        settingsDao.update(settings);
        return "settings";
    }
}
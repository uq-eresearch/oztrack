package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.app.Constants;
import org.oztrack.data.access.SettingsDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Settings;
import org.oztrack.data.model.User;
import org.oztrack.validator.SettingsFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SettingsController {
    @Autowired
    private SettingsDao settingsDao;
    
    @Autowired
    private UserDao userDao;
    
    @ModelAttribute("settings")
    public Settings getSettings() throws Exception {
        return settingsDao.getSettings();
    }

    @RequestMapping(value="/settings", method=RequestMethod.GET)
    public String handleRequest(HttpSession session) {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        User currentUser = (currentUserId == null) ? null : userDao.getUserById(currentUserId);
        if (currentUser == null || !currentUser.getAdmin()) {
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
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        User currentUser = (currentUserId == null) ? null : userDao.getUserById(currentUserId);
        if (currentUser == null || !currentUser.getAdmin()) {
            return "redirect:login";
        }
        new SettingsFormValidator().validate(settings, bindingResult);
        if (bindingResult.hasErrors()) {
            return "settings";
        }
        settingsDao.update(settings);
        return "settings";
    }
}
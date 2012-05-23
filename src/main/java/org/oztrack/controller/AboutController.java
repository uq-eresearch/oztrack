package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.SettingsDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AboutController {
    @ModelAttribute("aboutText")
    public String getAboutText() throws Exception {
        SettingsDao settingsDao = OzTrackApplication.getApplicationContext().getDaoManager().getSettingsDao();
        return settingsDao.getSettings().getAboutText();
    }

    @RequestMapping(value="/about", method=RequestMethod.GET)
    public String handleRequest(HttpSession session) {
        return "about";
    }
}
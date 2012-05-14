package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.app.Constants;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value={"/","/home"})
public class HomeController {
    @RequestMapping(method=RequestMethod.GET)
    public String getView(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        model.addAttribute(Constants.CURRENT_USER, currentUser);
        return "home";
    }
}

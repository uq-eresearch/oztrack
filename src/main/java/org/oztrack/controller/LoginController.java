package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.validator.LoginFormValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @ModelAttribute("user")
    public User getUser() {
        return new User();
    }
    
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String getFormView() {
        return "login";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String processSubmit(
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes,
        @ModelAttribute(value="user") User user,
        BindingResult bindingResult
    ) throws Exception {
        logger.info("Login User: " + user.getUsername());
        new LoginFormValidator().validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login";
        }        
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User currentUser = userDao.getByUsername(user.getUsername());
        session.setAttribute(Constants.CURRENT_USER, currentUser);
        return "redirect:projects";
    }
    
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String getLogoutView(HttpSession session) {
        session.invalidate();
        return "home";
    }
}
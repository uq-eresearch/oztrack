package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.validator.RegisterFormValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.uq.itee.maenad.util.BCrypt;

@Controller
public class RegisterFormController {
    protected final Log logger = LogFactory.getLog(getClass());

    @ModelAttribute("user")
    public User getUser(
        @RequestParam(value="user", required=false) String username,
        @RequestParam(value="update", defaultValue="false") boolean update
    ) throws Exception {
        if (!update || username == null) {
            return new User();
        }
        else {
            UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
            return userDao.getByUsername(username);
        }
    }

    @RequestMapping(value="/register", method=RequestMethod.GET)
    public String getFormView(
        HttpSession session,
        @ModelAttribute(value="user") User user,
        @RequestParam(value="update", defaultValue="false") boolean update
    ) {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (update && (currentUser == null || !currentUser.equals(user))) {
            return "redirect:login";
        }
        return "register";
    }
    
    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String onSubmit(
        HttpSession session,
        Model model,
        @ModelAttribute(value="user") User user,
        BindingResult bindingResult,
        @RequestParam(value="update", defaultValue="false") Boolean update
    ) throws Exception {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (update && (currentUser == null || !currentUser.equals(user))) {
            return "redirect:login";
        }
        
        new RegisterFormValidator().validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(passwordHash);
        userDao.save(user);
        logger.debug("register user: " + user.getUsername());

        if (update) {
            return "redirect:projects";
        }
        else {
            return "registersuccess";
        }
     }
}
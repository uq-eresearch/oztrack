package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.validator.RegisterFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterFormController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private UserDao userDao;

    @ModelAttribute("user")
    public User getUser(
        @RequestParam(value="user", required=false) String username,
        @RequestParam(value="update", defaultValue="false") boolean update
    ) throws Exception {
        if (!update || username == null) {
            return new User();
        }
        else {
            return userDao.getByUsername(username);
        }
    }

    @RequestMapping(value="/register", method=RequestMethod.GET)
    public String getFormView(
        @ModelAttribute(value="user") User user,
        @RequestParam(value="update", defaultValue="false") boolean update
    ) {
        User currentUser = getCurrentUser();
        if (update && (currentUser == null || !currentUser.equals(user))) {
            return "redirect:login";
        }
        return "register";
    }
    
    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String onSubmit(
        Model model,
        @ModelAttribute(value="user") User user,
        BindingResult bindingResult,
        @RequestParam(value="update", defaultValue="false") Boolean update
    ) throws Exception {
        User currentUser = getCurrentUser();
        if (update && (currentUser == null || !currentUser.equals(user))) {
            return "redirect:login";
        }
        
        new RegisterFormValidator(userDao).validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (
            (authentication != null) &&
            authentication.isAuthenticated() &&
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))
        ) {
            currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        }
        return currentUser;
    }
}
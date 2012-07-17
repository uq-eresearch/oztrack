package org.oztrack.controller;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.validator.RegisterFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserListController {
    @Autowired
    private UserDao userDao;

    @ModelAttribute("user")
    public User getUser() throws Exception {
        return new User();
    }

    @RequestMapping(value="/users/new", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getFormView(@ModelAttribute(value="user") User user) {
        return "user-form";
    }
    
    @RequestMapping(value="/users", method=RequestMethod.POST)
    @PreAuthorize("permitAll")
    public String onSubmit(
        Model model,
        @ModelAttribute(value="user") User user,
        BindingResult bindingResult
    ) throws Exception {
        new RegisterFormValidator(userDao).validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user-form";
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userDao.save(user);
        return "registersuccess";
    }
}

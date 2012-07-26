package org.oztrack.controller;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.util.OzTrackUtil;
import org.oztrack.validator.RegisterFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserDao userDao;

    @ModelAttribute("user")
    public User getUser(@PathVariable(value="id") Long id) throws Exception {
        return userDao.getUserById(id);
    }

    @RequestMapping(value="/users/{id}/edit", method=RequestMethod.GET)
    public String getEditView(
        @ModelAttribute(value="user") User user,
        @RequestParam(value="update", defaultValue="false") boolean update
    ) {
        User currentUser = OzTrackUtil.getCurrentUser(SecurityContextHolder.getContext().getAuthentication(), userDao);
        if (currentUser == null || !currentUser.equals(user)) {
            return "redirect:/login";
        }
        return "user-form";
    }
    
    @RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
    public String processUpdate(
        Model model,
        @ModelAttribute(value="user") User user,
        BindingResult bindingResult
    ) throws Exception {
        User currentUser = OzTrackUtil.getCurrentUser(SecurityContextHolder.getContext().getAuthentication(), userDao);
        if (currentUser == null || !currentUser.equals(user)) {
            return "redirect:/login";
        }
        new RegisterFormValidator(userDao).validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user-form";
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userDao.save(user);
        return "redirect:/projects";
    }
}

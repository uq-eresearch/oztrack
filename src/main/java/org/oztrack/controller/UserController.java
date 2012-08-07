package org.oztrack.controller;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.util.OzTrackUtil;
import org.oztrack.validator.UserFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserDao userDao;

    @InitBinder("user")
    public void initUserBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "username",
            "password",
            "title",
            "firstName",
            "lastName",
            "dataSpaceAgentDescription",
            "organisation",
            "email"
        );
    }

    @ModelAttribute("user")
    public User getUser(@PathVariable(value="id") Long id) throws Exception {
        return userDao.getById(id);
    }

    @RequestMapping(value="/users/{id}/edit", method=RequestMethod.GET)
    public String getEditView(
        @ModelAttribute(value="user") User user,
        @RequestHeader(value="eppn", required=false) String aafIdHeader,
        @RequestParam(value="update", defaultValue="false") boolean update
    ) {
        User currentUser = OzTrackUtil.getCurrentUser(SecurityContextHolder.getContext().getAuthentication(), userDao);
        if (currentUser == null || !currentUser.equals(user)) {
            return "redirect:/login";
        }
        if (OzTrackApplication.getApplicationContext().isAafEnabled()) {
            if (StringUtils.isBlank(user.getAafId()) && StringUtils.isNotBlank(aafIdHeader)) {
                user.setAafId(aafIdHeader);
            }
        }
        return "user-form";
    }
    
    @RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
    public String processUpdate(
        Model model,
        @ModelAttribute(value="user") User user,
        @RequestHeader(value="eppn", required=false) String aafIdHeader,
        @RequestParam(value="aafId", required=false) String aafIdParam,
        BindingResult bindingResult
    ) throws Exception {
        User currentUser = OzTrackUtil.getCurrentUser(SecurityContextHolder.getContext().getAuthentication(), userDao);
        if (currentUser == null || !currentUser.equals(user)) {
            return "redirect:/login";
        }
        if (OzTrackApplication.getApplicationContext().isAafEnabled()) {
            if (StringUtils.isBlank(aafIdParam)) {
                user.setAafId(null);
            }
            else if (StringUtils.equals(aafIdHeader, aafIdParam)) {
                user.setAafId(aafIdHeader);
            }
            else {
                throw new RuntimeException("Attempt to set AAF ID without being logged in");
            }
        }
        new UserFormValidator(userDao).validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "user-form";
        }
        if (StringUtils.isBlank(user.getPassword())) {
            user.setPassword(null);
        }
        else {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        userDao.save(user);
        return "redirect:/projects";
    }
}

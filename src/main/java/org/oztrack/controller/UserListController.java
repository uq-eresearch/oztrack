package org.oztrack.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.validator.UserFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserListController {
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
    public User getUser(
        @RequestHeader(value="eppn", required=false) String aafId,
        @RequestHeader(value="title", required=false) String title,
        @RequestHeader(value="givenname", required=false) String givenName,
        @RequestHeader(value="sn", required=false) String surname,
        @RequestHeader(value="description", required=false) String description,
        @RequestHeader(value="mail", required=false) String email,
        @RequestHeader(value="o", required=false) String organisation
    )
    throws Exception {
        User newUser = new User();
        if (OzTrackApplication.getApplicationContext().isAafEnabled()) {
            newUser.setAafId(aafId);
            newUser.setTitle(title);
            newUser.setFirstName(givenName);
            newUser.setLastName(surname);
            newUser.setDataSpaceAgentDescription(description);
            newUser.setEmail(email);
            newUser.setOrganisation(organisation);
            if (aafId != null) {
                if (aafId.contains("@")) {
                    newUser.setUsername(aafId.substring(0, aafId.indexOf("@")));
                }
                else {
                    newUser.setUsername(aafId);
                }
            }
        }
        return newUser;
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
        @RequestHeader(value="eppn", required=false) String aafIdHeader,
        @RequestParam(value="aafId", required=false) String aafIdParam,
        BindingResult bindingResult
    ) throws Exception {
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
        SecurityContextHolder.getContext().setAuthentication(OzTrackAuthenticationProvider.buildAuthentication(user));
        return "redirect:/";
    }

    @RequestMapping(value="/users/search", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("permitAll")
    public void getSearchJson(@RequestParam(value="term") String term, HttpServletResponse response) throws JSONException, IOException {
        List<User> users = userDao.search(term);
        JSONWriter out = new JSONWriter(response.getWriter());
        out.array();
        for (User user : users) {
            out.object();
            out.key("id").value(user.getId());
            out.key("value").value(user.getFirstName() + " " + user.getLastName());
            out.key("label").value(user.getFirstName() + " " + user.getLastName() + " " + "(" + user.getUsername() + ")");
            out.endObject();
        }
        out.endArray();
    }
}

package org.oztrack.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.CountryDao;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Country;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.User;
import org.oztrack.validator.UserFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private OzTrackConfiguration configuration;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private InstitutionDao institutionDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private OaiPmhRecordDao oaiPmhRecordDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("user")
    public void initUserBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "username",
            "title",
            "firstName",
            "lastName",
            "description",
            "institutions",
            "country",
            "email"
        );
        binder.registerCustomEditor(List.class, "institutions", new InstitutionsPropertyEditor(institutionDao));
        binder.registerCustomEditor(Country.class, "country", new CountryPropertyEditor(countryDao));
    }

    @ModelAttribute("user")
    public User getUser(@PathVariable(value="id") Long id) throws Exception {
        return userDao.getById(id);
    }

    @RequestMapping(value="/users/{id}/edit", method=RequestMethod.GET)
    public String getEditView(
        Authentication authentication,
        Model model,
        @ModelAttribute(value="user") User user,
        @RequestHeader(value="eppn", required=false) String aafIdHeader,
        @RequestParam(value="aafId", required=false) String aafIdParam
    ) {
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        if (currentUser == null || (!currentUser.equals(user) && (currentUser.getAdmin() != Boolean.TRUE))) {
            return "redirect:/login";
        }
        if (configuration.isAafEnabled()) {
            if (StringUtils.isNotBlank(aafIdParam) && StringUtils.equals(aafIdParam, aafIdHeader)) {
                user.setAafId(aafIdHeader);
            }
        }
        addFormAttributes(model);
        return "user-form";
    }

    @RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
    public String processUpdate(
        Authentication authentication,
        Model model,
        @ModelAttribute(value="user") User user,
        @RequestHeader(value="eppn", required=false) String aafIdHeader,
        @RequestParam(value="aafId", required=false) String aafIdParam,
        @RequestParam(value="password", required=false) String newPassword,
        @RequestParam(value="password2", required=false) String newPassword2,
        @RequestParam(value="institutions", required=false) List<String> institutionIds,
        BindingResult bindingResult
    ) throws Exception {
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        if (currentUser == null || (!currentUser.equals(user) && (currentUser.getAdmin() != Boolean.TRUE))) {
            return "redirect:/login";
        }
        if (OzTrackApplication.getApplicationContext().isAafEnabled()) {
            if (StringUtils.isBlank(aafIdParam)) {
                user.setAafId(null);
            }
            else if (!StringUtils.equals(user.getAafId(), aafIdParam)) {
                if (StringUtils.equals(aafIdHeader, aafIdParam)) {
                    user.setAafId(aafIdHeader);
                }
                else {
                    throw new RuntimeException("Attempt to set AAF ID without being logged in");
                }
            }
        }
        // Catch empty institutions list here: although WebDataBinder will produce new list when
        // at least one institution entered, it will not be triggered for an empty list, leaving old value.
        if (institutionIds == null) {
            user.setInstitutions(Collections.<Institution>emptyList());
        }
        new UserFormValidator(userDao).validate(user, bindingResult);
        if (!StringUtils.equals(newPassword, newPassword2)) {
            bindingResult.rejectValue("password", "error.password.mismatch", "Passwords do not match");
        }
        else if (StringUtils.isBlank(user.getPassword()) && StringUtils.isBlank(user.getAafId())) {
            bindingResult.rejectValue("password", "error.empty.field", "Please enter password");
        }
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "user-form";
        }
        if (StringUtils.isNotBlank(newPassword)) {
            user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }
        user.setUpdateDate(new Date());
        user.setUpdateUser(currentUser);
        userDao.update(user);
        personDao.setInstitutionsIncludeInOaiPmh(user.getPerson());
        oaiPmhRecordDao.updateOaiPmhSets();
        return "redirect:/projects";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("institutions", institutionDao.getAllOrderedByTitle());
        model.addAttribute("countries", countryDao.getAllOrderedByTitle());
    }
}

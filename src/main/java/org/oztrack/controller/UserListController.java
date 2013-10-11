package org.oztrack.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Institution;
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
    private OzTrackConfiguration configuration;

    @Autowired
    private UserDao userDao;

    @Autowired
    private InstitutionDao institutionDao;

    @InitBinder("user")
    public void initUserBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "username",
            "title",
            "firstName",
            "lastName",
            "description",
            "institutions",
            "email"
        );
        binder.registerCustomEditor(List.class, "institutions", new InstitutionsPropertyEditor(institutionDao));
    }

    @ModelAttribute("user")
    public User getUser(
        @RequestHeader(value="eppn", required=false) String aafEppn,
        @RequestHeader(value="title", required=false) String aafTitle,
        @RequestHeader(value="givenname", required=false) String aafGivenName,
        @RequestHeader(value="sn", required=false) String aafSurname,
        @RequestHeader(value="description", required=false) String aafDescription,
        @RequestHeader(value="mail", required=false) String aafEmail,
        @RequestHeader(value="o", required=false) String aafOrganisation
    )
    throws Exception {
        User newUser = new User();
        if (configuration.isAafEnabled()) {
            newUser.setAafId(aafEppn);
            newUser.setTitle(aafTitle);
            newUser.setFirstName(aafGivenName);
            newUser.setLastName(aafSurname);
            newUser.setDescription(aafDescription);
            newUser.setEmail(aafEmail);
            if (StringUtils.isNotBlank(aafOrganisation)) {
                Institution institution = institutionDao.getByTitle(aafOrganisation);
                if (institution != null) {
                    newUser.setInstitutions(Arrays.asList(institution));
                }
            }
            if (aafEppn != null) {
                if (aafEppn.contains("@")) {
                    newUser.setUsername(aafEppn.substring(0, aafEppn.indexOf("@")));
                }
                else {
                    newUser.setUsername(aafEppn);
                }
            }
        }
        return newUser;
    }

    @ModelAttribute("recaptchaHtml")
    public String getRecaptcha() {
        String recaptchaPrivateKey = configuration.getRecaptchaPrivateKey();
        String recaptchaPublicKey = configuration.getRecaptchaPublicKey();
        if (StringUtils.isNotBlank(recaptchaPublicKey) && StringUtils.isNotBlank(recaptchaPrivateKey)) {
            ReCaptcha c = ReCaptchaFactory.newReCaptcha(recaptchaPublicKey, recaptchaPrivateKey, false);
            return c.createRecaptchaHtml(null, null);
        }
        return null;
    }

    @RequestMapping(value="/users/new", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String getFormView(Model model) {
        addFormAttributes(model);
        return "user-form";
    }

    @RequestMapping(value="/users", method=RequestMethod.POST)
    @PreAuthorize("permitAll")
    public String onSubmit(
        HttpServletRequest request,
        Model model,
        @ModelAttribute(value="user") User user,
        @RequestHeader(value="eppn", required=false) String aafIdHeader,
        @RequestParam(value="aafId", required=false) String aafIdParam,
        @RequestParam(value="password", required=false) String password,
        @RequestParam(value="password2", required=false) String password2,
        BindingResult bindingResult
    ) {
        if (configuration.isAafEnabled()) {
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
        if (!StringUtils.equals(password, password2)) {
            bindingResult.rejectValue("password", "error.password.mismatch", "Passwords do not match");
        }
        else if (StringUtils.isBlank(password) && StringUtils.isBlank(user.getAafId())) {
            bindingResult.rejectValue("password", "error.empty.field", "Please enter password");
        }
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "user-form";
        }
        if (user.getAafId() == null) {
            String recaptchaPrivateKey = configuration.getRecaptchaPrivateKey();
            String recaptchaPublicKey = configuration.getRecaptchaPublicKey();
            if (StringUtils.isNotBlank(recaptchaPublicKey) && StringUtils.isNotBlank(recaptchaPrivateKey)) {
                ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
                reCaptcha.setPrivateKey(recaptchaPrivateKey);
                String recaptchaChallenge = request.getParameter("recaptcha_challenge_field");
                String recaptchaResponse = request.getParameter("recaptcha_response_field");
                ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(request.getRemoteAddr(), recaptchaChallenge, recaptchaResponse);
                if (!reCaptchaResponse.isValid()) {
                    addFormAttributes(model);
                    model.addAttribute("recaptchaError", "Verification incorrect - please try again.");
                    return "user-form";
                }
            }
        }
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        }
        user.setCreateDate(new Date());
        user.setCreateUser(null);
        userDao.save(user);
        SecurityContextHolder.getContext().setAuthentication(OzTrackAuthenticationProvider.buildAuthentication(user));
        return "redirect:/";
    }

    @RequestMapping(value="/users/search", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("permitAll")
    public void getSearchJson(@RequestParam(value="term") String term, HttpServletResponse response) throws JSONException, IOException {
        List<User> users = userDao.search(term);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
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

    private void addFormAttributes(Model model) {
        model.addAttribute("institutions", institutionDao.getAllOrderedByTitle());
    }
}

package org.oztrack.controller;

import java.util.Date;

import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Institution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InstitutionListController {
    @Autowired
    private InstitutionDao institutionDao;

    @Autowired
    private UserDao userDao;

    @InitBinder("institution")
    public void initInstitutionBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "title",
            "domainName"
        );
    }

    @ModelAttribute("institution")
    public Institution getInstitution() {
        return new Institution();
    }

    @RequestMapping(value="/institutions", method=RequestMethod.POST)
    public String processCreate(
        Authentication authentication,
        @ModelAttribute(value="institution") Institution institution,
        BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            return "institution-form";
        }
        institution.setCreateDate(new Date());
        if (authentication != null) {
            institution.setCreateUser(userDao.getByUsername((String) authentication.getPrincipal()));
        }
        institutionDao.save(institution);
        return "redirect:/institutions/" + institution.getId();
    }
}
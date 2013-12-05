package org.oztrack.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.CountryDao;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.model.Country;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
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
    private CountryDao countryDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("institution")
    public void initInstitutionBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "title",
            "domainName",
            "country"
        );
        binder.registerCustomEditor(String.class, "domainName", new StringTrimmerEditor(true));
        binder.registerCustomEditor(Country.class, "country", new CountryPropertyEditor(countryDao));
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
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        if (StringUtils.isBlank(institution.getTitle())) {
            throw new RuntimeException("Please enter a title.");
        }
        institution.setCreateDate(new Date());
        institution.setCreateUser(currentUser);
        try {
            institutionDao.save(institution);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not save institution. Check title/domain is not used for another institution.");
        }
        return "redirect:/institutions/" + institution.getId();
    }
}
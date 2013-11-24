package org.oztrack.controller;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.User;
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
public class PersonListController {
    @Autowired
    private PersonDao personDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("person")
    public void initPersonBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "firstName",
            "lastName",
            "email"
        );
    }

    @ModelAttribute("person")
    public Person getPerson() {
        return new Person();
    }

    @RequestMapping(value="/people", method=RequestMethod.POST)
    public String processCreate(
        Authentication authentication,
        @ModelAttribute(value="person") Person person,
        BindingResult bindingResult
    ) throws Exception {
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        if (StringUtils.isBlank(person.getFirstName()) || StringUtils.isBlank(person.getLastName()) || StringUtils.isBlank(person.getEmail())) {
            throw new RuntimeException("Please enter a first name, last name, and email address.");
        }
        person.setUuid(UUID.randomUUID());
        person.setCreateDate(new Date());
        person.setCreateUser(currentUser);
        try {
            personDao.save(person);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not save person.");
        }
        return "redirect:/people/" + person.getId();
    }
}
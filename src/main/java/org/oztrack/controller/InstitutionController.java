package org.oztrack.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.data.access.CountryDao;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.model.Country;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InstitutionController {
    @Autowired
    private InstitutionDao institutionDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private OaiPmhRecordDao oaiPmhRecordDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("institution")
    public void initInstitutionBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "title",
            "domainName",
            "country"
        );
        binder.registerCustomEditor(Country.class, "country", new CountryPropertyEditor(countryDao));
    }

    @ModelAttribute("institution")
    public Institution getInstitution(@PathVariable(value="id") Long id) throws Exception {
        return institutionDao.getById(id);
    }

    @RequestMapping(value="/institutions/{id}", method=RequestMethod.GET, produces="application/json")
    public void getViewJson(@ModelAttribute(value="institution") Institution institution, HttpServletResponse response) throws IOException, JSONException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        out.key("id").value(institution.getId());
        out.key("title").value(institution.getTitle());
        out.key("domainName").value(institution.getDomainName());
        out.endObject();
    }

    @RequestMapping(value="/institutions/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processUpdate(Authentication authentication, @ModelAttribute(value="institution") Institution institution) {
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        Date currentDate = new Date();
        institution.setUpdateDate(currentDate);
        institution.setUpdateUser(currentUser);
        institution.setUpdateDateForOaiPmh(currentDate);
        institutionDao.update(institution);
        oaiPmhRecordDao.updateOaiPmhSets();
        return "redirect:/settings/institutions";
    }

    @RequestMapping(value="/institutions/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void processDelete(@ModelAttribute(value="institution") Institution institution, HttpServletResponse response) {
        institutionDao.delete(institution);
        response.setStatus(204);
    }

    @RequestMapping(value="/institutions/{id}/replace", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processReplace(
        Authentication authentication,
        @ModelAttribute(value="institution") Institution institution,
        @RequestParam("other") Long otherInstitutionId
    ) {
        Institution otherInstitution = institutionDao.getById(otherInstitutionId);
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        Date currentDate = new Date();
        for (Person person : institution.getPeople()) {
            int index = person.getInstitutions().indexOf(institution);
            person.getInstitutions().remove(institution);
            person.getInstitutions().add(index, otherInstitution);
            person.setUpdateDate(currentDate);
            person.setUpdateUser(currentUser);
            person.setUpdateDateForOaiPmh(currentDate);
            personDao.update(person);
            personDao.setInstitutionsIncludeInOaiPmh(person);
        }
        institutionDao.delete(institution);
        oaiPmhRecordDao.updateOaiPmhSets();
        return "redirect:/settings/institutions";
    }
}

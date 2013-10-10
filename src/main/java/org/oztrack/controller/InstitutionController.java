package org.oztrack.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.model.Institution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InstitutionController {
    @Autowired
    private InstitutionDao institutionDao;

    @InitBinder("institution")
    public void initInstitutionBinder(WebDataBinder binder) {
        binder.setAllowedFields();
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

    @RequestMapping(value="/institutions/{id}", method=RequestMethod.DELETE)
    public void processDelete(@ModelAttribute(value="institution") Institution institution, HttpServletResponse response) {
        institutionDao.delete(institution);
        response.setStatus(204);
    }
}

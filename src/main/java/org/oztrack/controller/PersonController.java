package org.oztrack.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PersonController {
    @Autowired
    private PersonDao personDao;

    @InitBinder("person")
    public void initPersonBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("person")
    public Person getPerson(@PathVariable(value="id") Long id) throws Exception {
        return personDao.getById(id);
    }

    @RequestMapping(value="/people/{id}", method=RequestMethod.GET, produces="application/json")
    public void getViewJson(@ModelAttribute(value="person") Person person, HttpServletResponse response) throws IOException, JSONException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        out.key("id").value(person.getId());
        out.key("title").value(person.getTitle());
        out.key("firstName").value(person.getFirstName());
        out.key("lastName").value(person.getLastName());
        out.key("fullName").value(person.getFullName());
        out.key("email").value(person.getEmail());
        out.endObject();
    }
}

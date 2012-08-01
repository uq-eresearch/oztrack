package org.oztrack.controller;

import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.access.SrsDao;
import org.oztrack.data.model.Srs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vividsolutions.jts.geom.Polygon;

@Controller
public class SrsController {
    @Autowired
    private SrsDao srsDao;
    
    @InitBinder("srs")
    public void initSrsBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "identifier",
            "title",
            "bounds"
        );
        binder.registerCustomEditor(Polygon.class, "bounds", new SrsBoundsPropertyEditor());
    }
    
    @ModelAttribute("srs")
    public Srs getSrs(@PathVariable(value="id") Long id) throws Exception {
        return srsDao.getById(id);
    }
    
    @RequestMapping(value="/settings/srs/{id}/edit", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getEditView(@ModelAttribute(value="srs") Srs srs) {
        return "srs-form";
    }
    
    @RequestMapping(value="/settings/srs/{id}", method=RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processUpdate(
        Authentication authentication,
        @ModelAttribute(value="srs") Srs srs,
        BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            return "srs-form";
        }
        srsDao.update(srs);
        return "redirect:/settings/srs";
    }
    
    @RequestMapping(value="/settings/srs/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void processDelete(@ModelAttribute(value="srs") Srs srs, HttpServletResponse response) {
        srsDao.delete(srs);
        response.setStatus(204);
    }
}

package org.oztrack.controller;


import org.oztrack.data.access.SrsDao;
import org.oztrack.data.model.Srs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vividsolutions.jts.geom.Polygon;

@Controller
public class SrsListController {
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
    public Srs getSrs() {
        return new Srs();
    }

    @RequestMapping(value="/settings/srs", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getListView(Model model) {
        model.addAttribute("srsList", srsDao.getAll());
        return "srs-list";
    }
    
    @RequestMapping(value="/settings/srs/new", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getNewView(@ModelAttribute(value="srs") Srs srs) {
        return "srs-form";
    }
    
    @RequestMapping(value="/settings/srs", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processCreate(
        Authentication authentication,
        @ModelAttribute(value="srs") Srs srs,
        BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            return "srs-form";
        }
        srsDao.save(srs);
        return "redirect:/settings/srs";
    }
}

package org.oztrack.controller;

import org.oztrack.data.access.InstitutionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InstitutionSettingsController {
    @Autowired
    private InstitutionDao institutionDao;

    @RequestMapping(value="/settings/institutions", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getListView(Model model) {
        model.addAttribute("institutions", institutionDao.getAll());
        return "institution-settings";
    }
}

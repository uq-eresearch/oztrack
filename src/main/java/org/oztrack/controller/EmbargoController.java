package org.oztrack.controller;

import org.oztrack.app.EmbargoUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EmbargoController {
    @Autowired
    private EmbargoUpdater embargoUpdater;

    @RequestMapping(value="/settings/embargo", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String handleRequest() {
        return "embargo-form";
    }

    @RequestMapping(value="/settings/embargo", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processSubmit() throws Exception {
        embargoUpdater.run();
        return "embargo-form";
    }
}
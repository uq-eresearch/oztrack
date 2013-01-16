package org.oztrack.controller;

import org.oztrack.app.EmbargoNotifier;
import org.oztrack.app.EmbargoUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmbargoController {
    @Autowired
    private EmbargoUpdater embargoUpdater;

    @Autowired
    private EmbargoNotifier embargoNotifier;

    @RequestMapping(value="/settings/embargo", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String handleRequest() {
        return "embargo-form";
    }

    @RequestMapping(value="/settings/embargo", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processSubmit(@RequestParam("action") String action) throws Exception {
        if (action.equals("update")) {
            embargoUpdater.run();
        }
        else if (action.equals("notify")) {
            embargoNotifier.run();
        }
        return "embargo-form";
    }
}
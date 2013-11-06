package org.oztrack.controller;

import org.apache.commons.io.IOUtils;
import org.oztrack.util.RserveConnectionPool;
import org.oztrack.util.RserveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RserveSettingsController {
    @Autowired
    private RserveConnectionPool rserveConnectionPool;

    @RequestMapping(value="/settings/rserve", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String handleRequest(Model model) {
        model.addAttribute("rserveConnectionPool", rserveConnectionPool);
        return "rserve-settings-form";
    }

    @RequestMapping(value="/settings/rserve", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processSubmit(Model model) throws Exception {
        Process process = RserveUtils.stopRserve();
        String err = IOUtils.toString(process.getErrorStream());
        String out = IOUtils.toString(process.getInputStream());
        process.waitFor();
        model.addAttribute("rserveConnectionPool", rserveConnectionPool);
        model.addAttribute("err", err);
        model.addAttribute("out", out);
        return "rserve-settings-form";
    }
}
package org.oztrack.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.oztrack.geoserver.GeoServerUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GeoServerController {
    @Autowired
    private GeoServerUploader geoServerUploader;

    @RequestMapping(value="/settings/geoserver", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String handleRequest() {
        return "geoserver-form";
    }

    @RequestMapping(value="/settings/geoserver", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processSubmit(Model model) throws Exception {
        StringWriter messages = new StringWriter();
        geoServerUploader.upload(new PrintWriter(messages));
        model.addAttribute("messages", messages.toString());
        return "geoserver-form";
    }
}
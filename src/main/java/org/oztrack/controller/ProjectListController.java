package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectListController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String getView(Model model) {
        return "projects";
    }
}
package org.oztrack.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {
    @RequestMapping(value="/home", method=RequestMethod.GET)
    public RedirectView redirectOldHomeUrl() throws IOException {
        return redirectTo("/");
    }
    
    @RequestMapping(value="/projectdescr", method=RequestMethod.GET)
    public RedirectView redirectProjectDescriptionView(@RequestParam(value="id") Long id) {
        return redirectTo("/projects/" + id);
    }

    private RedirectView redirectTo(String url) {
        RedirectView redirectView = new RedirectView(url, true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setExposeModelAttributes(false);
        redirectView.setExposePathVariables(false);
        return redirectView;
    }
}
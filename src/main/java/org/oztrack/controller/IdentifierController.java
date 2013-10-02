package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;

import org.oztrack.util.OaiPmhConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class IdentifierController {
    @RequestMapping(value="/{type:id|record}/**", method=RequestMethod.GET)
    public RedirectView redirectProjectDescriptionView(@PathVariable("type") String type, HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String id = path.substring(("/" + type + "/").length());
        return redirectTo(
            id.equals(OaiPmhConstants.repositoryServiceLocalIdentifier) ? "/" :
            id.equals(OaiPmhConstants.repositoryCollectionLocalIdentifier) ? "/" :
            id.equals(OaiPmhConstants.oaiPmhServiceLocalIdentifier) ? "/oai-pmh" :
            id.equals(OaiPmhConstants.dataManagerPartyLocalIdentifier) ? "/contact" :
            "/" + id
        );
    }

    private RedirectView redirectTo(String url) {
        RedirectView redirectView = new RedirectView(url, true);
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setExposeModelAttributes(false);
        redirectView.setExposePathVariables(false);
        return redirectView;
    }
}

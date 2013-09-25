package org.oztrack.controller;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.util.OaiPmhException;
import org.oztrack.view.OaiPmhListSetsView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements ListSets verb request handling
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets
@Controller
public class OaiPmhListSetsController extends OaiPmhController {
    @RequestMapping(value="/oai-pmh", method=RequestMethod.GET, produces="text/xml", params="verb=ListSets")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);

        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "resumptionToken"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            throw new OaiPmhException("badArgument", "Request includes illegal arguments.");
        }

        @SuppressWarnings("unused")
        String resumptionToken = request.getParameter("resumptionToken");

        // TODO: Check for badResumptionToken (resumptionToken is invalid or expired)
        // TODO: Check for noSetHierarchy (repository does not support sets)

        return new OaiPmhListSetsView();
    }
}
